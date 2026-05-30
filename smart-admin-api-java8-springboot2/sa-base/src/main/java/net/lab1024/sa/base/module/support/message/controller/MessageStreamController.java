package net.lab1024.sa.base.module.support.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.base.common.controller.SupportBaseController;
import net.lab1024.sa.base.common.domain.RequestUser;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.constant.SwaggerTagConst;
import net.lab1024.sa.base.module.support.message.service.MessageStreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * 消息 SSE 推送
 *
 * @author Codex
 * @date 2026/05/30
 */
@RestController
@Tag(name = SwaggerTagConst.Support.MESSAGE)
public class MessageStreamController extends SupportBaseController {

    @Resource
    private MessageStreamService messageStreamService;

    @Operation(summary = "订阅消息实时推送")
    @GetMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        RequestUser user = SmartRequestUtil.getRequestUser();
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return messageStreamService.connect(user.getUserType(), user.getUserId());
    }
}
