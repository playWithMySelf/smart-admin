package net.lab1024.sa.base.module.support.message.service;

import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.module.support.message.domain.MessageEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * 消息 SSE 推送
 *
 * @author Codex
 * @date 2026/05/30
 */
@Service
public class MessageStreamService {

    private static final String SSE_EVENT_REFRESH = "refresh";

    private static final String SSE_EVENT_CONNECTED = "connected";

    private final ConcurrentHashMap<String, CopyOnWriteArraySet<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    /**
     * 建立 SSE 连接
     */
    public SseEmitter connect(UserTypeEnum userType, Long userId) {
        String emitterKey = buildEmitterKey(userType.getValue(), userId);
        SseEmitter emitter = new SseEmitter(0L);
        CopyOnWriteArraySet<SseEmitter> emitterSet = emitterMap.computeIfAbsent(emitterKey, key -> new CopyOnWriteArraySet<>());
        emitterSet.add(emitter);

        emitter.onCompletion(() -> removeEmitter(emitterKey, emitter));
        emitter.onTimeout(() -> removeEmitter(emitterKey, emitter));
        emitter.onError(throwable -> removeEmitter(emitterKey, emitter));

        try {
            emitter.send(SseEmitter.event().name(SSE_EVENT_CONNECTED).data("connected"));
        } catch (IOException e) {
            removeEmitter(emitterKey, emitter);
            throw new RuntimeException("建立消息推送连接失败", e);
        }
        return emitter;
    }

    /**
     * 消息保存后触发刷新。若当前存在事务，则在提交后再推送，避免回滚后误通知。
     */
    public void notifyAfterCommit(List<MessageEntity> messageEntityList) {
        if (CollectionUtils.isEmpty(messageEntityList)) {
            return;
        }

        Runnable sendTask = () -> doNotify(messageEntityList);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendTask.run();
                }
            });
            return;
        }
        sendTask.run();
    }

    /**
     * 心跳，避免中间代理把空闲连接断掉。
     */
    @Scheduled(fixedRate = 25000L)
    public void heartbeat() {
        if (emitterMap.isEmpty()) {
            return;
        }

        List<String> emptyKeyList = new ArrayList<>();
        emitterMap.forEach((emitterKey, emitterSet) -> {
            if (CollectionUtils.isEmpty(emitterSet)) {
                emptyKeyList.add(emitterKey);
                return;
            }
            for (SseEmitter emitter : emitterSet) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (Exception e) {
                    removeEmitter(emitterKey, emitter);
                }
            }
        });

        for (String emptyKey : emptyKeyList) {
            emitterMap.remove(emptyKey);
        }
    }

    private void doNotify(List<MessageEntity> messageEntityList) {
        Set<String> emitterKeySet = messageEntityList.stream()
                .filter(messageEntity -> messageEntity.getReceiverUserType() != null)
                .filter(messageEntity -> messageEntity.getReceiverUserId() != null)
                .map(messageEntity -> buildEmitterKey(messageEntity.getReceiverUserType(), messageEntity.getReceiverUserId()))
                .collect(Collectors.toSet());

        for (String emitterKey : emitterKeySet) {
            CopyOnWriteArraySet<SseEmitter> emitterSet = emitterMap.get(emitterKey);
            if (CollectionUtils.isEmpty(emitterSet)) {
                continue;
            }
            for (SseEmitter emitter : emitterSet) {
                try {
                    emitter.send(SseEmitter.event().name(SSE_EVENT_REFRESH).data("refresh"));
                } catch (Exception e) {
                    removeEmitter(emitterKey, emitter);
                }
            }
        }
    }

    private void removeEmitter(String emitterKey, SseEmitter emitter) {
        CopyOnWriteArraySet<SseEmitter> emitterSet = emitterMap.get(emitterKey);
        if (emitterSet == null) {
            return;
        }
        emitterSet.remove(emitter);
        if (emitterSet.isEmpty()) {
            emitterMap.remove(emitterKey);
        }
    }

    private String buildEmitterKey(Integer userType, Long userId) {
        return userType + ":" + userId;
    }
}
