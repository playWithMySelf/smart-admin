/*
 * 所有常量入口
 *
 * @Author: jw
 * @Date:      2022-09-06 19:58:28
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { FLAG_NUMBER_ENUM, GENDER_ENUM, USER_TYPE_ENUM } from './common-const';
import workitemConst from './business/workitem/workitem-const';
import messageConst from "./support/message-const";

export default {
  FLAG_NUMBER_ENUM,
  GENDER_ENUM,
  USER_TYPE_ENUM,
  ...workitemConst,
  ...messageConst
};
