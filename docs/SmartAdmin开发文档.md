# SmartAdmin 开发文档

> 来源：https://smartadmin.vip/views/doc/standard/basic.html

---


---

## 顶级代码规范


### 高质量代码思想 V3.0

# SmartAdmin 《高质量代码思想 V3.0》

## 序

自2018 年， 发布 **《高质量代码思想V1.0》《Java规范V1.0》** 和 **《前端规范V1.0》**，在这七年里，收到很多好评，截止目前已经有**千余家企业**使用这套代码规范，同时也在结合大家的反馈、技术的发展、以及自身的不断成长中，**一直保持不断迭代更新，同时也保证会一直更新！**

## 高质量代码思想

* **我们推崇高质量的代码，身为开发，代码即利剑，键盘上一套行云流水，宛如侠客，事了拂衣去，深藏身与名。**
* **我们推崇团队的高度配合默契、互相帮助，从不加班，而不是一看到别人的代码就头皮发麻，留其 [996.ICU](https://baike.baidu.com/item/996.ICU) 加班。**
* **。**
* **。**

## 前言

开源的代码规范基于阿里巴巴、华为的开发手册，添加了团队的风格和规范，补充了一些细节。感谢前人的经验和付出，让我们可以有机会站在巨人的肩膀上眺望星辰大海。

* 规范不是为了约束和禁锢大家的创造力，而是为了帮助大家能够在正确的道路上，尽可能的避免踩坑和跑偏。
* 规范可以让我们无论单枪匹马还是与众人同行的时候都能得心应手。
* 规范可以让我们在面对日益变态的需求和做代码接盘侠的时候，更优雅从容。
* 规则并不是完美的，通过约束和禁止在特定情况下的特性，可能会对代码实现造成影响。

**我们制定规则的目的：为了大多数程序员小伙伴可以得到更多的好处，如果在团队实际运作中认为某个规则无法遵循或有更好的做法，希望大家可以共同改进该规范。**

> *引自《阿里规约》的开头片段：现代软件架构的复杂性需要协同开发完成，如何高效地协同呢？无规矩不成方圆，无规范难以协同，比如，制订交通法规表面上是要限制行车权，实际上是保障公众的人身安全，试想如果没有限速，没有红绿灯，谁还敢上路行驶。对软件来说，适当的规范和标准绝不是消灭代码内容的创造性、优雅性，而是限制过度个性化，以一种普遍认可的统一方式一起做事，提升协作效率，降低沟通成本。代码的字里行间流淌的是软件系统的血液，质量的提升是尽可能少踩坑，杜绝踩重复的坑，切实提升系统稳定性，码出质量。*

## 基础规范

### 1.1、什么是好的代码？

在 之前的版本中，我们对好的代码引用了 Kent Beck 的简单设计四原则，大家反馈比较抽象。V3.0 决定用白话来简单讲讲好的代码原则：

* **满足业务需要：代码是来实现业务的，如果业务都实现不了，代码也就没什么价值了**
* **代码尽可能的清晰明了：就是让小白也能看懂你的代码**
* **代码尽可能的少：在保证清晰明了的前提下，能少一行少一行，能少一个类少一个类，能少一行注释少一行注释**
* **代码尽可能复用性和模块化：在保证清晰明了和尽可能少的前提下，能复用的代码尽量复用，能模块的尽量模块**

以上四个原则的重要程度依次降低， 这是**1024 创新实验室**认为好代码的原则，即：简单的、好的、代码

### 1.2、英文单词命名规范

无论前端代码还是后端代码、异或其他代码，都是由一个个单词组成的，所以一个好的单词影响着代码的本身，所以定义如下：

#### 1）合理使用正确的英文单词

很多人认为自己英语不好就命名比较随意，但，一个有道词典或者百度翻译就能解决这件事情，所以单词的命名必须使用正确。曾经遇到过这种起名字的，比如有一个双11业务，要求第一天业务、第二天、第三天业务的区别，有些小伙伴这样起名字：
```java

第一天： di1day
第二天： di2day
第三天： treeday  （三应该是 three，笑喷）
```


以上是真实发生的例子，故定义如下:

* 一定要用英文，且单词正确，不要用汉语拼音（特殊情况除外，比如某些税务系统的特殊科目命名）
* 英文单词一定要使用常用词
* 英文单词要符合业务

#### 2）合理区分名词和动词

在大部分编程语言中， 项目、类Class、数据库、表名、url中的前半部分 等等 一个比较大的范围都是应该用名词，比如java中的`class`、`interface`、`enum` 等等都是名词，比如 `OrderService`。
 而具体的方法名应该是**动词**异或**动名词**，比如方法：创建订单：`createOrder`，查询订单`queryOrder`

#### 3）各个端、数据库、等命名要统一

无论团队里的前端、后端、移动端、数据库、服务器、redis、docker等等一切对于 某个业务或者某个业务单元的命名必须高度保持一致。
```
比如之前遇到过这么一个功能，OA办公系统的`通知`功能，各个端定义为：
- 后端:`notice`, （NoticeController, 表：t_notice）
- 前端用成了`news`， （news-list.vue， /news/news-list）
- 移动端用成了`message`，(MessageFragement)
- 最后再对接的时候，懵逼了，懵了；
```


### 1.3、注释规范

#### 1）注释和代码一样重要

注释是披荆斩棘历经磨难翻越需求这座大山时，留下的踪迹和收获的经验教训，这些宝贵的知识除了证明曾经存在过，也提醒着后来的人们殷鉴不远、继往开来。

注释除了说明作用、逻辑之外。还有一个很重要的原因：当业务逻辑过于复杂，代码过于庞大的时候，注释就变成了一道道美化环境、分离与整理逻辑思路的路标。这是很重要的一点，它能有效得帮助免于陷入代码与业务逻辑的泥沼之中。

正例：
```java

/**
* 开始抽奖方法
* 保存中奖信息、奖励用户积分等
* @param luckDrawDTO
* @return ResponseDTO 返回中奖信息
*/
public ResponseDTO<String> startLuckDraw(LuckDrawDTO luckDrawDTO) {

    // -------------- 1、校验抽奖活动基本信息 ------------------------
    xxx伪代码一顿操作

    // -------------- 2、新增抽奖记录 -------------------------------
    xxx伪代码一顿操作

    // -------------- 3、如果需要消耗积分，则扣除钢镚积分 -------------
    xxx伪代码一顿操作

    // -------------- 4、获取奖品信息，开始翻滚吧 --------------------
    xxx伪代码一顿操作

    return ResponseDTO.succ(luckDrawPrizeVO);
}
```


#### 2）注释和代码的一致性

注释并不是越多越好，当注释过多，维护代码的同时，还需要维护注释，不仅变成了一种负担，也与当初添加注释的初衷背道而驰。

首先：大家应该通过清晰的逻辑架构，好的变量命名来提高代码可读性；需要的时候，才辅以注释说明。注释是为了帮助阅读者快速读懂代码，所以要从读者的角度出发，按需注释。注释内容要简洁、明了、无二义性，信息全面且不冗余。

其次：无论是修改、复制代码时，都要仔细核对注释内容是否正确。只改代码，不改注释是一种不文明行为，破坏了代码与注释的一致性，会让阅读者迷惑、费解，甚至误解。

反例：
```java

// 查询部门
EmployeeVO employee = employeeDao.listByDepartmenttId(deptId);
```


#### 3）方法注释

方法要尽量通过方法名自解释，不要写无用、信息冗余的方法头，不要写空有格式的方法头注释。

方法头注释内容可选，但不限于：功能说明、返回值，用法、算法实现等等。尤其是对外的方法接口声明，其注释，应当将重要、有用的信息表达清楚。

正例：
```java

/**
 * 解析转换时间字符串为 LocalDate 时间类
 * 调用前必须校验字符串格式 否则可能造成解析失败的错误异常
 *
 * @param dateStr 必须是 yyyy-MM-dd 格式的字符串
 * @return LocalDate
 */
public static LocalDate parseYMD(String dateStr){}
```


反例：
```java

/**
 * 校验对象
 *
 * @param t
 * @return String
 */
public static <T> String checkObj(T t);
```


反例中出现的问题：

* 方法注释没有说明具体的作用、使用事项。
* 参数、返回值，空有格式没内容。这是非常重要一点，任何人调用任何方法之前都需要知道方法对参数的要求，以及返回值是什么。

### 1.4、TODO FIX规范

`TODO/TBD(to be determined)` 注释一般用来描述已知待改进、待补充的修改点,并且加上作者名称。
`FIXME` 注释一般用来描述已知缺陷，它们都应该有统一风格，方便文本搜索统一处理。如：
```java

// TODO <author-name>: 补充XX处理
// FIXME <author-name>: XX缺陷
```


举例：
```js

// TODO <卓大> 为了数据安全，此处应该对手机号进行加*处理
```


### 1.5、 无用代码：删！

因为现在所有的项目都使用了代码管理工具，比如 git、svn 、ts等等，所以对于无用的代码，让我们尽情的删除掉吧！
 重要的说三遍：
 不要注释，不要注释，不要注释！
 要删除代码，要删除代码，要删除代码！

### 1.6、 代码Git提交规范

* 提交前应该冷静、仔细检查一下，确保没有忘记加入版本控制或不应该提交的文件。
* 提交前应该先编译一次（idea 里 ctrl+F9），防止出现编译都报错的情况。
* 提交前先更新 pull 一次代码，提交前发生冲突要比提交后发生冲突容易解决的多。
* 提交前检查代码是否格式化，是否符合代码规范，无用的包引入、变量是否清除等等。
* 提交时检查注释是否准确简洁的表达出了本次提交的内容。
* 提交代码时必须填写详细备注，如完成功能，注释为“新增 XX 功能”；
* 若此次提交代码对应禅道中的任务或者 bug，格式如下：
```
task#[任务id] [任务标题] [具体事项]
bug#[bug id] [bug标题] [具体事项]
```


* 例子如下：
```
git commit -m 'task#1101 开发smartreload功能 完成线程池的编码'
git commit -m 'bug#1102 smartreload时间不正确 线程池的大小问题'
```


### 1.7、保持项目整洁

使用 git，必须添加 .gitignore 忽略配置文件。
 不要提交与项目无关的内容文件：idea 配置、target 包等。

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### Vue3规范 V3.0

# SmartAdmin 《Vue3规范 V3.0》

## 阅读须知

 ，[前往阅读](./basic.html)

## 一、Vue3 基础规范

### 1.1、项目命名

全部采用小写方式， 以中划线分隔。
```javascript

正例：`smart-admin`
反例：`mall_management-system / mallManagementSystem`
```


### 1.2、目录、文件命名

目录、文件名 均以 小写方式， 以中划线分隔。
```javascript

正例：`/head-search/`、`/shopping-car/`、`smart-logo.png`、`role-form.vue`
反例：`/headSearch/`、 `smartLogo.png`、 `RoleForm.vue`
```


### 1.3、单引号、双引号、分号

* html中、vue的template 中 标签属性 使用 **双引号**
* 所有js中的 字符串 使用 **单引号**
* 所有js中的代码行换行要用 **分号**

## 二、Vue3 组合式 API规范

### 2.1、 使用setup语法糖

* 组件必须使用 `setup` 语法糖
* setup 大法方便简洁
* 全局都要使用setup语法糖

### 2.2、组合式Composition API 规范

组件内必须使用模块化思想，把代码 进行 拆分；
 参照 vue3官方文档对于 Composition Api的理解： [更灵活的代码组织](https://cn.vuejs.org/guide/extras/composition-api-faq.html#better-logic-reuse) ，组合式Api，即 Composition API 解决的是让 相互关联的代码在一起，以更方便的组织代码，故代码格式如下：
```js

<script setup>
// 各种需要导入
import xxxxx;
import xxxxx;
import xxxxx;

// -------- 定义组件属性和对外暴露的方法、以及抛出的事件 --------

// -------- 表格查询的 变量和方法 --------

// -------- 批量操作的 变量和方法 --------

// -------- 表单的 变量和方法 --------

</script>
```


举例，分成了两个模块，即：

* 模块1：显示 、隐藏操作的 变量和方法
* 模块2：表单的 变量和方法
```js

<script setup>
  import { ref, reactive } from 'vue';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import _ from 'lodash';
  import { categoryApi } from '/@/api/business/category/category-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  // emit
  const emit = defineEmits('reloadList');

  //  组件
  const formRef = ref();

  // ------------------------------ 显示 、隐藏操作的 变量和方法------------------------------

  // 是否展示抽屉
  const visible = ref(false);
  // 显示
  function showModal(categoryType, parentId, rowData) {
    Object.assign(form, formDefault);
    form.categoryType = categoryType;
    form.parentId = parentId;
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    visible.value = true;
  }
  // 隐藏
  function onClose() {
    Object.assign(form, formDefault);
    visible.value = false;
  }

  // ------------------------------ 表单的  变量和方法 ------------------------------
  // 查询表单默认值
  const formDefault = {
    categoryId: undefined, //分类id
    categoryName: '', //分类名字
    categoryType: 1, // 分类类型
    parentId: undefined, // 父级id
    disabledFlag: false, //是否禁用
  };
  // 查询表单
  let form = reactive({ ...formDefault });
  // 表单校验规则
  const rules = {
    categoryName: [{ required: true, message: '请输入分类名称' }],
  };

  function onSubmit() {
    formRef.value
      .validate()
      .then(async () => {
        SmartLoading.show();
        try {
          if (form.categoryId) {
            await categoryApi.updateCategory(form);
          } else {
            await categoryApi.addCategory(form);
          }
          message.success(`${form.categoryId ? '修改' : '添加'}成功`);
          emit('reloadList', form.parentId);
          onClose();
        } catch (error) {
          smartSentry.captureError(error);
        } finally {
          SmartLoading.hide();
        }
      })
      .catch((error) => {
        console.log('error', error);
        message.error('参数验证错误，请仔细填写表单数据!');
      });
  }

  defineExpose({
    showModal,
  });
</script>
```

```js

<script setup>
  import { ref, reactive } from 'vue';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import _ from 'lodash';
  import { categoryApi } from '/@/api/business/category/category-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  // -----------------  定义 所有变量  -----------------

  // emit
  const emit = defineEmits('reloadList');
  //  组件
  const formRef = ref();
  // 是否展示抽屉
  const visible = ref(false);
  // 查询表单默认值
  const formDefault = {
    categoryId: undefined, //分类id
    categoryName: '', //分类名字
    categoryType: 1, // 分类类型
    parentId: undefined, // 父级id
    disabledFlag: false, //是否禁用
  };
  // 查询表单
  let form = reactive({ ...formDefault });
  // 表单校验规则
  const rules = {
    categoryName: [{ required: true, message: '请输入分类名称' }],
  };

  // -----------------  定义 所有方法 -----------------
  // 显示
  function showModal(categoryType, parentId, rowData) {
    Object.assign(form, formDefault);
    form.categoryType = categoryType;
    form.parentId = parentId;
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    visible.value = true;
  }
  // 隐藏
  function onClose() {
    Object.assign(form, formDefault);
    visible.value = false;
  }

  function onSubmit() {
    formRef.value
      .validate()
      .then(async () => {
        SmartLoading.show();
        try {
          if (form.categoryId) {
            await categoryApi.updateCategory(form);
          } else {
            await categoryApi.addCategory(form);
          }
          message.success(`${form.categoryId ? '修改' : '添加'}成功`);
          emit('reloadList', form.parentId);
          onClose();
        } catch (error) {
          smartSentry.captureError(error);
        } finally {
          SmartLoading.hide();
        }
      })
      .catch((error) => {
        console.log('error', error);
        message.error('参数验证错误，请仔细填写表单数据!');
      });
  }

  defineExpose({
    showModal,
  });
</script>
```


|  |
| --- |
|  |

第一种写法，所有变量写到了一起，就像vue2的 data区域，所有方法写到一起，就像vue2中的 methods区域，这样下来其实和vue2没什么区别，也就语法稍微变了一下，那么就与composition api的初衷违背了，下面引用官方的话

> 处理相同逻辑关注点的代码被强制拆分在了不同的选项中，位于文件的不同部分。在一个几百行的大组件中，要读懂代码中的一个逻辑关注点，需要在文件中反复上下滚动，这并不理想。另外，如果想要将一个逻辑关注点抽取重构到一个可复用的工具函数中，需要从文件的多个不同部分找到所需的正确片段。

SmartAdmin中的写法才真正做到了vue3组合式composition api的意图，每一块业务的变量与方法写到一起，比如上面代码中的`显示与隐藏`的变量和方法放到了一起，`表单`的变量和方法放到了一起，下面引用官方的话

> 现在与同一个逻辑关注点相关的代码被归为了一组：无需再为了一个逻辑关注点在不同的选项块间来回滚动切换。此外，现在可以很轻松地将这一组代码移动到一个外部文件中，不再需要为了抽象而重新组织代码，大大降低了重构成本，这在长期维护的大型项目中非常关键。

### 2.3 模板引用变量Ref

对于vue3中的模板引用ref，即 ref 是作为一个特殊的 attribute
```html

<input ref="inputRef">
```


要求：

* 使用 ref方法，参数为空 进行声明变量
* 变量必须以 `Ref`为结尾
* template中的ref 也必须以 `Ref` 为结尾

比如上面的例子，声明如下
```js

const inputRef = ref();
```


### 2.4 变量和方法的注释

在使用Composition Api进行代码编写时，有效的组织了代码，但是由于Composition Api变量和方法会写到一起，这时候注释就变得很有必要 要求：

* 变量必须都加上注释
* 方法必须加上注释 比如
```js

  // 查询 公告 默认值
  const queryFormState = {
    noticeTypeId: undefined, //分类
    keywords: '', //标题、作者、来源
    documentNumber: '', //文号
    createUserId: undefined, //创建人
    deletedFlag: undefined, //删除标识
    createTimeBegin: null, //创建-开始时间
    createTimeEnd: null, //创建-截止时间
    publishTimeBegin: null, //发布-开始时间
    publishTimeEnd: null, //发布-截止时间
    pageNum: 1,
    pageSize: PAGE_SIZE,
  };
  // 查询 公告 请求表单
  const queryForm = reactive({ ...queryFormState });
```


## 三、Vue3 组件规范

### 3.1、 组件文件名

组件文件名应该为 pascal-case 格式

正例：
```javascript

components
|- my-component.vue
```


反例：
```javascript

components
|- myComponent.vue
|- MyComponent.vue
```


### 3.2、 父子组件文件名

和父组件紧密耦合的子组件应该以父组件名作为前缀命名 正例：
```javascript

components
|- todo-list.vue
|- todo-list-item.vue
|- todo-list-item-button.vue
|- user-profile-options.vue （完整单词）
```


反例：
```javascript

components
|- TodoList.vue
|- TodoItem.vue
|- TodoButton.vue
|- UProfOpts.vue （使用了缩写）
```


### 3.3、 组件属性

组件属性较多，应该主动换行。

正例：
```html

<MyComponent foo="a" bar="b" baz="c"
    foo="a" bar="b" baz="c"
    foo="a" bar="b" baz="c"
 />
```


反例：
```html

<MyComponent foo="a" bar="b" baz="c" foo="a" bar="b" baz="c" foo="a" bar="b" baz="c" foo="a" bar="b" baz="c"/>
```


### 3.4、 模板中表达式

组件模板应该只包含简单的表达式，复杂的表达式则应该重构为计算属性或方法。复杂表达式会让你的模板变得不那么声明式。应该尽量描述应该出现的是什么，而非如何计算那个值。而且计算属性和方法使得代码可以重用。

正例：
```js

<template>
  <p>{{ normalizedFullName }}</p>
</template>

// 复杂表达式已经移入一个计算属性
computed: {
  normalizedFullName: function () {
​    return this.fullName.split(' ').map(function (word) {
​      return word[0].toUpperCase() + word.slice(1)
​    }).join(' ')
  }
}
```


反例：
```js

<template>
  <p>
       {{
          fullName.split(' ').map(function (word) {
​             return word[0].toUpperCase() + word.slice(1)
           }).join(' ')
        }}
  </p>
</template>
```


### 3.5、 标签顺序

单文件组件应该总是让标签顺序保持为 `<template> 、<script>、 <style>`

正例：
```java

<template>...</template>
<script>...</script>
<style>...</style>
```


反例：
```
<template>...</template>
<style>...</style>
<script>...</script>
```


## 四、Vue Router 规范

### 4.1、 页面传参

页面跳转，例如 A 页面跳转到 B 页面，需要将 A 页面的数据传递到 B 页面，推荐使用 路由参数进行传参，即 `{query:param}`

正例：
```js

let id = ' 123';
this.$router.push({ name: 'userCenter', query: { id: id } });
```


### 4.2、 path 和 name 命名规范

* path`kebab-case`命名规范（尽量与vue文件的目录结构保持一致，因为目录、文件名都是`kebab-case`，这样很方便找到对应的文件）
* path 必须以 / 开头，即使是children里的path也要以 / 开头。如下示例
* 经常有这样的场景：某个页面有问题，要立刻找到这个vue文件，如果不用以/开头，path为parent和children组成的，可能经常需要在router文件里搜索多次才能找到，而如果以/开头，则能立刻搜索到对应的组件
* name 命名规范采用`KebabCase`命名规范且和component组件名保持一致！（因为要保持keep-alive特性，keep-alive按照component的name进行缓存，所以两者必须高度保持一致）
```js

// 动态加载
export const reload = [
  {
    path: '/reload',
    name: 'reload',
    component: Main,
    meta: {
      title: '动态加载',
      icon: 'icon iconfont'
    },

    children: [
      {
        path: '/reload/smart-reload-list',
        name: 'SmartReloadList',
        meta: {
          title: 'SmartReload',
          childrenPoints: [
            {
              title: '查询',
              name: 'smart-reload-search'
            },
            {
              title: '执行reload',
              name: 'smart-reload-update'
            },
            {
              title: '查看执行结果',
              name: 'smart-reload-result'
            }
          ]
        },
        component: () =>
          import('@/views/reload/smart-reload/smart-reload-list.vue')
      }
    ]
  }
];
```


## 五、 Vue 项目规范

### 5.1、 目录规范
```
src                               源码目录
|-- api                              所有api接口
|-- assets                           静态资源，images, icons, styles等
|-- components                       公用组件
|-- config                           配置信息
|-- constants                        常量信息，项目所有Enum, 全局常量等
|-- directives                       自定义指令
|-- i18n                             国际化
|-- lib                              外部引用的插件存放及修改文件
|-- mock                             模拟接口，临时存放
|-- plugins                          插件，全局使用
|-- router                           路由，统一管理
|-- store                            vuex, 统一管理
|-- theme                            自定义样式主题
|-- utils                            工具类
|-- views                            视图目录
|   |-- role                             role模块名
|   |-- |-- role-list.vue                    role列表页面
|   |-- |-- role-add.vue                     role新建页面
|   |-- |-- role-update.vue                  role更新页面
|   |-- |-- index.less                      role模块样式
|   |-- |-- components                      role模块通用组件文件夹
|   |-- employee                         employee模块
```


### 5.2、 api 目录

* api文件要以api为结尾，比如 `employee-api.js`、`login-api.js`，方便查找
* api文件必须导出对象必须以`Api`为结尾，如：`employeeApi`、`noticeApi`
* api中以一个对象将方法包裹
* api中的注释，必须和后端 swagger 文档保持一致，同时保留后端作者

正例：

前端： `department-api.js`
```js

import { getRequest, postRequest } from '/@/lib/axios';

export const departmentApi = {
  /**
   * @description: 查询部门列表 @author 卓大
   * @param {*}
   * @return {*}
   */
  queryAllDepartment: () => {
    return getRequest('/department/listAll');
  },

  /**
   * @description: 查询部门树形列表 @author 卓大
   * @param {*}
   * @return {*}
   */
   queryDepartmentTree: () => {
    return getRequest('/department/treeList');
  },

  /**
   * @description: 添加部门 @author 卓大
   * @param {*}
   * @return {*}
   */
  addDepartment: (param) => {
    return postRequest('/department/add', param);
  },
  /**
   * @description: 更新部门信息 @author 卓大
   * @param {*}
   * @return {*}
   */
  updateDepartment: (param) => {
    return postRequest('/department/update', param);
  }
};
```


### 5.3、 assets 目录

assets 为静态资源，里面存放 images, styles, icons 等静态资源，静态资源命名格式为 kebab-case
```
|assets
|-- icons
|-- images
|   |-- background-color.png
|   |-- upload-header.png
|-- styles
```


### 5.4、 components 目录

此目录应按照组件进行目录划分，目录命名为 kebab-case，一个组件必须一个单独的目录 ；
 目的：

* 一个组件一个目录是为了将来组件的扩展，因为这是整个项目公用的组件
* 组件入口必须为 index.vue，原因也是因为这是整个项目公用的组件

举例如下：
```
|components
|-- error-log
|   |-- index.vue
|   |-- index.less
|-- markdown-editor
|   |-- index.vue
|   |-- index.js
|-- kebab-case
```


### 5.5、 constants 目录

此目录存放项目所有常量和枚举，如果常量在 vue 中使用，请使用 `src/plugin/smart-enums-plugin.js`插件，也可以使用第三方库： [vue-enum 插件](https://www.npmjs.com/package/vue-enum)，但是还不支持vue3，等待更新吧

具体要求：

* 常量文件要以 `const` 为结尾，比如`login-const.js`、`file-const.js`
* 变量要：大写下划线，比如 `LOGIN_RESULT_ENUM`、`LOGIN_SUCCESS`、`LOGIN_FAIL`
* 如果是 枚举，变量必须以 `ENUM`为结尾，如：`LOGIN_RESULT_ENUM`、`CODE_FRONT_COMPONENT_ENUM`

目录结构：
```
|constants
|-- index-const.js
|-- role-const.js
|-- employee-const.js
```


例子： employee-const.js
```js

export const EMPLOYEE_STATUS = {
  NORMAL: {
    value: 1,
    desc: '正常'
  },
  DISABLED: {
    value: 1,
    desc: '禁用'
  },
  DELETED: {
    value: 2,
    desc: '已删除'
  }
};

export const EMPLOYEE_ACCOUNT_TYPE = {
  QQ: {
    value: 1,
    desc: 'QQ登录'
  },
  WECHAT: {
    value: 2,
    desc: '微信登录'
  },
  DINGDING: {
    value: 3,
    desc: '钉钉登录'
  },
  USERNAME: {
    value: 4,
    desc: '用户名密码登录'
  }
};

export default {
  EMPLOYEE_STATUS,
  EMPLOYEE_ACCOUNT_TYPE
};
```


### 5.6、 router 与 store 目录

这两个目录一定要将业务进行拆分，不能放到一个 js 文件里。

`router` 尽量按照 views 中的结构保持一致

`store` 按照业务进行拆分不同的 js 文件

### 5.7、 views 目录

目录要求，按照模块划分，其中具体文件名要求如下：

* 如果是列表页面，要以list为结尾，如`role-list.vue`、`cache-list.vue`
* 如果是 表单页面，要以 form为结尾，如 `role-form.vue`、`notice-add-form.vue`
* 如果是 modal弹窗，要以 modal为结尾，如 表单弹窗 `role-form-modal.vue`，详情 `role-detail-modal.vue`
* 如果是 drawer 抽屉页面，要同上以 `Drawer`为结尾
```js

|-- views                                        视图目录
|   |-- role                                     role模块名
|   |   |-- role-list.vue                        role列表页面
|   |   |-- role-add-form.vue                    role新建页面
|   |   |-- role-update-form-modal.vue           role更新页面
|   |   |-- index.less                           role模块样式
|   |   |-- components                           role模块通用组件文件夹
|   |   |   |-- role-title-modal.vue             role弹出框组件
|   |-- employee                                 employee模块
|   |-- behavior-log                             行为日志log模块
|   |-- code-generator                           代码生成器模块
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### Java规范 V3.0

# SmartAdmin 《Java规范 V3.0》

## 阅读须知

 ，[前往阅读](./basic.html)

## 一、Java 项目规范

### 1.1、Java 项目命名规范

全部采用小写方式， 以中划线分隔。
```java

正例：`mall-management-system / order-service-client / user-api`

反例：`mall_management-system / mallManagementSystem / orderServiceClient`
```


### 1.2、方法参数规范

无论是 `controller，service，manager，dao` 亦或是**其他 class 的**代码，每个方法最多 `5` 个参数，如果超出 `5` 个参数的话，要封装成 `javabean` 对象。

* 方便他人调用，降低出错几率。尤其是当参数是同一种类型，仅仅依靠顺序区分，稍有不慎便是灾难性后果，而且排查起来也极其恶心。
* 保持代码整洁、清晰度。当一个个方法里充斥着一堆堆参数的时候，再坚强的人，也会身心疲惫。

反例：
```java

/**
* 使用证书加密数据工具方法
*
* @param param 参数
* @param password 加密密码
* @param priCert 私钥
* @param pubCert 公钥
* @param username 用户名
* @param ip ip地址
* @param userAgent 用户特征
* @return 返回加密后的字符串
*/
public String signEnvelop(JdRequestParam param, String password, String priCert, String pubCert, String username, String ip, String userAgent){

}
```


### 1.3、代码目录结构

统一的目录结构是所有项目的基础。
```xml

src                               源码目录
|-- common                            各个项目的通用类库
|-- config                            项目的配置信息
|-- constant                          全局公共常量
|-- handler                           全局处理器
|-- interceptor                       全局连接器
|-- listener                          全局监听器
|-- module                            各个业务(方便将来拆成微服务)
|-- |--- employee                         员工模块
|-- |--- role                             角色模块
|-- |--- login                            登录模块
|-- third                             三方服务，比如redis, oss，微信sdk等等
|-- util                              全局工具类
|-- Application.java                  启动类
```


### 1.4、common 目录规范

common 目录用于存放各个项目通用的项目，但是又可以依照项目进行特定的修改。
```java

src 源码目录
|-- common 各个项目的通用类库
|-- |--- anno          通用注解，比如权限，登录等等
|-- |--- constant      通用常量，比如 ResponseCodeConst
|-- |--- domain        全局的 javabean，比如 BaseEntity,PageParamDTO 等
|-- |--- exception     全局异常，如 BusinessException
|-- |--- json          json 类库，如 LongJsonDeserializer，LongJsonSerializer
|-- |--- swagger       swagger 文档
|-- |--- validator     适合各个项目的通用 validator，如 CheckEnum，CheckBigDecimal 等
```


### 1.5、module 目录规范

module 目录里写项目的各个业务，每个业务一个独立的顶级文件夹，在文件里进行 mvc 的相关划分。 其中，domain 包里存放 entity, dto, vo，bo 等 javabean 对象
```java

src
|-- module                         所有业务模块
|-- |-- role                          角色模块
|-- |-- |--RoleController.java              controller
|-- |-- |--RoleConst.java                   role相关的常量
|-- |-- |--RoleService.java                 service
|-- |-- |--RoleDao.java                     dao
|-- |-- |--domain                           domain
|-- |-- |-- |-- RoleEntity.java                  表对应实体
|-- |-- |-- |-- RoleForm.java                     请求Form对象
|-- |-- |-- |-- RoleVO.java                      返回对象
|-- |-- employee                      员工模块
|-- |-- login                         登录模块
|-- |-- email                         邮件模块
|-- |-- ....                          其他
```


## 二、MVC 规范

### 2.1、整体分层

* controller 层
* service 层
* manager 层
* dao 层

### 2.2、 `controller` 层规范

#### 1） 只允许在 method 上添加 `RequestMapping` 注解

只允许在 method 上添加 `RequestMapping` 注解，不允许加在 `class` 上（为了方便的查找 url，放到 class 上 url 不能一次性查找出来）

正例：
```java

@RestController
public class DepartmentController {

    @GetMapping("/department/list")
    public ResponseDTO<List<DepartmentVO>> listDepartment() {
        return departmentService.listDepartment();
    }
```


反例：
```java

@RequestMapping ("/department")
public class DepartmentController {

    @GetMapping("/list")
    public ResponseDTO<List<DepartmentVO>> listDepartment() {
        return departmentService.listDepartment();
    }
```


#### 2）不推荐使用 restful 命名 url

不推荐使用 restful 命名 url， 只能使用 `get/post` 方法。url 命名遵循：**`/业务模块/子模块/动作`** ;
 其中 `业务模块和子模块` 使用 名字， `动作` 使用动词； 原因：

> **虽然 restful 大法好，但是有时并不能一眼根据 url 看出来是什么操作，所以选择了后者，这个没有对与错，只有哪个更适合团队**

正例：
```java

GET  /department/get/{id}      查询某个部门详细信息
POST /department/query         复杂查询
POST /department/add           添加部门
POST /department/update        更新部门
GET  /department/delete/{id}   删除部门

GET  /department/employee/delete/{id}   删除部门员工
```


#### 3）swagger 接口注释必须加上后端作者

每个方法必须添加 `swagger` 文档注解 `@ApiOperation` ，并填写接口描述信息，描述最后必须加上接口的作者信息，格式如下： `@author 卓大`

正例：
```java

    @ApiOperation("更新部门信息 @author 卓大")
    @PostMapping("/department/update")
    public ResponseDTO<String> updateDepartment(@Valid @RequestBody DepartmentUpdateForm departmentUpdateForm) {
        return departmentService.updateDepartment(departmentUpdateForm);
    }
```


#### 4）controller 每个方法要保持简洁

controller 在mvc中负责协同和委派业务，充当路由的角色，所以要保持代码少量和清晰，要做到如下要求：

* 不做任何的业务逻辑操作
* 不做任何的参数、业务校验，参数校验只允许使用@Valid 注解做简单的校验
* 不做任何的数据组合、拼装、赋值等操作

正例：
```java

    @ApiOperation("添加部门 @author 卓大")
    @PostMapping("/department/add")
    public ResponseDTO<String> addDepartment(@Valid @RequestBody DepartmentAddForm departmentAddForm) {
        return departmentService.addDepartment(departmentAddForm);
    }
```


#### 5）只能在 `controller` 层获取当前请求用户

只能在 `controller` 层获取当前请求用户，并传递给 `service` 层。

> **因为获取当前请求用户是从 ThreadLocal 里获取取的，在 service、manager、dao 层极有可能是其他非 request 线程调用，会出现 null 的情况，尽量避免**
```java

    @ApiOperation("添加员工 @author 卓大")
    @PostMapping("/employee/add")
    public ResponseDTO<String> addEmployee(@Valid @RequestBody EmployeeAddForm employeeAddForm) {
        RequestUser requestUser = SmartRequestUtil.getRequestUser();
        return employeeService.addEmployee(employeeAddForm, requestUser);
    }
```


### 2.3、 `service` 层规范

#### 1） 合理拆分 service 业务

不建议service文件行数太大 ，如果业务较大，请拆分为多个 service；

如订单业务,所有业务都写到 OrderService 中会导致文件过大，故需要进行拆分如下：

* `OrderQueryService` 订单查询业务
* `OrderCreateService` 订单新建业务
* `OrderDeliverService` 订单发货业务
* `OrderValidatorService` 订单验证业务

#### 2) 谨慎使用 `@Transactional` 事务注解

谨慎使用 `@Transactional` 事务注解的使用，不要简单对 `service` 的方法添加个 `@Transactional` 注解就觉得万事大吉了。
 应当合并对数据库的操作，尽量减少添加了`@Transactional`方法内的业务逻辑。
`@Transactional` 注解内的 `rollbackFor` 值必须使用异常 `Exception.class`

> *对于@Transactional 注解，当 spring 遇到该注解时，会自动从数据库连接池中获取 connection，并开启事务然后绑定到 ThreadLocal 上，如果业务并没有进入到最终的 操作数据库环节，那么就没有必要获取连接并开启事务，应该直接将 connection 返回给数据库连接池，供其他使用（比较难以讲解清楚，如果不懂的话就主动去问）。*

反例：
```java

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> upOrDown(Long departmentId, Long swapId) {
        // 验证 1
        DepartmentEntity departmentEntity = departmentDao.selectById(departmentId);
        if (departmentEntity == null) {
            return ResponseDTO.wrap(DepartmentResponseCodeConst.NOT_EXISTS);
        }
        // 验证 2
        DepartmentEntity swapEntity = departmentDao.selectById(swapId);
        if (swapEntity == null) {
            return ResponseDTO.wrap(DepartmentResponseCodeConst.NOT_EXISTS);
        }
        // 验证 3
        Long count = employeeDao.countByDepartmentId(departmentId)
        if (count != null && count > 0) {
            return ResponseDTO.wrap(DepartmentResponseCodeConst.EXIST_EMPLOYEE);
        }
        // 操作数据库 4
        Long departmentSort = departmentEntity.getSort();
        departmentEntity.setSort(swapEntity.getSort());
        departmentDao.updateById(departmentEntity);
        swapEntity.setSort(departmentSort);
        departmentDao.updateById(swapEntity);
        return ResponseDTO.succ();
    }
```


以上代码前三步都是使用 connection 进行验证操作，由于方法上有@Transactional 注解，所以这三个验证都是使用的同一个 connection。

若对于复杂业务、复杂的验证逻辑，会导致整个验证过程始终占用该 connection 连接，占用时间可能会很长，直至方法结束，connection 才会交还给数据库连接池。

对于复杂业务的不可预计的情况，长时间占用同一个 connection 连接不是好的事情，应该尽量缩短占用时间。

正例：
```java

    DepartmentService.java

    public ResponseDTO<String> upOrDown(Long departmentId, Long swapId) {
        DepartmentEntity departmentEntity = departmentDao.selectById(departmentId);
        if (departmentEntity == null) {
            return ResponseDTO.wrap(DepartmentResponseCodeConst.NOT_EXISTS);
        }
        DepartmentEntity swapEntity = departmentDao.selectById(swapId);
        if (swapEntity == null) {
            return ResponseDTO.wrap(DepartmentResponseCodeConst.NOT_EXISTS);
        }
        Long count = employeeDao.countByDepartmentId(departmentId)
        if (count != null && count > 0) {
            return ResponseDTO.wrap(DepartmentResponseCodeConst.EXIST_EMPLOYEE);
        }
        departmentManager.upOrDown(departmentSort,swapEntity);
        return ResponseDTO.succ();
    }


    DepartmentManager.java

    @Transactional(rollbackFor = Throwable.class)
    public void upOrDown(DepartmentEntity departmentEntity ,DepartmentEntity swapEntity){
        Long departmentSort = departmentEntity.getSort();
        departmentEntity.setSort(swapEntity.getSort());
        departmentDao.updateById(departmentEntity);
        swapEntity.setSort(departmentSort);
        departmentDao.updateById(swapEntity);
    }
```


将数据在 service 层准备好，然后传递给 manager 层，由 manager 层添加@Transactional 进行数据库操作。

**以上是使用`manager`去处理解决的，其实也可以是使用spring的 `TransactionTemplate`  事务模板解决**

#### 3）需要注意的是：注解 `@Transactional` 事务在类的内部方法调用是不会生效的

反例：如果发生异常，saveData 方法上的事务注解并不会起作用
```java

@Service
public class OrderService{

    public void createOrder(OrderAddForm addForm){
        this.saveData(addForm);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveData(OrderAddForm addForm){
        orderDao.insert(addForm);
    }
}
```


> *Spring 采用动态代理(AOP)实现对 bean 的管理和切片，它为每个 class 生成一个代理对象。只有在代理对象之间进行调用时，可以触发切面逻辑。而在同一个 class 中，方法 A 调用方法 B，调用的是原对象的方法，而不通过代理对象。所以 Spring 无法拦截到这次调用，也就无法通过注解保证事务了。简单来说，在同一个类中的方法调用，不会被方法拦截器拦截到，因此事务不会起作用。*

解决方案：

1. 可以将方法放入另一个类，如新增 `manager层`，通过 spring 注入，这样符合了在对象之间调用的条件。
2. 启动类添加 `@EnableAspectJAutoProxy(exposeProxy = true)`，方法内使用`AopContext.currentProxy()`获得代理类，使用事务。
```java

SpringBootApplication.java

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class SpringBootApplication {}

OrderService.java

public void createOrder(OrderCreateDTO createDTO){
    OrderService orderService = (OrderService)AopContext.currentProxy();
    orderService.saveData(createDTO);
}
```


### 2.4、 manager 层规范

manager 层的作用(引自《阿里 java 手册》)：

* 对第三方平台封装的层，预处理返回结果及转化异常信息；
* 对 Service 层通用能力的下沉，如缓存方案、中间件通用处理；
* 与 DAO 层交互，对多个 DAO 的组合复用。

### 2.5、 dao 层规范

#### 1）持久层框架选择

优先使用 mybatis-plus 框架。如果需要多个数据源操作的，可以选择使用 实验室的 [SmartDb](https://gitee.com/lab1024/smartdb.git) 框架。

#### 2）使用mybatis-plus的要求

* 所有 Dao 继承自 BaseMapper
* 禁止使用 Mybatis-plus 的 Wrapper 条件构建器；原因：1、SQL无法复用；2、排查如慢SQL的时候无法以搜索SQL的形式快速定位到代码；
* 禁止直接在 mybatis xml 中写死常量，应从 dao 中传入到 xml 中

正例： NoticeDao.java 常量在参数中传入到xml
```java

public interface NoticeDao{

    Integer noticeCount(@Param("sendStatus") Integer sendStatus);

}
```


NoticeMapper.xml
```xml

    <select id="noticeCount" resultType="integer">
        select
        count(1)
        from t_notice
        where
        send_status = #{sendStatus}
    </select>
```


反例：常量直接写死到 xml 中
```java

public interface NoticeDao{

    Integer noticeCount();

}
```


NoticeMapper.xml
```xml

    <select id="noticeCount" resultType="integer">
        select
        count(1)
        from t_notice
        where
        send_status = 0
    </select>
```


#### 4）连接join写法

建议在xml中的 join 关联写法使用表名的全称，而不是用别名，对于关联表太多的话，在xml格式中，其实很难记住 别名是什么意思！

反例： t\_notice 别名 tn，t\_employee别名 e， 在xml中已经很难区分是什么意思了，索性不如使用全称
```xml

<select id="queryPage" resultType="net.lab1024.sa.admin.module.business.oa.notice.domain.vo.NoticeVO">
        SELECT tn.*,
        e.actual_name AS createUserName
        FROM t_notice tn
        LEFT JOIN t_employee e ON tn.create_user_id = e.employee_id
        <where>
            tn.deleted_flag = #{queryForm.deletedFlag}
            <if test="queryForm.keywords != null and queryForm.keywords != ''">
                AND (INSTR(tn.notice_title,#{queryForm.keywords}) OR INSTR(e.actual_name,#{queryForm.keywords}))
            </if>
            <if test="queryForm.noticeType != null">
                AND tn.notice_type = #{queryForm.noticeType}
            </if>
            <if test="queryForm.noticeBelongType != null">
                AND tn.notice_belong_type = #{queryForm.noticeBelongType}
            </if>
            <if test="queryForm.startTime != null">
                AND DATE_FORMAT(tn.publish_time, '%Y-%m-%d') &gt;= #{queryForm.startTime}
            </if>
            <if test="queryForm.endTime != null">
                AND DATE_FORMAT(tn.publish_time, '%Y-%m-%d') &lt;= #{queryForm.endTime}
            </if>
            <if test="queryForm.disabledFlag != null">
                AND tn.disabled_flag = #{queryForm.disabledFlag}
            </if>
        </where>
        <if test="queryForm.sortItemList == null or queryForm.sortItemList.size == 0">
            ORDER BY tn.top_flag DESC,tn.publish_time DESC
        </if>
    </select>
```


正确：使用全称
```xml

    <select id="queryPage" resultType="net.lab1024.sa.admin.module.business.oa.notice.domain.vo.NoticeVO">
        SELECT t_notice.*,
        t_employee.actual_name AS createUserName
        FROM t_notice
        LEFT JOIN t_employee  ON t_notice.create_user_id = t_employee.employee_id
        <where>
            t_notice.deleted_flag = #{queryForm.deletedFlag}
            <if test="queryForm.keywords != null and queryForm.keywords != ''">
                AND (INSTR(t_notice.notice_title,#{queryForm.keywords}) OR INSTR(t_employee.actual_name,#{queryForm.keywords}))
            </if>
            <if test="queryForm.noticeType != null">
                AND t_notice.notice_type = #{queryForm.noticeType}
            </if>
            <if test="queryForm.noticeBelongType != null">
                AND t_notice.notice_belong_type = #{queryForm.noticeBelongType}
            </if>
            <if test="queryForm.startTime != null">
                AND DATE_FORMAT(t_notice.publish_time, '%Y-%m-%d') &gt;= #{queryForm.startTime}
            </if>
            <if test="queryForm.endTime != null">
                AND DATE_FORMAT(t_notice.publish_time, '%Y-%m-%d') &lt;= #{queryForm.endTime}
            </if>
            <if test="queryForm.disabledFlag != null">
                AND t_notice.disabled_flag = #{queryForm.disabledFlag}
            </if>
        </where>
        <if test="queryForm.sortItemList == null or queryForm.sortItemList.size == 0">
            ORDER BY t_notice.top_flag DESC,t_notice.publish_time DESC
        </if>
    </select>
```


### 2.6、 javabean 命名规范

1） `javabean` 的整体要求：

* 不得有任何的业务逻辑或者计算
* 基本数据类型必须使用包装类型`（Integer, Double、Boolean 等）`
* 不允许有任何的默认值
* 每个属性必须添加注释，并且必须使用多行注释。
* 必须使用 `lombok` 简化 `getter/setter` 方法
* 建议对象使用 `lombok` 的 `@Builder ，@NoArgsConstructor`，同时使用这两个注解，简化对象构造方法以及 set 方法。

2）javabean 名字划分：

* XxxEntity 数据库持久对象
* XxxVO 返回前端对象 （一些大厂用 Resp结尾，比如 XxxxResp）
* XxxForm 前端请求对象 （一些大厂用 Req结尾，比如 XxxxReq）
* XxxDTO 数据传输对象
* XxxBO 内部处理对象

3）数据对象；`XxxxEntity`，要求：

* 以 `Entity` 为结尾（阿里是为 DO 为结尾）
* Xxxx 与数据库表名保持一致
* 类中字段要与数据库字段保持一致，不能缺失或者多余
* 类中的每个字段添加注释，并与数据库注释保持一致
* 不允许有组合
* 项目内的日期类型必须统一，使用 `java.time.LocalDateTime` 或者 `java.time.LocalDate`

4）请求对象；`XxxxForm`，要求：

* 不可以继承自 `Entity`
* `Form` 可以继承、组合其他 `DTO，VO，BO` 等对象
* `Form` 只能用于前端、RPC 的请求参数

3）返回对象；`XxxxVO`，要求：

* 不可继承自 `Entity`
* `VO` 可以继承、组合其他 `DTO，VO，BO` 等对象
* `VO` 只能用于返回前端、rpc 的业务数据封装对象

4）业务对象 `BO`，要求：

* 不可以继承自 `Entity`
* `BO` 对象只能用于 `service，manager，dao` 层，不得用于 `controller` 层

### 2.7、boolean 类型的属性命名规范

> 类中布尔类型的变量，都不要加 is，否则部分框架解析会引起序列化错误。反例：定义为基本数据类型 Boolean isDeleted；的属性，它的方法也是 isDeleted()，RPC 在反向解析的时候，“以为”对应的属性名称是 deleted，导致属性获取不到，进而抛出异常。

这是阿里巴巴开发手册中的原文，团队的规定是：`boolean` 类型的类属性和数据表字段都统一使用 `flag` 结尾。虽然使用 `isDeleted，is_deleted` 从字面语义上更直观，但是比起可能出现的潜在错误，这点牺牲还是值得的。

正例：
```
deletedFlag，deleted_flag，onlineFlag，online_flag
```


## 三、数据库 规范

### 3.1、数据库命名

全部采用小写方式， 以下划线分隔，并且区分是什么环境的数据库
```sql

正例：smart_admin_v2_dev / smart_admin_v2_prod / smart_admin_v2_test

反例：mall_management-system / mallManagementSystem / orderServiceClient
```


### 3.2、表命名

全部采用小写方式， 以下划线分隔，并且并且以 `t_` 开头

比如：员工表：`t_employee`、部门表：`t_department`、配置表：`t_config`

### 3.3、建表规范

若需要的话，表必备三字段：[module]\_id, create\_time, update\_time ，简单表除外：如 log表，不需要 update\_time 等

* [module]\_id 字段 Long 类型，单表自增，自增长度为 1，module为业务名称，如：user\_id、order\_id 等
* create\_time 字段 datetime 类型，默认值 CURRENT\_TIMESTAMP
* update\_time 字段 datetime 类型，默认值 CURRENT\_TIMESTAMP, On update CURRENT\_TIMESTAMP

**#### 枚举类表字段注释需要将所有枚举含义进行注释**

修改或增加字段的状态描述，必须要及时同步更新注释。如下表的 `sync_status` 字段 `同步状态 0 未开始 1同步中 2同步成功 3失败`。

正例：
```sql

CREATE TABLE `t_change_data` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`sync_status` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0' COMMENT '同步状态 0 未开始 1同步中 2同步成功 3失败',
	`sync_time` DATETIME NULL DEFAULT NULL COMMENT '同步时间',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`update_time` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`change_data_id`)
)
```


反例：
```sql

CREATE TABLE `t_change_data` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`sync_status` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0' COMMENT '同步状态 ',
	`sync_time` DATETIME NULL DEFAULT NULL COMMENT '同步时间',
	`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`update_time` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`change_data_id`)
)
```


### 3.4、索引规范

具体索引规范请参照《阿里巴巴 Java 开发手册》索引规约

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


---

## 顶级架构思想


### 微服务与单体那些事儿

# 微服务与单体那些事儿

## 序

本篇文章比较长，主讲架构，因为架构不是个技术问题，他牵扯面很多，比较复杂，所以会长；
 本文会结合实验室这多年来的项目经验，以及开源这几年来认识的其他企业相关经验，还有身边一二线大厂、中型企业、小企业、国央企、政府科研院所等相关技术朋友的项目经验，还有“商业”、“成本”、“人员”、“团队”等其他方面的因素，来讲述架构；

## 一、背景与问题

为什么写这篇文章，是发现开源这几年来，很多人问 “1024创新实验室” 如下几个问题：

* “是否有微服务版本？”
* “为什么SmartAdmin不用微服务？”
* “你们是不是没用过微服务？”

先回答下问题：**有微服务版本`smart-cloud`，但未开源，精力不够；很多中大型项目用过微服务；**

---

**故事：** 这几年使用SmartAdmin的企业已经超过了千余家，被问过很多关于架构、微服务等相关的问题，甚至身边的企业，朋友还出现过盲目上微服务，为了微服务而微服务、还有一个项目出现过 两个开发维护一个大型微服务项目最后把老板逼着跑路的故事等等，发生过很多有意思的故事。
**经验：** 实验室本身这些年做过很多项目， 其中有大型产业电商项目、供应链项目、大型在线教育项目、大型企业ERP项目、CRM项目、大型国企项目、三线城市公交项目、大型物联网项目、中型仓储、物流项目、中型互联网体育项目、科研院所项目、部队军工项目、银行项目、学校智慧校园等等各行各业都接触过。 有些项目用了微服务架构，有些项目是多模块多体架构，有些是单体项目等等，不同的业务用了不同的架构。

## 二、关于架构的认知

很多人认为架构是一个技术问题，但笔者认为，架构是一个 “复杂科学”的技术问题，他需要考虑“技术能力”、“团队组织”、“基础设施”、“商业价值”、“业务形态”、“时间周期”、“边际成本”等等多个方面。如果只是从技术方面考虑，容易误入歧途。

* 技术能力：无论什么架构，在这种架构上的技术你是否精通？是否有系统且全面的架构解决技术能力？实验室对于架构技术的理解是，一定要系统且完整、全面的把这个技术从头到尾的学习一遍，且从外到内，再从内到外理解深刻后，并从小到大在真实的项目中使用过，才是真正的掌握某个技术。
* 团队组织：首先团队每个人要对这种架构熟悉，且团队必须有个拥有此架构落地经验的架构师，围绕架构师周边还需要有高级开发负责核心模块，与此同时还需要有一些负责业务开发中后端。
* 商业价值：无论哪种架构，只要脱离业务和商业，都属于空谈架构，但结合业务需求来看，不合理的架构会给商业带来风险。比如你利用架构支撑那些现在不需要的业务需求，导致许多努力、人力、财力白白浪费，这叫做架构努力错位。比如用一个单体架构开发一套大型电商平台，异或使用微服务架构开发了一个简单的后台管理CMS系统等等
* 业务形态：要关注业务的属性，比如是传统行业ERP、CRM、MES系统关注的是单据的流转，互联网行业的电商、在线教育等关注的是用户体验，物联网项目关注的是数据采集和报警等等，这些系统的业务形态，比所以用到的架构是不一样的。
* 时间周期：比如需要很短时间内做一个试验性的产品、或者长时间内做一个电商项目、再或者很明确要长期做一个大型企业的内部ERP系统等等；对于实验性项目突出的就是快，那么相对单体更适合，比如长期的复杂电商项目，相对微服务会比较好一些，但是这些都是相对而言，具体还是需要按照实际的周期来看。再比如融资了100万，做一个细分小领域的B2C平台，你该怎么选择呢？
* 边际成本：要考虑人工成本、维护成本、质量成本、性能成本，所有权成本、软硬件成本等等
* 其他方面：基础设置是否成熟、扩展、市场时间等等

**架构设计的三大原则：简单、适度、演化**

## 三、单体架构

单体架构最种常见，也是这几十年来最主要的架构。使用经典的 3 层模型，即表示层、业务逻辑层和数据访问层即所有功能都在一个工程里，打成一个jar包、war包进行部署。

案例：

> 1）早期开源中国（oschina）是个经典的案例，以一台4核8g撑起来一个十几万人的社区，且jfinal就是模仿的oschina的架构。
>  2）github，GitHub 是基于 Ruby on Rails 的单体架构，直到 2021 年，为了让超过一半的开发人员在单体代码库之外富有成效地开展工作，GitHub 以赋能为出发点开始了向微服务架构的迁移。
>  3）互联网大厂后台管理系统，央国企的后台管理系统，ERP、OA、CRM等等传统应用

### 3.1、优缺点

优点：
 1、单体架构开发简单，容易上手，开发人员只要集中精力开发当前工程
 2、容易修改，只需要修改对应功能模块的代码，且容易找到相关联的其他业务代码
 3、部署简单，由于是完整的结构体，编译打包成jar包或者war包，直接部署在一个服务器上即可
 4、容易扩展，可以将某些业务抽出一个新的单体架构，用于独立分担压力，也可以方便部署集群
 5、性能最高，对于单台服务器而言，单体架构独享内存和cpu，不需要api远程调用，性能损耗最小
 缺点：
 1、灵活度不高，随着代码量增加，代码整体编译效率下降
 2、规模化，无法满足团队规模化开发，因为共同修改一个项目
 3、应用扩展性比较差，只能横向扩展，不能深度扩展，扩容只能只对这个应用进行扩容，不能做到对某个功能点进行扩容，关键性的代码改动一处多处会受影响
 4、健壮性不高，任何一个模块的错误均可能造成整个系统的宕机
 5、技术升级，如果想对技术更新换代，代价很大

### 3.2、建议


## 四、多模块（SOA）

随着业务发展，重构，单体架构中，业务、代码越来越多，出现了上面 [3.2 相关的问题](./JavaArchitecture.html#_3-2、缺点)，于是将系统按照模块业务拆分成多个单体小系统，系统和系统之间通过明确的接口或者消息串联起来；每个系统内部结构和逻辑发生改变，并不影响对外提供的服务，只要保持接口不变，服务内部对外是透明的；

典型案例

> 早期淘宝，dubbo最初的版本，目的就是多模块这种，只不过支持了集群而已，但是已经向微服务架构迈出了很大的国产化一步

### 4.1、优缺点

优点：
 1、项目制：一个业务单元对应一个系统，由不同的团队开发，好组织和管理
 2、扩展性：可以根据不同的需求，进行重新的组合和构造。
 3、耦合性：把模块拆分，使用接口或者消息通信，降低了模块之间的耦合度
 4、复用性：可以重复利用，相对业务属性独立
 5、敏捷性：方便更新，迭代，可以只更新单个服务（系统）而不需要重写整个项目
 缺点：
 1、灵活度不高，随着代码量增加，某些系统还是会变得复杂和臃肿
 2、开发难度大，业务抽取、接口或者消息设计等问题，考验着团队的技术能力
 3、数据一致性问题
 4、业务拆分，你确定拆分的准确吗？

### 4.2、建议


## 五、微服务

终于到微服务了，但是随着用户和数据量的进一步增长，多模块系统（SOA），服务管理不完善，再扩展也很难，所以这个时候出现了微服务。

这里在明确几个微服务的属性：

* **1）代码库是分开，每个服务单独人维护，独立迭代**
* **2）集成和部署分开，独立且自由的发布**
* **3）数据库分开**
* **4）必须容器管理，网关，、服务治理，服务监控，服务跟踪等基础设施**
* **5）必须有专人来维护服务，而不是就2，3个java共同维护多个微服务就搞笑了**
* **6）技术栈也可以不相同，只要遵守 api json 接口协议即可以上5点是必须有的，如果没有，可能你的并不是微服务。**

### 5.1、优缺点

优点：
 1、独立性：每个服务都是独立灵活，且独立迭代和独立部署
 2、扩展性强：微服务之间是松耦合的，内部是高内聚，容易按需扩展，产品迭代周期更短
 3、隔离性好：每个微服务都是独立的运行，有服务挂掉不会大面积地波及整个服务运行体系
 4、技术选型灵活：对于独立的某个服务，用什么技术栈都可以，比如作者希望用nodejs，完全没问题
 缺点：
 1、复杂度很高：一个业务流程需要多个微服务通过网络交互来完成，开发、调试都很复杂
 2、维护成本大：需要有人维护容器管理，网关，、服务治理，服务监控，服务跟踪等基础设施
 3、确定哪些业务是一个服务很难，因为微服务要拆的更细，什么拆什么不拆，其实很难决策

### 5.2、建议


## 六、微服务病态

当前了解了千余家企业、还有身边认识的大、中、小企业，都陷入了一个怪圈，“我们后端微服务架构，很屌的，网关、服务治理、跟踪、监控、docker全都上了，架构很漂亮的”。我们也以人员外包的形式协助过一些企业一起开发过他们所谓的“微服务”项目，最终结论就是 “写代码好累，沟通好累，交流好累，改bug改的好累，心累，想骂娘”；我们依次举几个真实案例：

### 6.1、某中型文具电商项目

**背景：**

> 地点：北方前5的城市
>  有crm系统、erp进销存系统、面向C端电商系统、B端代理电商系统、有app端应用、也要对接淘宝拼多多下单
>  之前系统是php研发，老板要大干一场，技术团队换成了java（java这批人也不懂文具行业）
>  最终技术团队（写代码）大概20个人（1个前端架构，5个前端、1个后端架构，8个后端、4个移动端、1个运维）
>  B端客户2000人，C端累计用户几十万（注意累计）

开始了：

* 了解业务，拆分服务，拆分服务，最终**20个java服务**，具体服务为：`C端用户、B端用户、商品、B端订单、B端账户、C端订单、交易、仓储、物流、电商对接、CRM电销、文件、合同、资讯、PC、电商App、网站数据、运营活动、财务软件对接、公司员工 等`，10个公共模块，**10个数据库**。
* 容器管理，网关，服务治理，服务监控，服务跟踪等项目，最终java30个，要部署的docker，约40个（含前端）
* 服务器部署：2台阿里云服务器，64G内存， 每台部署了20多个docker（redis、nginx等等其他）

看到这里，想必你已经知道了里面的弊端，以这样的人员规模弄了一套这么大的系统，很“牛”！运行过一段时间后，发现数据：

* B端用户，累计3000个，日活不到1千
* C端用户，累计50万，日活不到2万
* 平台交易量：B端日均不到1000单，C端，日均不到3000单
* 员工：不超过300人

出现的问题：

* 服务的拆分，按照模块还算独立而且合理（哈哈哈）
* 8个后端，30个java项目，维护起来，头大，每天要开好几个idea窗口，来回切换
* 10个数据库，（按理说应该20个数据库，因为20个服务）执行脚本，晕头转向，查询个数据来回切
* 调试起来及其麻烦，从下单、签合同、付款、发货、物流 等要经过十几个微服务
* 运营要做个活动，改完上线，某些服务犹如定时炸弹，总有bug
* 团队人员身心疲惫，记不住哪些服务哪些表
* 运维人员，淦他大爷的，累死老子了

**高潮：**

* **某订单服务开发人员，查询订单频繁被别人调用，直接服务降级，保护自己，不然别人开发就搞死自己，就是自己的bug了，再狠点，调用超过多少次，直接return**
* **老板一脸懵逼，投入那么多人，还每天出bug，进度还慢，一问就是人手不够，再问就是技术架构很屌，问非所答**
* **业务口问技术，技术反馈这实现不了，那实现不了，因为在不同的服务里**
* **20个技术，加上社保公积金、房租水电等，一年至少300万到400万，这么复杂的架构，测试至少6个，再加上产品，算下来一年需要550万到650万最终:**

* **老板两三年投入了两千万左右被技术玩死了**
* **技术也因企业而被裁员了，新来的一个技术，这些架构简直是一坨屎，哈哈**

### 6.2、某200万学校项目

主要做某个专科院校的智慧党建平台，200万；地点：武汉。在校生2万人，拆分业务服务10个，一套微服务架构下来总计20个；15个开发个4月干完；

* 技术因架构问题，与产品沟通这不好做那不好做，不然开发时间不够，人手不够，在老板面前又说技术很先进，最终舍弃了很多功能
* 开发人员天天加班赶功能，调试累，心累，还加班
* 交付学校的时候，学校说不好用，要改，技术与学校方吵了一架，说架构是这样的，无法实现你的想法，于是离职了
* 最终，老板和团队成员猝....

### 6.3、某设计院项目

主要为某个南方一线城市设计院做一个内部项目管理系统；预算200万；微服务10个(8个库)，10个java，中途也调来了一些其他项目人，5个月干完；最终加班少，也有项目奖金，原因是：之前给某北方一线城市做过一个类似的项目，也是微服务架构的，只不过这次拆分了更少的服务，更少的数据库，拆分的更清晰；

### 6.4、问题分析

以上是所接触到的真实的案例，出现问题的原因是“拆分的不合理”，导致一个完整的流程无论从开发、调试、测试 都很麻烦，也很容易出现bug，最终导致开发人员压力很大，然后会认为微服务架构就是个噩梦，每天来回切换项目等等一系列的抱怨。但是微服务这么先进的技术难道不好吗 ？

## 七、总结与建议

### 7.1、业内大佬建议

**前Ebay架构师-杨波**

> “企业一开始不推荐直接使用微服务，因为微服务需要前期基础设施的投资，复杂性很高，如果对问题领域并不是很理解，一开始用微服务，你很难去划分服务的边界，你的生产力反而会比较低，而且你花了很大精力进行开发，你的产品并没有被市场验证过，有可能会失败，所以这个选项风险会比较高。所以推荐的是单块优先，先从单块运用做起，这样成本低，团队成员也比较少，无须太多研发投入，就可以交付一些基本的功能给客户使用。随着应用越来越成功，客户增加，你的系统复杂度会越来越高，就会出现单块应用和团队规模之间的矛盾，生产力会随着业务复杂度逐渐降低。所以在一些初创型公司，你更多看到的是单块应用，只有一些中大型的公司会看到微服务架构。”
>
>  “交叉点表明，业务已经到达了一定的复杂性，单块应用已经无法满足业务增长的需要，研发效率开始下降了，而微服务可以提升研发和交付的效率。这个点需要架构师去综合，权衡。个人经验，一般团队需要达到百人规模，才去考虑微服务。””

**胡忠想(微博微服务技术专家)**

> “一般情况下，这个时候就需要大规模地扩张开发人员，以支撑多个功能的开发。如果这个时候继续采用单体应用架构，多个功能模块混杂在一起开发、测试和部署的话，就会导致不同功能之间相互影响，一次打包部署需要所有的功能都测试 OK 才能上线。
>
>  不仅如此，多个功能模块混部在一起，对线上服务的稳定性也是个巨大的挑战。比如 A 开发的一个功能由于代码编写考虑不够全面，上线后产生了内存泄漏，运行一段时间后进程异常退出，那么部署在这个服务池中的所有功能都不可访问。
>
>  根据实际项目经验，一旦单体应用同时进行开发的人员超过 10 人，就会遇到上面的问题，这个时候就该考虑进行服务化拆分了。”

**Uber 支付体验平台的工程经理 Gergely Oros**

> “Uber 最早通过构建微服务来完成很小的需求或功能，以至于出现了很多由一个人构建维护的微服务。这些微服务的存在带来了新的复杂性和挑战，例如监控、测试、持续集成 / 持续交付（CI/CD）、服务级别协议（SLA）、跨所有微服务的库版本（安全和时区问题）等等。
>  Uber 放弃了微服务，转而使用宏服务，不再只是完成一件事，而是使其服务于一项业务功能，由 5-10 个工程师负责维护。”

**GitHub CTO Jason Warner**

> “任何构建过大型分布式系统的人都知道他们并不真的那样工作，但还必须适应它。”“公司所处的阶段很重要。如果是一家 5-50 人的公司，只需坚持使用单体。” “Warner 鼓励企业根据自己的情况来选择，而不是盲目跟随大厂的做法，他给出的建议是：
>  1）尽可能地延长单体应用的使用时间。
>  2）服务从基础设施开始，而非应用程序。
>  3）如果要打破单体架构，打破大型应用程序，而不是小型服务。
>  4）认为每个新应用程序是贵公司的虚拟墙。
>  5）尽可能选择库而不是微服务。

### 7.2、探寻架构本质

架构的本质问题就是 ，无论你是 单体架构、多模块（SOA）架构、微服务架构 都需要  ；
**那么试问，有多少的技术人员对这个行业熟悉，比如之前你在京东是干电商的，但当前的项目为钢铁电商，这是两个完全不同的电商。**
 引自《微服务设计》中的一句话：

> **“你越不了解一个领域，为服务找到合适的界限上下文就越难，服务的界限划分错误，可能会导致不得不频繁地更改服务间的协作，而这种更改成本更高”**

再引自 GitHub CTO Warner 的一句话：

> **当涉及几十个微服务或更大规模时，企业遇到通常并非技术问题，而是组织上的挑战。**

《1024创新实验室》最终关于 本质问题的看法：

### 7.3、建议


最后，你能找准你所在业务中的领域模型吗？ 或者说有没有其他不一样的观点呢？来联系方式吧，聊聊你的观点！

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 前端Js和Ts的选型那些事儿

# 前端Js和Ts的选型那些事儿

## 序

本篇文章要比《谈谈架构那些事儿》要短很多，因为前端技术虽然五花八门，但是核心还是 javascript。
 本文会结合实验室这多年来的项目经验，以及开源这几年来认识的其他企业相关经验，还有身边一二线大厂、中型企业、小企业、国央企、政府科研院所等相关技术朋友的项目经验，还有“商业”、“成本”、“人员”、“团队”等其他方面的因素，来讲述这个选型；

## 一、背景与问题

SmartAdmin 提供了 `javascript` 和 `typescript` 双版本 供大家选择，那么肯定有人问，建议选择哪个呢？
 已经在十多个项目上用了`vue3 + typescript`，且每个项目的页面都在200个页面以上;
 已经在几十个项目上用了`vue3 + javascript`，且每个项目的页面都在200个页面以上;

**故事：** 这几年使用SmartAdmin的企业已经超过了千余家，出现过很多关于`typescript`的有意思的故事，甚至身边的企业，朋友还出现过盲目用`typescript`，为了`typescript`而`typescript`这种现象。

## 一、typescript

### 1.1、简介

官方定义:

> TypeScript is a typed superset of JavaScript that compiles to plain JavaScript. Any browser. Any host. Any OS. Open source. TypeScript is a strongly typed programming language that builds on JavaScript, giving you better tooling at any scale.

翻译过来：TypeScript 是 JavaScript 的一个超集，可以编译为纯 JavaScript ，在任何浏览器、集群（服务器）、操作系统上面都可以运行，而且还开源。通过在JavaScript的基础上添加静态类型定义构建而成。 如下图： ![typescript](https://img.smartadmin.1024lab.net/smart-admin-v2/base/ts1.png)

### 1.2、ts的本质

简单而言，ts就是它让js有类型了，且提供了其他更好用的方法，但ts的核心还是让js有类型，所以关注点需要从 js和ts的选型上面，转移到：**“我们的项目，是否需要强制类型检查上？”**

强制类型检查的好处，列举下：

* 不怕人员变动，因为有类型，好维护很多
* 变量如果能确定是什么类型，开发人员心里要踏实的多，写出来的程序更有信心
* 能为语言带来各种各样的工具，比如自动补全，静态检查，可靠的重命名变量名等
* 人生苦短，多用代码提示
* 因为有类型约束，团队开发就不会随意放肆，更能做到整齐划一

坏处：

* 稍微多了个代码量而已
* 写的没那么随意，会有类型处处受限

**这是`typescript`的类型的 好坏之分，通过以上这些点，你会发现，这些坏处在好处面前一文不值。**

## 二、问题

阅读到这里，你会觉得，那就不用想了，直接用`typescript`就好了。笔者最开始的时候也是这也认为的，但是后来随着使用越来越多，发现不是那么回事。

有个很有趣的现象，在认识的千余家企业中，有90%左右的企业在`typescript`项目中在有如下几个有意思的特征：

* 这些企业近两年所有的项目都是`typescript`，但是在vscode中都会有类型检查警告
* 几乎所有的开发人员都会在vscode关闭 `typescript`的警告提醒
* 项目中大量的出现`any`类型和不写类型
* api接口请求参数或者返回的对象、lib方法类库等等不使用interface封装且变量、方法没有任何类型
* 最基本的方法参数没有类型、返回值没有类型，一切随缘，想起来的就加上类型，想不起来就不加了
* 项目一开始会有强制要求类型，项目稍微忙一些，算了以满足业务为前提，先不加了，于是后面就几乎都不加类型了

**通过上面的问题，项目开始使用到`ts`是为了使用类型来帮助把项目写好，但是在项目的实际使用中，味道越来越变了，一开始第一个人在项目中开始不使用类型，那么就会有第二个人，第三个人...第n个人不再使用类型，最终`typescript`的类型检查好处全无--《破窗效应》这就是实验室了解到的企业的真实现状，90%的企业虽然在刚开始搭建项目的时候是`typescript`项目，但是最终沦为和写js项目一模一样。**；

> 破窗理论：一个房子如果窗户破了，没有人去修补，隔不久，其它的窗户也会莫名其妙地被人打破;一面墙，如果出现一些涂鸦没有被清洗掉，很快的，墙上就布满了乱七八糟、不堪入目的东西;一个很干净的地方，人们不好意思丢垃圾，但是一旦地上有垃圾出现之后，人就会毫不犹疑地抛，丝毫不觉羞愧。

## 三、最终建议

SmartAdmin 提供了 JS 和 TS 双版本 供大家选择，那么肯定有人问，建议选择哪个呢？
 最终建议是：


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


---

## 后端解读


### 接口加解密

# 接口加解密

## 一、背景与问题

有时候需要对一些关键接口进行如下三种情况：

* 前端请求参数加密，后端解密
* 后端返回加密，前端解密
* 请求参数和后端返回 都需要加解密

## 二、架构与思想

通过上述三种情况，能清楚了解是在 `controller` 层进行一些拦截操作，所以自然而然想到了 `ControllerAdvice`，对于 `RequestBodyAdviceAdapter` 和 `ResponseBodyAdvice` 进行拦截和处理即可。
**当前支持 “国产SM4” 和 “AES” 两种加密算法，后期可以自行扩展**

## 三、具体使用

### 3.1、选择加密算法

**后端：**
 进入到`net.lab1024.sa.base.module.support.apiencrypt.service`包结构，有两个实现类。
 默认在`ApiEncryptServiceSmImpl`类上加了`@Service`注解，表示使用`SM4`加解密；若想改为其他，可以再其他 实现`ApiEncryptService`类上加注解。

**前端:**
 进入到`src/lib/encrypt.js`` 文件，在 如下代码中进行 注释来选择加密算法
```js

// -----------------------  对外暴露： 加密、解密 -----------------------
outline: 'deep'

// 默认使用SM4算法
const EncryptObject = SM4;
// const EncryptObject = AES

...
```


### 3.2、修改秘钥

**后端：** 在`ApiEncryptService`对应的实现类中找到 KEY ，进行修改；

**前端:** 进入到`src/lib/encrypt.js` 文件，找到 KEY，进行修改，要和后端保持一致。

### 3.3、参数加密

**后端：** 在需要解密的 controller方法上加上注解 `@ApiDecrypt` 。

**前端:**
 1）使用 `lib/axios.js` 中的 `postEncryptRequest` 方法进行 请求加密
 2）使用 `src/lib/encrypt.js` 中的 `encryptData` 方法直接进行加密

### 3.4、后端返回加密

**后端：** 在需要解密的 controller方法上加上注解 `@ApiEncrypt` 。

**前端:** 不需要任何操作，因为在`lib/axios.js` 已经对加密过的数据进行解密了。

## 四、演示案例

**后端：** 请看java类: `AdminApiEncryptController`

**前端：** 请看vue文件：`\src\views\support\api-encrypt\api-encrypt-index.vue`

|  |  |
| --- | --- |
|  |  |
| 支持请求参数加密、解密 | 支持返回内容加密、解密 |

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 简单好用SmartJob

# 简单好用SmartJob

## 前言

定时任务是软件开发中的一项基本需求，几乎贯穿于每位开发者的职业生涯，

无论是定时发送通知、安排内容发布，还是周期性地执行特定业务逻辑，这些场景都要求能够管理、监控定时任务。

| 方案 | 优点 | 缺点 |
| --- | --- | --- |
| Spring 自带 | 1、集成简单，直接在Spring Boot应用中使用，无需额外依赖。 | 1、任务管理功能有限，如动态添加或修改任务较麻烦，缺少监控等。 |
| Quartz | 1、功能强大，支持复杂的调度需求，如任务持久化、集群部署、分布式调度。 | 1、配置和使用相对复杂，集成Spring需要更多配置。 |
| Elastic-Job | 1、专为分布式环境设计，支持任务分片、弹性扩缩容。 | 1、需要额外的部署和配置工作。 |
| xxl-job | 1、轻量级分布式任务调度平台，易用性强。 | 1、社区和文档相对于Quartz可能较小。 |


可以发现没有完美的方案，易用、全面、简单几乎是一个不可能三角。

上手简单，如Spring，功能相对基础，全靠自己改造。

而功能全面，如Quartz、xxl-job 等，又面临配置部署复杂，大材小用，杀鸡用牛刀的窘境。


既然完美方案不可能存在，何不聚焦于核心需求，有所取舍，

开发一套上手简单，功能够用，让开发者专注于业务实现，适合中小型团队绝大多数需求场景的任务执行方案呢，这就是 SmartJob 的开发初衷。


## SmartJob 核心理念：简单、好用、够用

简单：轻量级的功能，极简的配置

好用：易上手，易编码，易拓展

够用：功能实用，界面友好，让管理任务变得直观便捷


同时我们深知，一个团队一个人的能力、精力都是有限的，

因此，SmartJob选择拥抱简单，虽在一定程度上牺牲了功能的全面性，但这也是我们的理念，既然不可能完美，就向简单、好用、够用的方向踏实坚定前行。

### 功能特点

1. 基于 Spring 的任务调度，实现高效的任务调度管理，无需额外依赖。
2. 支持 cron 表达式，支持固定间隔执行。例如：每天10:15发送报表邮件，每隔100秒执行一次
3. 支持灵活的定时任务参数。 例如：有时候需要定时任务按设置的参数执行调试
4. 在分布式环境下，无需额外处理，调度机制能确保任务在多服务、多实例间无缝、不重复执行。
5. 支持动态添加、修改、开启/暂停、执行、查看执行记录等功能，这也是最常见的需求场景。
6. 提供了直观的前端界面，让您能够更清晰地查看和管理定时任务。
7. 如果您倾向于直接操作数据库，也支持直接修改数据库配置，配置变动将自动生效，更加便利灵活。


## 前端管理页面-演示

### 任务列表

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-list.png "0")

### 配置任务

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-cron.png "0")

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-fix.png "0")

### 立即执行

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-run.png "0")

### 查询任务执行记录

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-log.png "0")


## 使用指南


### 一、开启 SmartJob 定时任务管理模块

#### 只需一行，即可开启

【properties】⬇️
```
    smart.job.enabled=true
```


【yaml】⬇️
```
    smart:
      job:
        enabled: true
```


#### 完整配置，参考注释按需配置。

【properties】 ⬇️
```
    # smart job 开关
    smart.job.enabled=true
    # 任务初始化延迟 默认30秒 可选
    smart.job.init-delay=30
    # 定时任务执行线程池数量偶数 默认2 可选
    smart.job.core-pool-size=2
    # 数据库配置检测-开关 默认开启可选（作用是固定间隔读取数据库配置更新配置，关闭后只能重启服务或通过接口修改定时任务，建议开启）
    smart.job.db-refresh-enabled=true
    # 数据库配置检测-执行间隔 默认120秒 可选（smart.job.db-refresh-enabled 开启状态下此项才有意义和作用）
    smart.job.db-refresh-interval=120
```


【yaml】⬇️
```
    smart:
      job:
        enabled: true
        core-pool-size: 2
        db-refresh-enabled: true
        db-refresh-interval: 60
```


### 二、编写/添加定时任务


#### 1、实现接口 SmartJob

业务逻辑写在run()方法内，可参考 net.lab1024.sa.base.module.support.job.sample 下示例类。

##### 示例、SmartJobSample1⬇️
```java

    /**
     * 定时任务 示例1
     *
     * @author huke
     * @date 2024/6/17 21:30
     */
    @Slf4j
    @Service
    public class SmartJobSample1 implements SmartJob {
        /**
         * 定时任务示例
         *
         * @param param 可选参数 任务不需要时不用管
         * @return
         */
        @Override
        public String run(String param) {
            // 写点什么业务逻辑
            return "执行完毕,随便说点什么吧";
        }
    }
```


##### 示例2、SmartJobSample2⬇️
```java

    /**
     * 定时任务 示例2
     *
     * @author huke
     * @date 2024/6/17 21:30
     */
    @Slf4j
    @Service
    public class SmartJobSample2 implements SmartJob {

        @Autowired
        private ConfigDao configDao;

        /**
         * 定时任务示例
         * 需要事务时 添加 @Transactional 注解
         *
         * @param param 可选参数 任务不需要时不用管
         * @return
         */
        @Transactional(rollbackFor = Throwable.class)
        @Override
        public String run(String param) {
            // 随便更新点什么东西
            ConfigEntity configEntity = new ConfigEntity();
            configEntity.setConfigId(1L);
            configEntity.setRemark(param);
            configDao.updateById(configEntity);

            configEntity = new ConfigEntity();
            configEntity.setConfigId(2L);
            configEntity.setRemark("SmartJob Sample2 update");
            configDao.updateById(configEntity);

            return "执行成功,本次处理数据1条";
        }

    }
```


### 2、数据库配置定时任务


方式1：前端页面点击【添加】按钮，填写配置，确认保存即可。

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-cron.png "0")

方式2：在数据表 b\_smart\_job 中新增一条对应的任务配置，主要参数如下

job\_name：任务名称

job\_class：任务实现类，注意需要与代码匹配

trigger\_type：触发类型 cron表达式 或 固定间隔（秒），存储在字段 trigger\_value 中

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-db.png "0")


### 3、启动服务，即可看到 SmartJob 配置信息

![0](https://img.smartadmin.1024lab.net/smart-admin-v3/job/job-start.png "0")

任务状态 enabled\_flag 设置为1开启时，会在预定时间执行任务，同时输出执行日志，b\_smart\_job\_log 也可看到执行记录。

> [2024-06-24 22:10:11,270][INFO ][SmartJobExecutor-0] ==== SmartJob ==== execute job->示例任务1,time-millis->0ms


# 核心原理

### 1、SmartJobScheduler - 任务调度

基于Spring框架的ThreadPoolTaskScheduler。

ThreadPoolTaskScheduler作为Spring提供的强大线程池任务调度器，为SmartJob提供了底层的多线程调度支持。确保每个任务能够按照预设的时间准确无误地执行。

### 2、SmartJobLauncher - 初始化/管理配置、定时任务

在系统启动阶段，负责初始化延迟时间，线程数量等关键参数，以及初始化所有的定时任务，将它们加入到任务调度器中。

同时还承担着检测数据库配置变动的重任。通过启动一个独立的线程，周期性地检查数据库中的任务配置信息，一旦发现配置有所变动（如启动/暂停,修改任务参数等），便能够立即作出响应，更新任务调度器中的任务配置，确保系统始终按照最新的配置运行。 这种机制确保SmartJob在无需接口及前端管理界面时，同样正常运作，提高了 SmartJob 的适应性和动态调整能力。

### 3、SmartJobExecutor - 任务执行

作为任务执行的核心组件，SmartJobExecutor承载着任务执行前后的各项关键逻辑。

在执行任务之前，SmartJobExecutor会先获取任务配置信息，并根据需要获取分布式锁，以确保在分布式环境下任务执行的唯一性和一致性。

在任务执行过程中，SmartJobExecutor会调用用户定义的执行逻辑，并处理可能出现的异常情况。

任务执行完毕后，SmartJobExecutor还会负责保存任务执行记录，包括执行时间、执行参数、执行结果等关键信息，以便后续查询和审计。

### 4、分布式应用调度 - Redis的订阅消息

SmartJob采用了Redis的发布/订阅功能，支持分布式环境下多应用实例间的任务调度管理，不同应用实例间，可以相互通信，共享任务执行的状态和结果。提高任务执行的可靠性，还降低了分布式系统间的耦合度，使系统更加易于扩展和维护。


# 常见问题

### Q：单服务、多服务/多实例 场景下，需要怎么处理？

什么都不需要做，在处理单服务、多服务/多实例场景时，

当前的任务执行机制已设计为：无论任务在多少个服务中运行，同一时间仅触发一次执行，确保避免任何重复操作。同

时，对任务配置的任何更新都会实时同步至所有运行中的实例，保证数据一致性和准确性。


### Q：触发类型 cron 表达式 和 fixedDelay 的选择

适合就行，关键在于业务场景

cron 表达式确保任务在预设的准确时间执行， 例如：每天9点消息推送通知、每天凌晨3点10分生成统计报表 等等

fixedDelay 侧重于任务执行的频率，不特定于某一时间点。例如：每隔100秒清理下缓存、每隔200秒同步下数据 等等

需要注意的是，fixedDelay的首次执行取决于服务启动时间，且后续执行间隔固定，可能导致多实例中定时任务执行时间不同步。


### Q：为什么没有删除定时任务的接口

删除定时任务只涉及数据库变动，实际的任务逻辑代码仍需开发者自行移除。

考虑到SmartJob面对的是开发者，不如直接操作数据库删除定时任务更为便捷。


### Q：想要的功能或者场景无法满足怎么办

受限于时间、需求优先级、紧急程度等因素，诸如：告警通知、失败重试、分片执行等等功能，需要等后续更新计划。

欢迎各位开发同志们的宝贵意见和批评指导，您的建议对我们至关重要。


## 结尾：

最后，衷心希望我们的努力能够助力您的项目开发，解决问题，提高效率。

如果您在使用过程中遇到任何问题、BUG或者有宝贵的建议和反馈，请随时与我们联系，您的支持是我们持续前行的最大动力！

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 数据范围

# 数据范围

## 一、背景与问题

1024实验室下有三个销售部,分别是销售一部、销售二部、销售三部，卓主任要求销售各部门只能看到各部门自己的订单数据，以防数据外泄恶意竞争。

## 二 、具体使用

1. `DataScopeTypeEnum`新增`ORDER`枚举项
2. 创建销售角色，给销售部的人分配此角色
3. 设置此角色的订单业务模块的数据范围为本人
4. 对应订单查询方法（Dao接口方法）添加数据范围注解`@DataScope`注解
```java

    /**
     * 订单分页查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    @DataScope(dataScopeType = DataScopeTypeEnum.ORDER, whereInType = DataScopeWhereInTypeEnum.EMPLOYEE, joinSql = "create_user_id in (#employeeIds)")
    List<OrderVO> queryByPage(Page page, @Param("queryForm") OrderQueryForm queryForm);
```


## 三、`@DataScope`注解说明

### 3.1、@DataScope注解

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| dataScopeType | DataScopeTypeEnum | 定义对应数据范围的业务模块 |
| whereInType | DataScopeWhereInTypeEnum | 定义数据范围Sql拼接的模式是已部门、员工还是自定义的方式判断数据范围 |
| joinSqlImplClazz | DataScopePowerStrategy | 这个是扩展功能，当`whereInType`的值为`CUSTOM_STRATEGY`必填，用于固有功能无法满足需求的情况下，通过实现joinSqlImplClazz的方式来自定义数据范围 |
| paramName | String | 这个同样是扩展功能，用于自定义数据范围策略的实现方法中获取接口参数的内容 |
| whereIndex | int | 默认值0，定义拼接的sql从第几个Where开始 |
| joinSql | String | 拼接的sql语句，非自定义策略此参数必填，目前扩展的参数变量有#departmentIds、#employeeIds |

### 3.2、`joinSql`参数说明

| 参数 | 说明 |
| --- | --- |
| #departmentIds | 当`whereInType`为`DEPARTMENT`时，此参数会自动根据对应用户在此模块的数据范围设置此变量的部门id集合，会自动拼接`()` |
| #employeeIds | 当`whereInType`为`EMPLOYEE`时，此参数会自动根据对应用户在此模块的数据范围设置此变量的员工id集合，会自动拼接`()` |

### 3.3、`joinSqlImplClazz`参数说明
```java

    /**
     * 数据范围策略 ,使用DataScopeWhereInTypeEnum.CUSTOM_STRATEGY类型，DataScope注解的joinSql属性无用
     *
     * @Author 1024创新实验室: 罗伊
     * @Date 2020/11/28  20:59:17
     * @Wechat zhuoda1024
     * @Email lab1024@163.com
     * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
     */
    public abstract class AbstractDataScopeStrategy {

        /**
         * 获取joinsql 字符串
         */
        public abstract String getCondition(DataScopeViewTypeEnum viewTypeEnum, Map<String, Object> paramMap, DataScopeSqlConfig sqlConfigDTO);
    }
```


当通过框架内置的参数变量#departmentIds、#employeeIds无法控制数据范围需要自定义人员或部门的获取方式时，可以使用自定义策略。 自定义策略需要设置`whereInType`的值为`CUSTOM_STRATEGY`，切实现`AbstractDataScopeStrategy`抽象类。

`AbstractDataScopeStrategy`参数说明

| 参数 | 说明 |
| --- | --- |
| viewTypeEnum | 当前业务模块操作人所处的查看类型是哪种（本人、本部门、本部门及下属子部门。全部） |
| paramMap | 当前Dao层方法的参数集合 |
| sqlConfigDTO | 当前Dao层方法@DataScope的注解配置 |

`AbstractDataScopeStrategy`返回说明:

返回值为字符串，具体内容就是需要拼接的sql语句，如`"(w.create_user_id in (1,2,3,4) or o.create_user_id in (11,12,13,14))"`

## 四、实现原理

### 实现原理：

在日常项目开发过程中，通常是在不考虑权限、数据范围的情况下进行开发的，那么怎么实现后期数据范围的动态配置是首选要考虑解决的问题。 通过查看Mybatis项目文档，发现Mybatis具有插件功能，允许你在映射语句执行过程中的某一点进行拦截调用，那么我们可以自定义一个插件 在项目执行查询方法时通过动态拼装一些SQL实现数据访问的动态配置。

注：Mybatis项目文档请查看：`https://mybatis.org/mybatis-3/zh_CN/configuration.html#plugins`

### 实现步骤：

1.自定义数据访问注解@DataScope，用于获取各业务模块数据范围的配置方式。同时为方便获取配置信息我们在项目启动时自动将所有添加过此注解的Mybatis接口方法加入到了系统缓存中。 参考：`DataScopeSqlConfigService#initDataScopeMethodMap`
```java

    /**
     * 刷新 所有添加数据范围注解的接口方法配置<class.method,DataScopeSqlConfigDTO></>
     */
    private Map<String, DataScopeSqlConfig> refreshDataScopeMethodMap() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(AdminApplication.COMPONENT_SCAN)).setScanners(new MethodAnnotationsScanner()));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(DataScope.class);
        for (Method method : methods) {
            DataScope dataScopeAnnotation = method.getAnnotation(DataScope.class);
            if (dataScopeAnnotation != null) {
                DataScopeSqlConfig configDTO = new DataScopeSqlConfig();
                configDTO.setDataScopeType(dataScopeAnnotation.dataScopeType());
                configDTO.setJoinSql(dataScopeAnnotation.joinSql());
                configDTO.setWhereIndex(dataScopeAnnotation.whereIndex());
                configDTO.setDataScopeWhereInType(dataScopeAnnotation.whereInType());
                configDTO.setParamName(dataScopeAnnotation.paramName());
                configDTO.setJoinSqlImplClazz(dataScopeAnnotation.joinSqlImplClazz());
                dataScopeMethodMap.put(method.getDeclaringClass().getSimpleName() + "." + method.getName(), configDTO);
            }
        }
        return dataScopeMethodMap;
    }

    /**
     * 根据调用的方法获取，此方法的配置信息
     *
     */
    public DataScopeSqlConfig getSqlConfig(String method) {
        return this.dataScopeMethodMap.get(method);
    }

    /**
     * 组装需要拼接的sql
     */
    public String getJoinSql(Map<String, Object> paramMap, DataScopeSqlConfig sqlConfigDTO) {
        DataScopeTypeEnum dataScopeTypeEnum = sqlConfigDTO.getDataScopeType();
        String joinSql = sqlConfigDTO.getJoinSql();
      	// 获取当前请求的用户信息
        Long employeeId = SmartRequestUtil.getRequestUserId();
        // 由于数据范围依托于员工信息，如果员工不存在时，就没必要拼装sql了
        if (employeeId == null) {
            return "";
        }
        // 使用的是自定义策略的情况
        if (DataScopeWhereInTypeEnum.CUSTOM_STRATEGY == sqlConfigDTO.getDataScopeWhereInType()) {
            Class strategyClass = sqlConfigDTO.getJoinSqlImplClazz();
            if (strategyClass == null) {
                log.warn("data scope custom strategy class is null");
                return "";
            }
            // 获取当前自定义策略的bean对象
            AbstractDataScopeStrategy powerStrategy = (AbstractDataScopeStrategy) applicationContext.getBean(sqlConfigDTO.getJoinSqlImplClazz());
            if (powerStrategy == null) {
                log.warn("data scope custom strategy class：{} ,bean is null", sqlConfigDTO.getJoinSqlImplClazz());
                return "";
            }
          	// 获取当前员工的可见范围
            DataScopeViewTypeEnum viewTypeEnum = dataScopeViewService.getEmployeeDataScopeViewType(dataScopeTypeEnum, employeeId);
          	// 执行自定义策略，返回需要拼装的sql
            return powerStrategy.getCondition(viewTypeEnum,paramMap, sqlConfigDTO);
        }
        // 以员工为维度进行可见范围判断的时候
        if (DataScopeWhereInTypeEnum.EMPLOYEE == sqlConfigDTO.getDataScopeWhereInType()) {
           	// 通过系统预定义的查询可见范围的方法，获取当前员工可以看到哪些人员的数据
            List<Long> canViewEmployeeIds = dataScopeViewService.getCanViewEmployeeId(dataScopeTypeEnum, employeeId);
            if (CollectionUtils.isEmpty(canViewEmployeeIds)) {
                return "";
            }
            String employeeIds = StringUtils.join(canViewEmployeeIds, ",");
          	// 将查询到的人员id替换掉系统预定义的参数#employeeIds
            String sql = joinSql.replaceAll(EMPLOYEE_PARAM, employeeIds);
            return sql;
        }
       // 以部门为维度进行可见范围判断的时候
        if (DataScopeWhereInTypeEnum.DEPARTMENT == sqlConfigDTO.getDataScopeWhereInType()) {
          	// 通过系统预定义的查询可见范围的方法，获取当前员工可以看到哪些部门的数据
            List<Long> canViewDepartmentIds = dataScopeViewService.getCanViewDepartmentId(dataScopeTypeEnum, employeeId);
            if (CollectionUtils.isEmpty(canViewDepartmentIds)) {
                return "";
            }
            String departmentIds = StringUtils.join(canViewDepartmentIds, ",");
          	// 将查询到的人员id替换掉系统预定义的参数#departmentIds
            String sql = joinSql.replaceAll(DEPARTMENT_PARAM, departmentIds);
            return sql;
        }
        return "";
    }
```


2.自定义Mybatis插件，用于各业务模块在执行Sql查询时动态拼接Sql。实现数据范围的核心代码在此模块，接下来我们来详细看下此部分代码
```java

public Object intercept(Invocation invocation) throws Throwable {
				// 获取当前执行的MappedStatement对象，在MyBatis每一个<select>, <insert>, <update>, <delete>标签都会被解析成一个MappedStatement对象
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
  			// 获取当前调用Dao接口方法的参数信息
        Object parameter = invocation.getArgs()[1];
				// 获取此次执行的Sql信息
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql().trim();
  			// 获取此次调用MappedStatement对象的ID,此ID用于获取第一步存入到系统缓存中配置内容，系统的缓存key是【类名.方法名】
        String id = mappedStatement.getId();
        List<String> methodStrList = StrUtil.split(id, ".");
        String path = methodStrList.get(methodStrList.size() - 2) + "." + methodStrList.get(methodStrList.size() - 1);
  			// 通过applicationContext获取系统配置Service
        DataScopeSqlConfigService dataScopeSqlConfigService = this.dataScopeSqlConfigService();
  			// 未获取到 Mybatis直接按照当前XML内的SQL内容进行处理
        if (dataScopeSqlConfigService == null) {
            return invocation.proceed();
        }
  			// 获取系统配置Service中的配置信息
        DataScopeSqlConfig sqlConfigDTO = dataScopeSqlConfigService.getSqlConfig(path);
        if (sqlConfigDTO != null) {
            // 获取当前执行Dao的参数信息
            Map<String, Object> paramMap = this.getParamList(sqlConfigDTO.getParamName(), parameter);
          	// 获取拼装过最新SQL语句的BoundSql
            BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql, this.joinSql(originalSql, paramMap, sqlConfigDTO));
            ParameterMap map = mappedStatement.getParameterMap();
          	// 获取最新MappedStatement
            MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql), map);
            invocation.getArgs()[0] = newMs;
        }
				// Mybatis执行返回结果
        Object obj = invocation.proceed();
        return obj;
    }


    private Map<String, Object> getParamList(String paramName, Object parameter) {
        Map<String, Object> paramMap = Maps.newHashMap();
        if (StringUtils.isEmpty(paramName)) {
            return paramMap;
        }
        if (parameter == null) {
            return paramMap;
        }
        if (parameter instanceof Map) {
            String[] paramNameArray = paramName.split(",");
            Map<?, ?> parameterMap = (Map) parameter;
            for (String param : paramNameArray) {
                if(parameterMap.containsKey(param)){
                    paramMap.put(param, parameterMap.get(param));
                }
            }
        }
        return paramMap;
    }

    private String joinSql(String sql, Map<String, Object> paramMap, DataScopeSqlConfig sqlConfigDTO) {
        if (null == sqlConfigDTO) {
            return sql;
        }
      	// 获取需要拼装的Sql语句
        String appendSql = this.dataScopeSqlConfigService().getJoinSql(paramMap, sqlConfigDTO);
        if (StringUtils.isEmpty(appendSql)) {
            return sql;
        }
        // 获取Sql语句需要拼接在第几个Where语句后面
        Integer appendSqlWhereIndex = sqlConfigDTO.getWhereIndex();
        String where = "where";
        String order = "order by";
        String group = "group by";
        // 获取where关键字的索引位置
        int whereIndex = StringUtils.ordinalIndexOf(sql.toLowerCase(), where, appendSqlWhereIndex + 1);
        int orderIndex = sql.toLowerCase().indexOf(order);
        int groupIndex = sql.toLowerCase().indexOf(group);
      	// 原始sql存在where关键字的时候
        if (whereIndex > -1) {
            String subSql = sql.substring(0, whereIndex + where.length() + 1);
            subSql = subSql + " " + appendSql + " AND " + sql.substring(whereIndex + where.length() + 1);
            return subSql;
        }
				// 原始sql不存在where关键字，但存在group by的时候
        if (groupIndex > -1) {
            String subSql = sql.substring(0, groupIndex);
            subSql = subSql + " where " + appendSql + " " + sql.substring(groupIndex);
            return subSql;
        }
      	// 原始sql不存在where group by关键字，但存在order by的时候
        if (orderIndex > -1) {
            String subSql = sql.substring(0, orderIndex);
            subSql = subSql + " where " + appendSql + " " + sql.substring(orderIndex);
            return subSql;
        }
        sql += " where " + appendSql;
        return sql;
    }
	
		public DataScopeSqlConfigService dataScopeSqlConfigService() {
        return (DataScopeSqlConfigService) applicationContext.getBean("dataScopeSqlConfigService");
    }
```


3.将自定义的插件加入到数据源中，参考`DataSourceConfig#sqlSessionFactory`
```java

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(druidDataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:/mapper/**/*.xml");
        factoryBean.setMapperLocations(resources);

        // 设置 MyBatis-Plus 分页插件 注意此处myBatisPlugin一定要放在后面,特别注意以防连接器被覆盖
        List<Interceptor> pluginsList = new ArrayList<>();
      	// 分页插件
        pluginsList.add(paginationInterceptor);
        if (dataScopePlugin != null) {
          	// 数据范围插件
            pluginsList.add(dataScopePlugin);
        }
        factoryBean.setPlugins(pluginsList.toArray(new Interceptor[pluginsList.size()]));
        // 添加字段自动填充处理
        factoryBean.setGlobalConfig(new GlobalConfig().setBanner(false).setMetaObjectHandler(new MybatisPlusFillHandler()));

        return factoryBean.getObject();
    }
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 支持所有云的文件上传

# 支持所有云的文件上传

## 一、背景与问题

文件上传是一个很基本的需求，但是每个公司使用的云计算可能不一样，比如有些公司用的是阿里云、华为云、腾讯云、联通、电信等等；但是无论是哪一种云，其实他们都是遵循的 亚马逊的 S3 文件存储协议。

* 支持阿里云、华为云、腾讯云等各类云
* 同时支持 minio，因为minio也是支持 S3协议的
* 如果是私有化部署，还得支持 本地存储

## 二、架构与思想

* 使用S3协议作为基础进行设计，目的为了满足所有云。
* 记录所有的上传文件，存到数据库
* 保留文件的基础信息： 大小，文件名，时间等等
* 区分 公共文件和私有文件

## 三、具体使用

### 3.1、修改配置

SmartAdmin支持的文件上传模式有local、cloud两种，文件上传接口参考：`FileController`。

* `local`：为本地文件上传，文件存储在服务器本地
* `cloud`: 为云文件存储，目前支持主流的云存储厂商阿里云、华为云、七牛云、minio等支持S3协议

**1）修改为本地 local存储**

第一步将 `file.storage.mode` 改为 `local`
```yaml

# 文件上传 配置
file:
  storage:
    mode: local
```


第二步，配置具体的 `local` 参数： `upload-path` 和 `url-prefix`

* `upload-path` 为上传文件的存放路径，可以自行根据需要修改 ; `url-prefix`为访问这些文件的url前缀：
* `url-prefix`默认可以为空，为空情况下会使用`springboot`的`addResourceHandler`进行静态资源映射，默认映射到`/upload`的url路径，比如`http://192.168.3.188:1024/upload/public/common/a1.png` ,具体配置请看类`FileConfig`, 配置如下
```yaml

# 默认为空
file:
  storage:
    mode: local
    local:
      upload-path: ${localPath:/home}/smart_admin_v3/upload/  #上传路径
      url-prefix:                                             #url前缀，可以为空，系统会默认为 http://[ip][port]/upload/[fileKey]
```


* `url-prefix` 可以为nginx映射路径等，比如在nginx配置了映射，映射到`upload-path`配置的路径
```yaml

file:
  storage:
    mode: local
    local:
      upload-path: ${localPath:/home}/smart_admin_v2/upload/  #上传路径
      url-prefix: https://smartadmin.vip/upload               #使用nginx映射的路径
```


**2）阿里云、腾讯云、火山云等**
```yaml

# 文件上传 配置
file:
  storage:
    mode: cloud                                    # cloud 为云存储；local 为本地存储，则下面的cloud配置将失效
    cloud:
      region: oss-cn-hangzhou                      # 自行修改
      endpoint: https://oss-cn-hangzhou.aliyuncs.com        # 自行修改
      bucket-name: 1024lab-smart-admin                  # 自行修改
      access-key:                                  # 自行修改
      secret-key:                                  # 自行修改
      private-url-expire-seconds: 3600            # url失效时间
      # 云计算厂商支持公开的文件访问模式；minio默认是不支持的，对于minio用户可以配置为空
      public-url-prefix: https://1024lab-smart-admin.oss-cn-hangzhou.aliyuncs.com/
```


**3）minio 配置**
```yaml

# 文件上传 配置
file:
  storage:
    mode: cloud                                    # cloud 为云存储；local 为本地存储，则下面的cloud配置将失效
    cloud:
      region: us-east-1                            # minio 默认就是 us-east-1
      endpoint: http://192.168.1.124:9000          # 自行修改
      bucket-name: 1024lab-smart-admin             # 自行修改
      access-key:                                  # 自行修改
      secret-key:                                  # 自行修改
      private-url-expire-seconds: 3600            # url失效时间
      # 云计算厂商支持公开的文件访问模式；minio默认是不支持的，对于minio用户可以配置为空
      public-url-prefix:
```


### 3.2、定义文件存放位置

如果所有文件都存放到一起，那么后续想做个分类都好方便，所以这里需要定义一下 文件目录 `FileFolderTypeEnum.java`，建议按照功能业务大块拆分：
```java

public enum FileFolderTypeEnum implements BaseEnum {
    COMMON(1, FileFolderTypeEnum.FOLDER_PUBLIC + "/common/", "通用"),
    NOTICE(2, FileFolderTypeEnum.FOLDER_PUBLIC + "/notice/", "公告"),
    HELP_DOC(3, FileFolderTypeEnum.FOLDER_PUBLIC + "help-doc", "帮助中心"),
    FEEDBACK(4, FileFolderTypeEnum.FOLDER_PUBLIC + "/feedback/", "意见反馈"),
    ;
```


### 3.3、上传

**对应前端组件**
 在前端提供了`file-preview`、`file-preview-modal`、`file-upload` 三个文件相关的组件可供使用；

**序列化与反序列化**
`@JsonDeserialize(using = FileKeyVoDeserializer.class)` 此反序列化，用于将前端传输的`fileVO`的JSON数组转化为可供数据库直接存储的`fileKey`字符串。
`@JsonSerialize(using = FileKeyVoDeserializer.class)` 此序列化，用于将`fileKey`字符串转化为可供前端直接使用的`fileVO`的JSON数组。
**在业务上逗号分割**
 在数据存储上一般在对应的商品表中创建`cover_pic`字段，此字段用于存储文件key信息，多张图片的话，一般采用逗号分割的方式存储此字段。 字段定义方式：
```java

    @ApiModelProperty("商品封面")
    @Length(max = 250, message = "商品封面最多250字符")
    @JsonSerialize(using = FileKeyVoSerializer.class)
    @JsonDeserialize(using = FileKeyVoDeserializer.class)
    private String coverPic;
```


SmartAdmin的前端文件上传组件，返回的JSON数据是以`fileVO`JSON数组的方式返回的，为了减少前后端数据二次处理的繁琐工作，特意增加了JSON的序列化和反序列化处理。
 除了上面的两个序列化类外，还提供了`FileKeySerializer.class`，此类可将`fileKey`字符串转化为文件请求全路径地址。

比如 反序列化:
```java

@Data
public class NoticeUpdateFormVO extends NoticeVO {

    @ApiModelProperty("附件")
    @JsonSerialize(using = FileKeyVoSerializer.class)
    private String attachment;

    @ApiModelProperty("可见范围")
    private List<NoticeVisibleRangeVO> visibleRangeList;
}
```


比如 序列化:
```java

@Data
public class NoticeDetailVO {

    @ApiModelProperty("id")
    private Long noticeId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("附件")
    @JsonSerialize(using = FileKeyVoSerializer.class)
    private String attachment;
```


## 四、实现原理

### 4.1、表结构

表结构用于记录：文件基本信息：大小、文件名、时间、上传人信息，表设计如下

|  |
| --- |
|  |

### 4.2、亚马逊S3协议

亚马逊S3协议有java库：
```xml

<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
  <version>最新</version>
</dependency>
```


### 4.3、文件实现类

为了满足 本地、云存储 多种方式，系统中定义了 接口`IFileStorageService` 文件接口；
 并提供两种实现类:
```java

FileStorageCloudServiceImpl.java   使用亚马逊S3协议的实现类
FileStorageLocalServiceImpl.java   本地存储实现类
```


具体如何判断使用本地存储实现类还是使用 云S3存储实现类，请看 `sa-base`项目中的 `FileCloudConfig`,使用了条件注解`@ConditionalOnProperty`，代码如下：
```java

   @Bean
    @ConditionalOnProperty(prefix = "file.storage", name = {"mode"}, havingValue = "cloud")
    public IFileStorageService initCloudFileService() {
        return new FileStorageCloudServiceImpl();
    }

    @Bean
    @ConditionalOnProperty(prefix = "file.storage", name = {"mode"}, havingValue = "local")
    public IFileStorageService initLocalFileService() {
        return new FileStorageLocalServiceImpl();
    }
```


### 4.4、 缓存

知道，对于某些私有化的文件，当访问的时候需要`后端`请求`云计算`生成一个可以访问的 `url地址`，并且这个url地址有个`过期时间`；
 但是文件服务又是一个很基础的服务，获取访问地址，后端需要发请求，是`阻塞的`，如果频繁的调用，会很慢，所以做了一个redis缓存；
```java

    private String getCacheUrl(String fileKey) {
        String redisKey = redisService.generateRedisKey(RedisKeyConst.Support.FILE_URL, fileKey);
        String fileUrl = redisService.get(redisKey);
        if (null != fileUrl) {
            return fileUrl;
        }
        ResponseDTO<String> responseDTO = fileStorageService.getFileUrl(fileKey);
        if (!responseDTO.getOk()) {
            return null;
        }
        fileUrl = responseDTO.getData();
        redisService.set(redisKey, fileUrl, fileStorageService.cacheExpireSecond());
        return fileUrl;
    }
```


### 4.5、 文件key生成规则

32位 uuid + 文件格式后缀

比如：`c0e6e9340a8c4c4aa8c8062bdc5f8bcc.png`

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 多环境配置

# 多环境配置

## 1、背景与问题

虽然spring-boot与spring-cloud家族有默认的parent pom，但是由于spring-boot和spring-cloud系列更新太过频繁，所以对于一个技术团队而言，设置自己的独有的parent才能做到整个项目亦或整个团队的版本统一，技术统一。

## 2、架构与思想

### 2.1、MAVEN BOM

*如果你只听过maven的pom，但是你没听过maven的bom的话，而且你还在用spring技术体系的话，那么你非常强烈建议你去了解bom。*
 使用BOM可以让使用者在子pom.xml声明依赖的时不需要指定版本号，最重要的是可以解决依赖冲突。
 而不幸的是 spring 系列相关的依赖经常冲突，不用bom能行么?

### 2.2、依赖版本统一定义版本

在parent的pom.xml文件里定义了所有的版本变量，这样可以只关注此区域，不用再看下面，因为下面`<dependencies>`节点里很长，翻阅起来很累又不易查看，而且随着系统的变大也会越来越长，但是`<properties>`节点不会。
```xml

  <properties>
        <java.version>1.8</java.version>
        <springboot.version>2.7.5</springboot.version>
        <spring-mock.version>2.0.8</spring-mock.version>
        <mybatis-plus.version>3.5.2</mybatis-plus.version>
        <p6spy.version>3.8.6</p6spy.version>
        ...
</properties>
```


## 三、多环境

### 3.1、maven profile多环境

笔者认为所有的项目都应该有以下四个环境:

* 开发环境 （dev,用于写代码）
* 测试环境 （test，测试人员测试）
* 预发布环境（pre, 真实的数据，最真实的生产环境）
* 生产环境（prod, 生产环境）

默认为dev环境，即开发环境。

配置如下：
```xml

 <profiles>
        <profile>
            <id>dev</id>
            <properties>
                <profiles.active>dev</profiles.active>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <profile>
            <id>sit</id>
            <properties>
                <profiles.active>sit</profiles.active>
            </properties>
        </profile>
        <profile>
            <id>pre</id>
            <properties>
                <profiles.active>pre</profiles.active>
            </properties>
        </profile>
        <profile>
            <id>prod</id>
            <properties>
                <profiles.active>prod</profiles.active>
            </properties>
        </profile>
    </profiles>
```


### 3.2、目录resources

因为使用了maven profile，所以必须在resources目录下建立不同环境的配置文件夹，如下图。
```java

--------\src\main\resources
--------\src\main\resources\dev
--------\src\main\resources\test
--------\src\main\resources\pre
--------\src\main\resources\prod
```


### 3.3、 打包配置build

maven profile的打包核心是对配置文件的过滤，如下
```xml

<finalName>${profiles.active}-${project.name}</finalName>
<resources>
    <resource>
        <filtering>false</filtering>
        <directory>src/main/resources</directory>
        <excludes>
            <exclude>dev/*</exclude>
            <exclude>test/*</exclude>
            <exclude>pre/*</exclude>
            <exclude>prod/*</exclude>
        </excludes>
    </resource>

    <resource>
        <directory>src/main/resources/${profiles.active}</directory>
        <filtering>true</filtering>
        <includes>
            <include>*.yaml</include>
        </includes>
    </resource>

    <resource>
        <directory>src/main/resources/${profiles.active}</directory>
        <filtering>false</filtering>
        <includes>
            <include>*.*</include>
        </includes>
    </resource>
</resources>
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 数据变更记录

# 数据变更记录

## 一、背景与问题

对于中后台系统中的数据都是非常重要的，但是如有人不小心修改了数据，异或有意而为之等等，这样都会对系统造成很大的影响，甚至对于公司可能也会造成一些影响。所以对于一个个重要的数据但凡谁去改动，都应该有详细的记录变更，就好比大家熟悉的git一样，任何变动都有对应的记录。
 那么具体需要记录哪些呢？

* 时间：什么时候修改的
* 用户：具体谁修改的
* 设备：在哪个设备、ip等修改的
* 修改前：修改之前的数据
* 修改后：修改之后的数据

## 二、架构与思想

具体前端的架构设计请看[前端数据变动记录设计](./../front/DataTracer.html) ;
 对于后端而言应该做到如下：

* 对于一些简单变动， 只需记录一些 简单的字符串就可以
* 对于复杂javabean对象，需要进行 javabean对象的比较操作，比较的结果作为字符串存起来
* 任何数据都有： 新增、修改、删除 ；这三样类型应该默认提供

## 三、具体使用

### 3.1、定义数据业务类型

在`DataTracerTypeEnum.java` 中定义自己的业务类型，如下:
```java

@AllArgsConstructor
@Getter
public enum DataTracerTypeEnum implements BaseEnum {

    GOODS(1, "商品"),
    OA_NOTICE(2, "OA-通知公告"),
    OA_ENTERPRISE(3, "OA-企业信息");

    private final Integer value;
    private final String desc;
}
```


### 3.2、JavaBean注解

提供字段的如下几种注解用于 两个javabean之间的对象比较，生成满足`git diff`格式的对比数据：

* `@DataTracerFieldBigDecimal` 用于 BigDecimal 类型字段
* `@DataTracerFieldDict` 用于 字典 类型字段
* `@DataTracerFieldEnum` 用于 枚举 类型字段
* `@DataTracerFieldLabel` 用于 字段名称
* `@DataTracerFieldSql` 用于 sql 查询注入字段

比如：`EnterpriseEntity.java`的javabean：
```java

@Data
@TableName("t_oa_enterprise")
public class EnterpriseEntity {

    @TableId(type = IdType.AUTO)
    private Long enterpriseId;

    @DataTracerFieldLabel("企业名称")
    private String enterpriseName;

    @DataTracerFieldLabel("企业logo")
    private String enterpriseLogo;

    @DataTracerFieldLabel("统一社会信用代码")
    private String unifiedSocialCreditCode;

    @DataTracerFieldLabel("类型")
    @DataTracerFieldEnum(enumClass = EnterpriseTypeEnum.class)
    private Integer type;

    ......
}
```


通过`DataTracerService.getChangeContent(enterpriseDetail)` 方法，可以拿到具体的对象内容

### 3.3、 新增、删除、修改

任何数据都有： 新增、修改、删除 ；这三样类型应该默认提供如下方法： `DataTracerService.java`中，如下方法：
```java

// 新增
DataTracerService.insert(1,DataTracerTypeEnum.GOODS); // 新增商品 记录
// 更新
DataTracerService.update(1,DataTracerTypeEnum.GOODS, oldGoods, newGoods); // 更新商品，传入新、旧 对象
// 删除
DataTracerService.delete(1,DataTracerTypeEnum.GOODS); // 删除商品 记录
DataTracerService.batchDelete(1,DataTracerTypeEnum.GOODS); // 批量删除商品 记录
```


### 3.4、其他记录

任何数据除了： 新增、修改、删除 ，还有其他操作记录，这个时候需要用到：
```java

DataTracerService.addTrace(...); // 添加数据痕迹  方法
```


比如，企业信息中的调用`EnterpriseService.updateEnterprise`方法中：
```java

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> updateEnterprise(EnterpriseUpdateForm updateVO) {
        Long enterpriseId = updateVO.getEnterpriseId();
        // 校验企业是否存在
        EnterpriseEntity oldEnterprise = enterpriseDao.selectById(enterpriseId);
        if (Objects.isNull(oldEnterprise) || oldEnterprise.getDeletedFlag()) {
            return ResponseDTO.userErrorParam("企业不存在");
        }
        // 数据编辑
        EnterpriseEntity newEnterprise = SmartBeanUtil.copy(oldEnterprise, EnterpriseEntity.class);
        SmartBeanUtil.copyProperties(updateVO, newEnterprise);
        enterpriseDao.updateById(newEnterprise);

        //变更记录
        DataTracerForm dataTracerForm = DataTracerForm.builder()
                .dataId(updateVO.getEnterpriseId())
                .type(DataTracerTypeEnum.OA_ENTERPRISE)
                .content("修改企业信息")
                .diffOld(dataTracerService.getChangeContent(oldEnterprise))
                .diffNew(dataTracerService.getChangeContent(newEnterprise))
                .build();

        dataTracerService.addTrace(dataTracerForm);
        return ResponseDTO.ok();
    }
```


## 四、实现原理

### 4.1、表结构

`t_data_tracer`表

|  |
| --- |
|  |

其中 `git diff` 主要面向`diff_old`和`diff_new` 两个字段的比较

### 4.2、解析Javabean

知道比较`javabean对象`利用反射就可以解析，但是无法知道一些特殊事项：

* 比如 `enterpriseName`字段的中文名称是什么意思？给用户显示总不能显示`enterpriseName`,应该显示`企业名称`字样
* 比如 对于一些枚举值，用户希望显示具体的中文含义，而不是 数值
* 比如 字典 字段，用户希望显示 字典的中文含义，而不是字典值
* 比如 关联关系id，用户希望显示 关联的对象信息，而不是 关联id

以上几个问题，使用几个注解来对应解决的，如下：
```java

@DataTracerFieldBigDecimal 用于 BigDecimal 类型字段
@DataTracerFieldDict 用于 字典 类型字段
@DataTracerFieldEnum 用于 枚举 类型字段
@DataTracerFieldLabel 用于 字段名称
@DataTracerFieldSql 用于 sql 查询注入字段
```


具体如何解析这些注解，请看`DataTracerChangeContentService.java`

### 4.3、其他

整个`datatracer`模块在 `sa-base`项目中的 `support.datatracer`包;
 具体使用可以查看`sa-admin`项目中的`net.lab1024.sa.admin.module.business.oa.enterprise`包

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### manager层是个什么鬼

# manager层是个什么鬼

## 一、问题与背景

> **背景：因某些原因回到洛阳后，本土it企业均为中小企业（小于500人），人员技术水平参差不齐，那么对于这类公司技术这块到底该怎么做？沿用大公司的架构还是顺从当前公司开发人员的理念，到底怎么做才是最好，曾经纠结过很长时间。但后来随着时间变化，领悟一个道理：需要找到一个折中的办法，不能完全照搬大公司架构，要符合企业的情况，形成特色（就像祖国一样，具有中国特色的社会主义），特色很重要！**

传统的SpringMVC架构，分为Controller，Service，DAO三层。Controller控制页面逻辑，业务逻辑和事务在Service层，数据库操作通过编写sql在DAO层。

其实这样的架构非常简洁也容易上手，但是会有如下的一些问题：

1. service层代码臃肿
2. service层事务嵌套，导致问题狠多
3. dao层参杂业务
4. dao层sql语句复杂，关联查询比较多
5. dao层经常改来改去

## 二、使用manager层解决

引自《阿里规约》：

> Manager 层：通用业务处理层，它有如下特征：
>
> 1. 对第三方平台封装的层，预处理返回结果及转化异常信息；
> 2. 对 Service 层通用能力的下沉，如缓存方案、中间件通用处理；
> 3. 与 DAO 层交互，对多个 DAO 的组合复用。

因为特色，所以在分层这块，最终还是选择阿里的架构：分为四层，如下：

* controller
* service
* manager
* dao

**1024创新实验室**把manager层追加下面的功能：

* **复杂业务，service提供数据给Manager层，然后把事务下沉到Manager层**
* **Manager层不允许相互调用（即不同业务下的manager禁止调用，如 用户 UserManager 禁止调用 订单 OrderManager ），以避免事务嵌套**
* **专注于不带业务sql语言，也可以在manager层进行通用业务的dao层封装**
* **避免复杂的join查询，数据库压力比java大很多，所以要严格控制好sql，所以可以在manager层进行拆分，比如复杂查询**
* **可以在manager层使用mybatis-plus的 BaseService，因为 manager层不会被其他业务调用，所以不会引起其他业务看到更多的BaseService方法**

## 三、使用举例

操作多张表进行统一事务管理

* `net.lab1024.sa.admin.module.system.role.manager.RoleMenuManager`
* `net.lab1024.sa.admin.module.system.employee.manager.EmployeeManager`

代码如下：
```java

@Service
public class RoleMenuManager extends ServiceImpl<RoleMenuDao, RoleMenuEntity> {

    @Resource
    private RoleMenuDao roleMenuDao;

    /**
     * 更新角色权限
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenu(Long roleId, List<RoleMenuEntity> roleMenuEntityList) {
        // 根据角色ID删除菜单权限
        roleMenuDao.deleteByRoleId(roleId);
        // 批量添加菜单权限
        saveBatch(roleMenuEntityList);
    }
}
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 系统环境

# 系统环境

## 一、问题与背景

当java进程运行的时候，希望了解到当前处于什么环境？ 开发还是测试、还是生产等等，因为需要在不同的环境做一些不同的操作，举个最简单例子，如果接口报错的话，开发环境中、测试环境中 可以在接口中返回 java的异常报错信息，这样在开发、测试阶段可以直接定位到问题。

## 二、架构与思想

想要实现在运行的时候知道是什么环境，那么肯定需要在启动的时候，将 环境信息 注入或者传入 到java项目中。
 根据的项目结构，清楚的知道 用的是 `maven profile` 来实现的多环境问题。
 那么完全可以利用这一点来去做。

## 三、具体使用

### 3.1、配置

在 `sa-admin`项目的 `application.yaml` 中 有如下配置：
```yaml

spring:
  profiles:
    active: '@profiles.active@'
```


这个地方不需要修改， maven会自动将 `maven package -P xxx` 打包命令的 `profile` 注入进来

### 3.2、代码中使用

在需要判断环境的地方，直接注入变量：
```java

    @Autowired
    private SystemEnvironment systemEnvironment;
```


具体方法如下：
```java

systemEnvironment.isProd(); // 是否为 生产 环境
systemEnvironment.getProjectName(); // 获取 项目 名称
systemEnvironment.getCurrentEnvironment(); // 获取当前项目环境， 返回 一个枚举： SystemEnvironmentEnum
```


使用举例，如果是生产环境，则只是提示参数错误，如果不是生产环境，则将 Exception 返回给前端：
```java

    /**
     * json 格式错误 缺少请求体
     */
    @ResponseBody
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseDTO<?> jsonFormatExceptionHandler(Exception e) {
        if (!systemEnvironment.isProd()) {
            log.error("全局JSON格式错误异常,URL:{}", getCurrentRequestUrl(), e);
        }
        return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数JSON格式错误");
    }
```


## 四、实现原理

使用 `@Configuration` 进行配置，具体配置类如下：`SystemEnvironmentConfig.java`;
 该类在项目启动的时候，会进行环境匹配判断，如果没有配置的，项目无法启动，具体请看如下代码：
```java

@Configuration
public class SystemEnvironmentConfig implements Condition {

    @Value("${spring.profiles.active}")
    private String systemEnvironment;

    @Value("${project.name}")
    private String projectName;

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = conditionContext.getEnvironment().getProperty("spring.profiles.active");
        return StringUtils.isNotBlank(property) && !SystemEnvironmentEnum.PROD.equalsValue(property);
    }

    @Bean
    public SystemEnvironment initEnvironment() {
        SystemEnvironmentEnum currentEnvironment = SmartEnumUtil.getEnumByValue(systemEnvironment, SystemEnvironmentEnum.class);
        if (currentEnvironment == null) {
            throw new ExceptionInInitializerError("无法获取当前环境！请在 application.yaml 配置参数：spring.profiles.active");
        }
        if (StringUtils.isBlank(projectName)) {
            throw new ExceptionInInitializerError("无法获取当前项目名称！请在 application.yaml 配置参数：project.name");
        }
        return new SystemEnvironment(currentEnvironment == SystemEnvironmentEnum.PROD, projectName, currentEnvironment);
    }
}
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 返回错误码

# 返回错误码

## 一、需求与背景

错误码这个就不多说需求了，说一下这两年的一个场景，作为后端Coder，经常会遇到这样的情况：

> 测试或者客服、客户 找来：“你的XX 出Bug了，xxxx没有反应？”
>  测试上：“看一个接口，接口返回：xxxx”，
>  测试问后端：“是不是你的问题？”
>  后端此时憋大件，还差500神装，被迫一顿操作，发现不是他的问题，是用户的误操作问题等等，错失超神。
>  然后怼了一顿测试，“这是xxx的问题，找做什么....”

以上是一个小情景，但是很真实。怎么解决呢？

## 二、架构与思想

### 2.1、错误码分类的意义

这里将错误码定义为了三类：

* 第一类：系统错误，system （即后台报错了，抛异常了）
* 第二类：未预期到的错误，unexpected（比如用户的钱对应不上了，异或 该有这个奖品，后来某个“信球”给删了，即后台发生了不该发生的事情，超乎寻常！）
* 第三类：用户级别错误，user （比如表单验证错误，用户不满足抽奖条件）

根据以上三类，对于即将神装的，如果测试能够告诉是那一种类型的错误码就好了，如果是第三类，肯定就超神 Penta Kill 异或 Rampage 了。

### 2.2、返回码维护的好处

考虑下分布式的场景，服务比较多，场景比较多，业务相对复杂点，当你调用其他服务的接口，需要在某个特定的场景下做一些事情的时候，需要怎么区分，或者可以看看其他开放平台接口。比如微信,支付宝等等，你会发现都会对不同的返回结果有个特殊的返回码。

所以如果对于一个长期维护的产品而言，把返回码维护好是非常重要的和必要的。

**返回码维护的好处：**

* 便于长期维护
* 避免在java代码中直接写字符串，不符合阿里规约
* 便于将来的服务拆分和扩展
* 便于与其他系统进行对接和开放接口
* 便于前端做更加细致的操作
* 暂时想到这么多

## 三、具体使用

### 3.1、三个错误码类

* 系统错误： SystemErrorCode.java
* 未预期到的错误： UnexpectedErrorCode.java
* 用户错误： UserErrorCode.java

### 3.2、ResponseDTO

在`sa-base`项目中有一个核心的javabean类，即`ResponseDTO`，这个是返回前端对象的封装；
```java

public class ResponseDTO<T> {
    private Integer code; //返回码： 0 成功；不是0，不成功
    private String level;// 分类：系统错误，未预期到的错误，用户错误； 如果正确，则为空
    private String msg;//消息
    private Boolean ok;//是否正确返回
    private T data;//返回数据
｝
```


**常用方法：**
```java

//------- 成功方法 使用 --------------
outline: 'deep'
ResponseDTO.ok();  //返回成功
ResponseDTO.ok(resultObject);  //返回成功，并且 data 为 resultObject
ResponseDTO.okMsg(msg);  //返回成功，并且 msg 为 msg

//------- 返回错误码 使用 --------------
outline: 'deep'
ResponseDTO.error(UserErrorCode.LOGIN_STATE_INVALID);// 直接返回错误码
ResponseDTO.errorData(UserErrorCode.LOGIN_STATE_INVALID, errorObject);// 直接返回错误码，并附带 data信息
..还有其他方法..

//------- 最常用的 用户参数 错误码  --------------
outline: 'deep'
ResponseDTO.userErrorParam(); //用户参数错误
ResponseDTO.userErrorParam(msg); //用户参数错误，并附带提示信息
```


`GoodsService.java` 实际使用举例：
```java

  private ResponseDTO<String> checkGoods(GoodsAddForm addForm, Long goodsId) {
        // 校验类目id
        Long categoryId = addForm.getCategoryId();
        Optional<CategoryEntity> optional = categoryQueryService.queryCategory(categoryId);
        if (!optional.isPresent() || !CategoryTypeEnum.GOODS.equalsValue(optional.get().getCategoryType())) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST, "商品类目不存在~");
        }
        return ResponseDTO.ok();
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(Long goodsId) {
        GoodsEntity goodsEntity = goodsDao.selectById(goodsId);
        if (goodsEntity == null) {
            return ResponseDTO.userErrorParam("商品不存在");
        }
        if (!goodsEntity.getGoodsStatus().equals(GoodsStatusEnum.SELL_OUT.getValue())) {
            return ResponseDTO.userErrorParam("只有售罄的商品才可以删除");
        }
        batchDelete(Arrays.asList(goodsId));
        dataTracerService.batchDelete(Arrays.asList(goodsId), DataTracerTypeEnum.GOODS);
        return ResponseDTO.ok();
    }
```


**因为菜单是系统能运行的核心功能，所以菜单业务返回的是“系统错误”，举例如下 MenuService.java**
```java

// 因为菜单
    public ResponseDTO<MenuVO> getMenuDetail(Long menuId) {
        //校验菜单是否存在
        MenuEntity selectMenu = menuDao.selectById(menuId);
        if (selectMenu == null) {
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "菜单不存在");
        }
        if (selectMenu.getDeletedFlag()) {
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "菜单已被删除");
        }
        MenuVO menuVO = SmartBeanUtil.copy(selectMenu, MenuVO.class);
        //处理接口权限
        String perms = menuVO.getApiPerms();
        if (!StringUtils.isBlank(perms)) {
            List<String> permsList = Lists.newArrayList(StringUtils.split(perms, ","));
            menuVO.setApiPermsList(permsList);
        }
        return ResponseDTO.ok(menuVO);
    }
```


## 四、实现原理

### 4.1、 返回错误分类

代码：`net.lab1024.sa.common.common.domain.ResponseDTO`
```java

code: 1,               0表示成功，不是0表示错误
level: 'user'，        等级：对应上面的三类，system，unexpected，user
msg:"成功",
data:{}
```


上面中 多了一个`level` 字段，就是表明 这个错误的分类 ，很重要。

对于通用的几个错误码如下：
 ErrorCode.java
```java

public interface ErrorCode {

    String LEVEL_SYSTEM = "system";//系统等级
    String LEVEL_USER = "user";//用户等级
    String LEVEL_UNEXPECTED = "unexpected";//未预期到的等级

    //错误码
    int getCode();

    //错误消息
    String getMsg();

    //错误等级
    String getLevel();
}
```


**SystemErrorCode**
```java

@Getter
@AllArgsConstructor
public enum SystemErrorCode implements ErrorCode {
    SYSTEM_ERROR(10001, "系统似乎出现了点小问题");

    private final int code;
    private final String msg;
    private final String level;

    SystemErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.level = LEVEL_SYSTEM;
    }
}
```


**SystemErrorCode**
```java

@Getter
@AllArgsConstructor
public enum UnexpectedErrorCode implements ErrorCode {
    BUSINESS_HANDING(20001, "呃~ 业务繁忙，请稍后重试"),
    PAY_ORDER_ID_ERROR(20002, "付款单id发生了异常，请联系技术人员排查");

    private final int code;
    private final String msg;
    private final String level;

    UnexpectedErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.level = LEVEL_UNEXPECTED;
    }
}
```


**UserErrorCode**
```java

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    PARAM_ERROR(30001, "参数错误"),
    DATA_NOT_EXIST(30002, "左翻右翻，数据竟然找不到了~"),
    ALREADY_EXIST(30003, "数据已存在了呀~"),
    REPEAT_SUBMIT(30004, "亲~您操作的太快了，请稍等下再操作~"),
    NO_PERMISSION(30005, "对不起，您无法访问此资源哦~"),
    LOGIN_STATE_INVALID(30007, "您还未登录或登录失效，请重新登录！"),
    FORM_REPEAT_SUBMIT(30009, "请勿重复提交");

    private final int code;
    private final String msg;
    private final String level;

    UserErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.level = LEVEL_USER;
    }
}
```


### 4.2、维护错误码

希望有个地方来维护这些返回码，通常最简单的想法是放到一个常量类或者枚举enum类里面，想法挺好，但是这样又有些问题：

1. 怎么避免返回码的重复？
2. 业务多会导致这个类特别大，难维护
3. 如何定义范围？

带着以上问题可以总结如下：

* 为避免类特别大，必须放到多个类里面
* 必须要有范围的定义和说明
* 必须要有全局的码值和避免范围重复的检测机制

所以 `ErrorCodeRegister` 和 `ErrorCodeRangeContainer` 类横空出世。
```java

import static net.lab1024.sa.common.common.code.ErrorCodeRangeContainer.register;
public class ErrorCodeRegister {
    static {
        // 系统 错误码
        register(SystemErrorCode.class, 10001, 20000);
        // 意外 错误码
        register(UnexpectedErrorCode.class, 20001, 30000);
        // 用户 通用错误码
        register(UserErrorCode.class, 30001, 40000);
    }
    public static int initialize() {
        return ErrorCodeRangeContainer.initialize();
    }
    public static void main(String[] args) {
        ErrorCodeRegister.initialize();
    }
}
```


**ErrorCodeRangeContainer 错误码 注册容器**
```java

class ErrorCodeRangeContainer {

    static final int MIN_START_CODE = 10000;//所有的错误码均大于10000
    static int errorCounter = 0;//用于统计数量
    static final Map<Class<? extends ErrorCode>, ImmutablePair<Integer, Integer>> CODE_RANGE_MAP = new ConcurrentHashMap<>();

   // 注册状态码 校验是否重复 是否越界
    static void register(Class<? extends ErrorCode> clazz, int start, int end) {
        String simpleName = clazz.getSimpleName();
        if (!clazz.isEnum()) {
            throw new ExceptionInInitializerError(String.format("<<ErrorCodeRangeValidator>> error: %s not Enum class !", simpleName));
        }
        if (start > end) {
            throw new ExceptionInInitializerError(String.format("<<ErrorCodeRangeValidator>> error: %s start must be less than the end !", simpleName));
        }
        ...
        ...
    }
}
```


### 4.3、解读

1）对于每个业务模块的`XxxErrorCode`，继承自三个基类（UserErrorCode/UnexpectedErrorCode/SystemErrorCode）中的一个，并将此类的code的起始值和末尾值注册进来。

2）在`ErrorCodeRangeContainer`类中有map用于接受注册的code，用于检测。

3）系统启动时检测：
 因为都是static常量，且类结构相同，所以可以在项目启动的时候利用static静态加载和反射技术进行全项目的code值检测。

即 调用`ErrorCodeRegister.initialize()`方法

举例 ：`AdminStartupRunner.java`
```java

@Slf4j
@Component
public class AdminStartupRunner implements CommandLineRunner {

    @Autowired
    private ScheduleConfig scheduleConfig;

    @Override
    public void run(String... args) {
        // 初始化状态码
        int codeCount = ErrorCodeRegister.initialize();
        //TODO <卓大> ：根据实际情况来决定是否开启定时任务
        String destroySchedules = "Spring 定时任务 @Schedule 已启动";
//      destroySchedules = scheduleConfig.destroy();
        log.info("\n ---------------【1024创新实验室 温馨提示：】 ErrorCode 共计完成初始化： {}个！---------------" +
                 "\n ---------------【1024创新实验室 温馨提示：】 {}---------------\n", codeCount, destroySchedules);
    }
}
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 心跳机制

# 心跳机制

## 一、需求与背景

经常会有这样的场景：

* 开发或者测试的时候，要测试一个定时任务job，刚好要测试的时候发现，不知道谁把job给执行了？
* 有些业务同一时间只能有一个进程在运行，但是结果并不是预期的，所以需要知道是否有多个进程同时在跑？
* 某些时候可能服务器停掉了，什么时候停的都不知道
* 想知道进程运行多久了，进行一些统计分析
* 等等其他

## 二、架构与思想

以上问题有很多种解决方案，比如服务发现、心跳、轮询等等，这里选择的是心跳机制。因为心跳机制非常简单，而且也只需要依赖一个数据库表，非常的轻便，适合各个项目。

### 2.1、守护daemon线程

在服务器启动的时候开启一个守护daemon线程，在守护daemon线程每隔一段时间将关于进程的基本信息存储到数据库表中。

### 2.2、记录关键信息

heart-beat记录了如下内容：
```
    private String projectPath;  // 项目启动路径
    private String serverIp; //服务器ip
    private Integer processNo;//进程号
    private LocalDateTime processStartTime;//进程开启时间
    private LocalDateTime heartBeatTime;//心跳当前时间
```


## 三、具体使用

在`sa-base`项目中的 `sa-base.yaml` 配置文件中，有关于心跳时间间隔的配置
```yaml

# 心跳配置
heart-beat:
  interval-seconds: 60
```


默认是 60秒，时间想改短或者改长都可以。

心跳由于是守护线程去处理，且只有一个线程，数据库操作也非常简单，所以整体性能影响非常非常非常小，所以这个心跳时长可以自由定义。当然，不改也可以，使用默认的 `60`秒。

## 四、实现原理

### 4.1、开启守护daemon线程

**HeartBeatManager.java**
```java

/**
 * 心跳核心调度管理器
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2023-01-09 20:57:24
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright 1024创新实验室 （ https://1024lab.net ）
 */
public class HeartBeatManager {
    private static final String THREAD_NAME_PREFIX = "sa-heart-beat";
    private static final int THREAD_COUNT = 1;
    private static final long INITIAL_DELAY = 60 * 1000L;

    private ScheduledThreadPoolExecutor threadPoolExecutor;//守护线程池
    private IHeartBeatRecordHandler heartBeatRecordHandler;//服务状态持久化处理类
    private long intervalMilliseconds;//调度配置信息

    public HeartBeatManager(Long intervalMilliseconds,
                            IHeartBeatRecordHandler heartBeatRecordHandler) {
        this.intervalMilliseconds = intervalMilliseconds;
        this.heartBeatRecordHandler = heartBeatRecordHandler;
        //使用守护线程去处理
        this.threadPoolExecutor = new ScheduledThreadPoolExecutor(THREAD_COUNT, r -> {
            Thread t = new Thread(r, THREAD_NAME_PREFIX);
            if (!t.isDaemon()) {
                t.setDaemon(true);
            }
            return t;
        });
        // 开始心跳
        this.beginHeartBeat();
    }

    //开启心跳
    private void beginHeartBeat() {
        HeartBeatRunnable heartBeatRunnable = new HeartBeatRunnable(heartBeatRecordHandler);
        threadPoolExecutor.scheduleWithFixedDelay(heartBeatRunnable, INITIAL_DELAY, intervalMilliseconds, TimeUnit.MILLISECONDS);
    }
}
```


### 4.2、心跳数据

**HeartBeatRunnable.java**
```java

public class HeartBeatRunnable implements Runnable {

    private String projectPath;//项目路径
    private List<String> serverIps;//服务器ip（多网卡）
    private Integer processNo;//进程号
    private LocalDateTime processStartTime;//进程开启时间
    private IHeartBeatRecordHandler recordHandler;

    public HeartBeatRunnable(IHeartBeatRecordHandler recordHandler) {
        this.recordHandler = recordHandler;
        this.initServerInfo();
    }

    /**
     * 初始化心跳相关信息
     */
    private void initServerInfo(){
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
       this.projectPath = System.getProperty("user.dir");
       this.serverIps = new ArrayList<>(NetUtil.localIpv4s());
       this.processNo = Integer.valueOf(runtimeMXBean.getName().split("@")[0]).intValue();
       this.processStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(runtimeMXBean.getStartTime()), ZoneId.systemDefault());
    }

    @Override
    public void run() {
        HeartBeatRecord heartBeatRecord = new HeartBeatRecord();
        heartBeatRecord.setProjectPath(this.projectPath);
        heartBeatRecord.setServerIp(StringUtils.join(this.serverIps, ";"));
        heartBeatRecord.setProcessNo(this.processNo);
        heartBeatRecord.setProcessStartTime(this.processStartTime);
        heartBeatRecord.setHeartBeatTime(LocalDateTime.now());
        recordHandler.handler(heartBeatRecord);
    }
}
```


**更多代码可见`sa-base`项目`support.heartbeat`** 包。

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 分包package结构

# 分包package结构

合理和统一的分包结构非常非常非常重要，之所以这么强调，是因为这是整个团队协作的基础，如果每个人分包很乱，很奇怪，将来维护起来必定身心疲惫，相互也无法配合，甚至想去帮助人的时候，一看到对方的分包结构，瞬间没了基情（激情）。

所以smart-admin的分包结构是经过团队长期实践过，验证过的。

**特别强调： 分包没有对错之分，只要适合你的团队，让大家都觉得舒服就好，不喜勿喷，喷子请绕行，谢谢**

具体分包详情请看 Java代码规范之项目规范： [分包结构](./../../start/guide/JavaProject.html)

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 重复提交

# 重复提交

## 一、背景与问题

很多时候需要限制用户的并发请求次数，比如对于某个接口，不能请求太频繁，限制 一个用户每秒钟只能请求一次；

举例场景：

* 短信验证码，对于同一个用户，60秒才能请求一次；
* 登录，对于一个用户，10秒才能请求一次

## 二、架构与思想

分析如上需求，能很清楚的知道这是一个 `请求信息记录` 的问题，核心在于：

* 以什么为 凭证 作为记录
* 如果已经请求过，记录在哪里
* 请求的时候需要记录请求时间

几个专有名词：
```java

凭证：ticket
重复提交：repeat submit
```


## 三、具体使用

系统提供了注解`@RepeatSubmit` 用于解决此问题。

### 3.1、配置

首先需要决策是使用`redis`还是`caffine`来存储凭证 ticket？ 这里建议：

* 如果是 集群部署，建议用 redis
* 如果是 单体部署，建议用 内存`caffine`

如下： 使用`caffine`作为存储，
 具体凭证使用的是 `[url + userid]`
```java

@Configuration
public class RepeatSubmitConfig {

    @Bean
    public RepeatSubmitAspect repeatSubmitAspect() {
        RepeatSubmitCaffeineTicket caffeineTicket = new RepeatSubmitCaffeineTicket(this::ticket);
        return new RepeatSubmitAspect(caffeineTicket);
    }

    /**
     * 获取指明某个用户的凭证
     */
    private String ticket(String servletPath) {
        Long userId = SmartRequestUtil.getRequestUserId();
        if (null == userId) {
            return StringConst.EMPTY;
        }
        return servletPath + "_" + userId;
    }
}
```


### 3.2、@RepeatSubmit注解
```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepeatSubmit {
    int value() default 300;//重复提交间隔时间/毫秒
    int MAX_INTERVAL = 30000;//最长间隔30s
}
```


默认重复提交为 300毫秒，具体按照实际业务需要

接下来可以将此注解加到 方法上，如下
```java

@RestController
@Api(tags = {SwaggerTagConst.Support.TABLE_COLUMN})
public class TableColumnController extends SupportBaseController {

    @Autowired
    private TableColumnService tableColumnService;

    @ApiOperation("修改表格列 @author 卓大")
    @PostMapping("/tableColumn/update")
    @RepeatSubmit
    public ResponseDTO<String> updateTableColumn(@RequestBody @Valid TableColumnUpdateForm updateForm) {
        return tableColumnService.updateTableColumns(SmartRequestUtil.getRequestUser(), updateForm);
    }

    @ApiOperation("恢复默认（删除） @author 卓大")
    @GetMapping("/tableColumn/delete/{tableId}")
    @RepeatSubmit
    public ResponseDTO<String> deleteTableColumn(@PathVariable Integer tableId) {
        return tableColumnService.deleteTableColumn(SmartRequestUtil.getRequestUser(), tableId);
    }

    @ApiOperation("查询表格列 @author 卓大")
    @GetMapping("/tableColumn/getColumns/{tableId}")
    public ResponseDTO<String> getColumns(@PathVariable Integer tableId) {
        return ResponseDTO.ok(tableColumnService.getTableColumns(SmartRequestUtil.getRequestUser(), tableId));
    }
}
```


## 四、实现原理

### 4.1、切面

因为是使用注解来解决问题，所以离不开 AOP，这里使用`RepeatSubmitAspect.java`来实现的；核心代码如下：
```java

    @Around("@annotation(net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ticketToken = attributes.getRequest().getServletPath();
        String ticket = this.repeatSubmitTicket.getTicket(ticketToken);
        if (StringUtils.isEmpty(ticket)) {
            return point.proceed();
        }
        Long timeStamp = this.repeatSubmitTicket.getTicketTimestamp(ticket);
        if (timeStamp != null) {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);

            // 说明注解去掉了
            if (annotation != null) {
                return point.proceed();
            }

            int interval = Math.min(annotation.value(), RepeatSubmit.MAX_INTERVAL);
            if (System.currentTimeMillis() < timeStamp + interval) {
                // 提交频繁
                return ResponseDTO.error(UserErrorCode.REPEAT_SUBMIT);
            }

        }
        Object obj = null;
        try {
            // 先给 ticket 设置在执行中
            this.repeatSubmitTicket.putTicket(ticket);
            obj = point.proceed();
        } catch (Throwable throwable) {
            log.error("", throwable);
            throw throwable;
        } finally {
            this.repeatSubmitTicket.removeTicket(ticket);
        }
        return obj;
    }
```


### 4.2、存储

**存储到redis: RepeatSubmitRedisTicket.java**
```java

    @Override
    public Long getTicketTimestamp(String ticket) {
        Long timeStamp = System.currentTimeMillis();
        boolean setFlag = redisValueOperations.setIfAbsent(ticket, String.valueOf(timeStamp), RepeatSubmit.MAX_INTERVAL, TimeUnit.MILLISECONDS);
        if (!setFlag) {
            timeStamp = Long.valueOf(redisValueOperations.get(ticket));
        }
        return timeStamp;
    }
```


**存储到 caffine: RepeatSubmitCaffeineTicket.java**
```java

 /**
     * 限制缓存最大数量 超过后先放入的会自动移除
     * 默认缓存时间
     * 初始大小为：100万
     */
    private static Cache<String, Long> cache = Caffeine.newBuilder()
            .maximumSize(100 * 10000)
            .expireAfterWrite(RepeatSubmit.MAX_INTERVAL, TimeUnit.MILLISECONDS).build();

    public RepeatSubmitCaffeineTicket(Function<String, String> ticketFunction) {
        super(ticketFunction);
    }

    @Override
    public Long getTicketTimestamp(String ticket) {
        return cache.getIfPresent(ticket);
    }
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 编号生成器

# 编号生成器

## 一、背景与问题

经常有这样的业务，生成“编号”，这个编号有一定的规则，比如合同编号: 以**1024LAB-HT**开头，然后跟年月日，最后从1开始增长，如下:
```js

1024LAB-20231024-0001
1024LAB-20231024-0002
1024LAB-20231024-0003
```


而且有些是以月为单位，以年为单位等等

## 二、架构与思想

### 2.1、自定义规则

根据需求，肯定要实现一个可以自定义规则的方式，方便后期修改，同时又遵循时间格式，如下：
**使用 [yyyy][mm][dd][nnnnn] 格式开配置生成器，其中：**
```java

yyyy 表示年
mm   表示月
dd   表示天
nnn  表示数字，几个n就表示几位
```


如上需求： HT[yyyy][mm][dd][nnnn] ，对于[nnnn]部分，系统提供四种增长周期：
```java

NONE     一直增长
YEAR     以年为单位，跨年会重新从0开始
MONTH    以月为单位，跨月会重新从0开始
DAY      以日为单位，跨日会重新从0开始
```


### 2.2、锁机制

由于生成的这些编号全局必须高度唯一，那么必须就要用到锁，那么锁大概有三种：

* 1. 基于内存锁实现 （不支持分布式和集群）
* 2. 基于redis锁实现
* 3. 基于Mysql 锁for update 实现

### 2.3、表结构
```sql

t_serial_number  编号定义表
t_serial_number_record   编号生成记录表
```


## 三、具体使用

### 3.1、定义规则

* 在`t_serial_number` 表中定义一条数据，其中`format`和`ruleType` 按照 指定的格式去填写；
* `initNumber`为初始值，默认从1开始
* `stepRandomRange`为每次[nnnn]的随机增长值，比如填写5，那么每次增加值为 [1 - 5]的一个随机数

### 3.2、添加枚举类

在`SerialNumberIdEnum.java`类中 添加刚才第一步的 `serialNumberId`;

### 3.3、选择锁方式

默认选择的是 第一种 内存锁的方式，可以见到`SerialNumberInternService.java` 类文件中有 `@Service`注解。
 如果选择redis或者mysql模式，请将 内存锁实现类的`@Service`注解去掉，然后在`SerialNumberMysqlService.java`或者`SerialNumberRedisService.java` 中添加 `@Service`注解；

至于三种锁如何选择？ 看你的项目了；

### 3.4、调用

注入 `SerialNumberService`类，然后调用`generate`方法; 如下：
```java

  @Autowired
  private SerialNumberService serialNumberService;
  ....
  ....
  // 生成5个 订单的 单号
  serialNumberService.generate(SerialNumberIdEnum.ORDER, 5);
```


## 四、技术实现

### 4.1、编号定义

表结构如下

|  |
| --- |
|  |

[nnnn] 增长策略如下： `SerialNumberRuleTypeEnum.java`
```java

@AllArgsConstructor
@Getter
public enum SerialNumberRuleTypeEnum implements BaseEnum {
    NONE(StringConst.EMPTY, "", "没有周期"),
    YEAR("[yyyy]", "\\[yyyy\\]", "年"),
    MONTH("[mm]", "\\[mm\\]", "年月"),
    DAY("[dd]", "\\[dd\\]", "年月日");

    private final String value;
    private final String regex;
    private final String desc;
}
```


替换规则代码如下，具体代码`SerialNumberBaseService.java`:
```java

    /**
     * 替换特殊rule，即替换[yyyy][mm][dd][nnn]等规则
     */
    protected List<String> formatNumberList(SerialNumberGenerateResultBO generteResult, SerialNumberInfoBO serialNumberInfo) {
        //第一步：替换年、月、日
        LocalDate lastTime = generteResult.getLastTime().toLocalDate();
        String year = String.valueOf(lastTime.getYear());
        String month = lastTime.getMonthValue() > 9 ? String.valueOf(lastTime.getMonthValue()) : "0" + lastTime.getMonthValue();
        String day = lastTime.getDayOfMonth() > 9 ? String.valueOf(lastTime.getDayOfMonth()) : "0" + lastTime.getDayOfMonth();

        // 把年月日替换
        String format = serialNumberInfo.getFormat();

        if (serialNumberInfo.getHaveYearFlag()) {
            format = format.replaceAll(SerialNumberRuleTypeEnum.YEAR.getRegex(), year);
        }
        if (serialNumberInfo.getHaveMonthFlag()) {
            format = format.replaceAll(SerialNumberRuleTypeEnum.MONTH.getRegex(), month);
        }
        if (serialNumberInfo.getHaveDayFlag()) {
            format = format.replaceAll(SerialNumberRuleTypeEnum.DAY.getRegex(), day);
        }
        //第二步：替换数字
        List<String> numberList = Lists.newArrayListWithCapacity(generteResult.getNumberList().size());
        for (Long number : generteResult.getNumberList()) {
            StringBuilder numberStringBuilder = new StringBuilder();
            int currentNumberCount = String.valueOf(number).length();
            //数量不够，前面补0
            if (serialNumberInfo.getNumberCount() > currentNumberCount) {
                int remain = serialNumberInfo.getNumberCount() - currentNumberCount;
                for (int i = 0; i < remain; i++) {
                    numberStringBuilder.append(0);
                }
            }
            numberStringBuilder.append(number);
            //最终替换
            String finalNumber = format.replaceAll(serialNumberInfo.getNumberFormat(), numberStringBuilder.toString());
            numberList.add(finalNumber);
        }
        return numberList;
    }
```


### 4.2、三种锁机制

第一种使用内存及锁机制，guava的 `Interners.newStrongInterner()`,具体代码在`SerialNumberInternService.java`,核心代码如下：
```java

@Override
    public List<String> generateSerialNumberList(SerialNumberInfoBO serialNumberInfo, int count) {
        SerialNumberGenerateResultBO serialNumberGenerateResult = null;
        synchronized (POOL.intern(serialNumberInfo.getSerialNumberId())) {
            // 获取上次的生成结果
            SerialNumberLastGenerateBO lastGenerateBO = serialNumberLastGenerateMap.get(serialNumberInfo.getSerialNumberId());
            // 生成
            serialNumberGenerateResult = super.loopNumberList(lastGenerateBO, serialNumberInfo, count);
            // 将生成信息保存的内存和数据库
            lastGenerateBO.setLastNumber(serialNumberGenerateResult.getLastNumber());
            lastGenerateBO.setLastTime(serialNumberGenerateResult.getLastTime());
            serialNumberDao.updateLastNumberAndTime(serialNumberInfo.getSerialNumberId(),
                    serialNumberGenerateResult.getLastNumber(),
                    serialNumberGenerateResult.getLastTime());
            // 把生成过程保存到数据库里
            super.saveRecord(serialNumberGenerateResult);
        }
        return formatNumberList(serialNumberGenerateResult, serialNumberInfo);
    }
```


第二种，使用redis锁机制，代码在`SerialNumberRedisService.java`，核心代码：
```java

@Override
    public List<String> generateSerialNumberList(SerialNumberInfoBO serialNumberInfo, int count) {
        SerialNumberGenerateResultBO serialNumberGenerateResult = null;
        String lockKey = RedisKeyConst.Support.SERIAL_NUMBER + serialNumberInfo.getSerialNumberId();
        try {
            boolean lock = false;
            for (int i = 0; i < MAX_GET_LOCK_COUNT; i++) {
                try {
                    lock = redisService.getLock(lockKey, 60 * 1000L);
                    if (lock) {
                        break;
                    }
                    Thread.sleep(SLEEP_MILLISECONDS);
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (!lock) {
                throw new BusinessException("SerialNumber 尝试5次，未能生成单号");
            }
            // 获取上次的生成结果
            SerialNumberLastGenerateBO lastGenerateBO = (SerialNumberLastGenerateBO) redisService.mget(
                    RedisKeyConst.Support.SERIAL_NUMBER_LAST_INFO,
                    String.valueOf(serialNumberInfo.getSerialNumberId()));
            // 生成
            serialNumberGenerateResult = super.loopNumberList(lastGenerateBO, serialNumberInfo, count);
            // 将生成信息保存的内存和数据库
            lastGenerateBO.setLastNumber(serialNumberGenerateResult.getLastNumber());
            lastGenerateBO.setLastTime(serialNumberGenerateResult.getLastTime());
            serialNumberDao.updateLastNumberAndTime(serialNumberInfo.getSerialNumberId(),
                    serialNumberGenerateResult.getLastNumber(),
                    serialNumberGenerateResult.getLastTime());

            // 把生成过程保存到数据库里
            super.saveRecord(serialNumberGenerateResult);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            redisService.unLock(lockKey);
        }
        return formatNumberList(serialNumberGenerateResult, serialNumberInfo);
    }
```


第三种，使用mysql innnodb的 `for update`机制，代码在`SerialNumberMysqlService.java`，核心代码如下：
```java

@Override
    @Transactional(rollbackFor = Throwable.class)
    public List<String> generateSerialNumberList(SerialNumberInfoBO serialNumberInfo, int count) {
        // // 获取上次的生成结果
        SerialNumberEntity serialNumberEntity = serialNumberDao.selectForUpdate(serialNumberInfo.getSerialNumberId());
        if (serialNumberEntity == null) {
            throw new BusinessException("cannot found SerialNumberId 数据库不存在:" + serialNumberInfo.getSerialNumberId());
        }
        SerialNumberLastGenerateBO lastGenerateBO = SerialNumberLastGenerateBO
                .builder()
                .lastNumber(serialNumberEntity.getLastNumber())
                .lastTime(serialNumberEntity.getLastTime())
                .serialNumberId(serialNumberEntity.getSerialNumberId())
                .build();
        // 生成
        SerialNumberGenerateResultBO serialNumberGenerateResult = super.loopNumberList(lastGenerateBO, serialNumberInfo, count);
        // 将生成信息保存的内存和数据库
        lastGenerateBO.setLastNumber(serialNumberGenerateResult.getLastNumber());
        lastGenerateBO.setLastTime(serialNumberGenerateResult.getLastTime());
        serialNumberDao.updateLastNumberAndTime(serialNumberInfo.getSerialNumberId(),
                serialNumberGenerateResult.getLastNumber(),
                serialNumberGenerateResult.getLastTime());
        // 把生成过程保存到数据库里
        super.saveRecord(serialNumberGenerateResult);
        return formatNumberList(serialNumberGenerateResult, serialNumberInfo);
    }
```


**`SerialNumberMapper.xml`**
```sql

    <select id="selectForUpdate" resultType="net.lab1024.sa.common.module.support.serialnumber.domain.SerialNumberEntity">
       select * from t_serial_number where serial_number_id = #{serialNumberId} for update
    </select>
```


### 4.3、保存生成记录

记录的数据如下:
```java

@TableName("t_serial_number_record")
public class SerialNumberRecordEntity {
    /**
     * 单号id
     */
    private Integer serialNumberId;
    /**
     * 记录日期
     */
    private LocalDate recordDate;
    /**
     * 最后更新值
     */
    private Long lastNumber;
    /**
     * 上次生成时间
     */
    private LocalDateTime lastTime;

    /**
     * 每日生成的数量
     */
    private Long count;
}
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 动态加载smart-reload

# 动态加载smart-reload

## 一、背景与问题

可能一看到这个名字会很困惑，不知道什么意思。但是接下来想象一个场景：

> 比如一个电商： 如果你把一些商品信息放到java进程里的缓存里，那么当你需要更新某两个商品信息的时候怎么办？
>
> 有人说为啥不使用redis缓存？ 额...并不是所有缓存都有必要使用redis 有人说不使用缓存？额...那还不如不用程序算了 有人说 重启？额...做好没年终奖的准备吧

所以系统需要有这个东西，在不重启java程序的前提下，能执行一个预留的代码。**这种场景叫做Reload**

## 二、架构与思想

### 2.1、轮询与订阅

其实有很多种解决方案，最经典的应该是 轮询和订阅这两种。
```java

轮询：每个一段时间重新查询数据库
订阅：系统订阅消息，然后使用第三者发送消息给系统，进行一些操作
```


### 2.2、选择轮询！

SmartAdmin中的reload选择`轮询+监听者模式`策略。 原因：

* 轮询比订阅简单，订阅需要依赖其他第三方：比如redis订阅，kafka等MQ订阅，Zookeeper等
* 轮询可以专注于本应用，不需要任何第三方

### 2.3、如何轮询

启动线程去扫描某个表，表中存放着一些 reload项（reload item），但凡有reload项标识发生变化，就发送事件给那些监听reload项的java监听者。

### 2.4、设计实现

#### 1）需要一个reload的数据，所以需要一个表定义reload项目，即表`t_reload_item`
```sql

CREATE TABLE `t_reload_item` (
  `tag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项名称',
  `args` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数 可选',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '运行标识',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='reload项目';

tag： reload项
args：listener执行之后的参数
identification：标识，identification与上次比较发生变化才进行reload
```


#### 2）需要在代码中找到reload的地方，准备使用一个注解解决`@SmartReload`
```java

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartReload {
    String value(); //reload的 tag，对应 t_reload_item表中的 tag
}
```


## 三、具体使用

### 3.1、定义tag

先定义一个tag名称，比如`system_config`，目的是为了 进行重新加载 `config`的缓存。

第一步：在`ReloadConst.java`类中 增加一个 常量，如下：
```java

public class ReloadConst {
    public static final String CONFIG_RELOAD = "system_config";
    public static final String CACHE_SERVICE = "cache_service";
}
```


第二步：在表`t_reload_item`，添加一条记录，其中：
```java

`tag`为`system_config`，不区分大小写；
`args`为调用方法的传入参数
`identification` 为具体的标识，每当`identification`发生变化的时候，会进行reload操作
```


### 3.2、添加注解

在需要调用的方法中，添加注解`@SmartReload`，且指定tag，如`ConfigService`中: 每次reload 会重新加载 配置的缓存
```java

@Slf4j
@Service
public class ConfigService {
    //系统配置缓存
    private final ConcurrentHashMap<String, ConfigEntity> configCache = new ConcurrentHashMap<>();
    @Autowired
    private ConfigDao configDao;

    /**
     * 此处为 reload方法，每当`identification`发生变化的时候，会执行此方法;
     * 此处 param 为 数据库t_reload_item 表中的args字段
     */
    @SmartReload(ReloadConst.CONFIG_RELOAD)
    public void configReload(String param) {
        this.loadConfigCache();
    }

    //初始化系统设置缓存
    @PostConstruct
    private void loadConfigCache() {
        List<ConfigEntity> entityList = configDao.selectList(null);
        entityList.forEach(entity -> this.configCache.put(entity.getConfigKey().toLowerCase(), entity));
    }
}
```


### 3.3、进行reload

需要reload的时候，打开表`t_reload_item`，找到记录 `tag = system_config`;
 修改：`identification`，只要和上次的`identification`不一样就可以；
 修改：`args`，即需要传入java方法的参数;
 等待一段时间（`sa-base`中配置文件配置`reload.interval-seconds`参数，秒）后，会自动执行`ConfigService.configReload(param)`方法；

## 四、实现原理

### 4.1、原理概述

就是有守护线程 去每隔一段时间扫码数据库`t_reload_item`，如果发现 `identification` 和之前的不一样，就找到对应`tag`的reload方法去执行。几个特殊点：

* 项目启动的时候，就去加载`t_reload_item` 作为初始参照物
* 项目启动的时候扫描所有的`@SmartReload`注解，并找到`tag -> reload方法` 的对应关系
* 使用守护daemon线程去轮训

### 4.2、表结构
```sql

tag： reload项
args：执行之后的参数
identification：标识，identification与上次比较发生变化才进行reload

CREATE TABLE `t_reload_item` (
  `tag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '项名称',
  `args` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数 可选',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '运行标识',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='reload项目';
```


### 4.3、守护线程

`SmartReloadManager.java`中
```java

    public SmartReloadManager(AbstractSmartReloadCommand reloadCommand, int intervalSeconds) {
        this.threadPoolExecutor = new ScheduledThreadPoolExecutor(THREAD_COUNT, r -> {
            Thread t = new Thread(r, THREAD_NAME_PREFIX);
            if (!t.isDaemon()) {
                t.setDaemon(true);
            }
            return t;
        });
        this.threadPoolExecutor.scheduleWithFixedDelay(new SmartReloadRunnable(reloadCommand), 10, intervalSeconds, TimeUnit.SECONDS);
        reloadCommand.setReloadManager(this);
    }
```


### 4.4、查找reload方法
```java

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods == null) {
            return bean;
        }
        for (Method method : methods) {
            SmartReload smartReload = method.getAnnotation(SmartReload.class);
            if (smartReload == null) {
                continue;
            }
            int paramCount = method.getParameterCount();
            if (paramCount > 1) {
                log.error("<<SmartReloadManager>> register tag reload : " + smartReload.value() + " , param count cannot greater than one !");
                continue;
            }
            String reloadTag = smartReload.value();
            this.register(reloadTag, new SmartReloadObject(bean, method));
        }
        return bean;
    }
```


### 4.5、比较 identification

在reload线程中去比较 `SmartReloadRunnable.java`:
```java

    private void doTask() {
        List<SmartReloadItem> smartReloadItemList = this.abstractSmartReloadCommand.readReloadItem();
        ConcurrentHashMap<String, String> tagIdentifierMap = this.abstractSmartReloadCommand.getTagIdentifierMap();
        for (SmartReloadItem smartReloadItem : smartReloadItemList) {
            String tag = smartReloadItem.getTag();
            String tagIdentifier = smartReloadItem.getIdentification();
            String preTagChangeIdentifier = tagIdentifierMap.get(tag);
            // 数据不一致
            if (preTagChangeIdentifier == null || !preTagChangeIdentifier.equals(tagIdentifier)) {
                this.abstractSmartReloadCommand.putIdentifierMap(tag, tagIdentifier);
                // 执行重新加载此项的动作
                SmartReloadResult smartReloadResult = this.doReload(smartReloadItem);
                this.abstractSmartReloadCommand.handleReloadResult(smartReloadResult);
            }
        }
    }
```


执行reload方法，并传入参数
```java

 private SmartReloadResult doReload(SmartReloadItem smartReloadItem) {
        SmartReloadResult result = new SmartReloadResult();
        SmartReloadObject smartReloadObject = this.abstractSmartReloadCommand.reloadObject(smartReloadItem.getTag());
        try {
            if (smartReloadObject == null) {
                result.setResult(false);
                result.setException("不能从系统中找到对应的tag：" + smartReloadItem.getTag());
                return result;
            }

            Method method = smartReloadObject.getMethod();
            if (method == null) {
                result.setResult(false);
                result.setException("reload方法不存在");
                return result;
            }

            result.setTag(smartReloadItem.getTag());
            result.setArgs(smartReloadItem.getArgs());
            result.setIdentification(smartReloadItem.getIdentification());
            result.setResult(true);
            int paramCount = method.getParameterCount();
            if (paramCount > 1) {
                result.setResult(false);
                result.setException("reload方法" + method.getName() + "参数太多");
                return result;
            }

            if (paramCount == 0) {
                method.invoke(smartReloadObject.getReloadObject());
            } else {
                method.invoke(smartReloadObject.getReloadObject(), smartReloadItem.getArgs());
            }
        } catch (Throwable throwable) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            result.setResult(false);
            result.setException(throwable.toString());
        }
        return result;
    }
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 独有工具类

# 独有工具类

## 问题与背景

工具类系统中使用了 hutool，但是有些情况他满足不了，所以又集合了自己的工具类;
 具体的工具类在 `sa-base`项目的 `net.lab1024.sa.base.common.util` 包；

## 获取当前用户

**SmartRequestUtil.java**，[查看代码](https://gitee.com/lab1024/smart-admin/tree/master/smart-admin-api/sa-base/src/main/java/net/lab1024/sa/common/common/util) 方法：
```java

SmartRequestUtil.getRequestUser();//获取当前请求用户
SmartRequestUtil.getRequestUserId();//获取当前请求用户的用户id
SmartRequestUtil.setRequestUser(...);//设置当前请求用户
SmartRequestUtil.remove();//移除请求用户
```


## Bean复制

javabean之间的复制很频繁，但是性能比较好的应该是 spring的`BeanUtils`，又简单封装了下;
**SmartBeanUtil.java**，[查看代码](https://gitee.com/lab1024/smart-admin/tree/master/smart-admin-api/sa-base/src/main/java/net/lab1024/sa/common/common/util)
```java

SmartBeanUtil.copy(Object source, Class<T> targetClass); //复制对象
SmartBeanUtil.copyProperties(Object source, Object target); //复制bean的属性
SmartBeanUtil.copyList(List<T> source, Class<T> targetClass); //复制list
```


## 字符串

hutool中有字符串工具类，但是还是少，可以看下的`SmartStringUtil`
```java

Set<String> splitConvertToSet(String str, String split);  //分割字符串转为 Int Set
List<String> splitConvertToList(String str, String split); //分割字符串转为 Int List

与此之外还有转为  Long、Byte、Double 等等其他类型的 Set、List、Array等
```


## 枚举

在java特性中，用了`BaseEnum`接口解决枚举的问题，[具体可以看](./../front/VueEnum.html#_2-1、baseenum接口)**SmartBaseEnum.java**，[查看代码](https://gitee.com/lab1024/smart-admin/tree/master/smart-admin-api/sa-base/src/main/java/net/lab1024/sa/common/common/util)
```java

// 校验参数与枚举类比较是否合法
boolean checkEnum(Object value, Class<? extends BaseEnum> enumClass) ;

// 创建一个具有唯一array值的数组，每个值不包含在其他给定的数组中。
List<Object> differenceValueList(Class<? extends BaseEnum> enumClass, T... exclude);

// 获取枚举类的说明 value : info 的形式
String getEnumDesc(Class<? extends BaseEnum> enumClass) ;

// 获取与参数相匹配的枚举类实例的
String getEnumDescByValue(Object value, Class<? extends BaseEnum> enumClass);

等等还有其他很好用的方法
```


## BigDecimal

**SmartBigDecimalUtil.java**，[查看代码](https://gitee.com/lab1024/smart-admin/tree/master/smart-admin-api/sa-base/src/main/java/net/lab1024/sa/common/common/util) 关于小数计算的： 加减乘除 方式方法；

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 万能密码

# 万能密码

## 一、需求与背景

经常会有这样的场景：
 线上出bug了，需要登录用户的账号 重现下，但是当登录 用户账号的时候，会问用户密码，涉及隐私，同时还会将其 踢掉线，等等一系列麻烦的操作。

那么有没有办法解决呢？ 这个时候呢，万能密码出现了，有如下几点：

* 使用万能密码可以登录任何账号
* 不将别人 踢掉线

## 二、架构与思想

由于逻辑比较简单，这里只是简单说一下大概思路：

* 需要在登录的时候，优先进行万能密码判断；
* 万能密码生成的 token 不会提掉线
* 万能密码生成的 token 有效期比较短，防止引起其他问题
* 万能密码方便修改和配置

## 三、具体使用

SmartAdmin 中 万能密码记录在了 `t_config` 表中，`key`为 `super_password`;

## 四、实现原理

### 4.1、登录代码

`net.lab1024.sa.admin.module.system.login.service.LoginService#login`方法
```java

   * 验证密码：
         * 1、万能密码
         * 2、真实密码
         */
        String superPassword = EmployeeService.getEncryptPwd(configService.getConfigValue(ConfigKeyEnum.SUPER_PASSWORD));
        String requestPassword = EmployeeService.getEncryptPwd(loginForm.getPassword());
        if (!(superPassword.equals(requestPassword) || employeeEntity.getLoginPwd().equals(requestPassword))) {
            saveLoginLog(employeeEntity, ip, userAgent, "密码错误", LoginLogResultEnum.LOGIN_FAIL);
            return ResponseDTO.userErrorParam("登录名或密码错误！");
        }

        // 生成 登录token，保存token
        Boolean superPasswordFlag = superPassword.equals(requestPassword);
```


### 4.2、万能密码的token

代码：`net.lab1024.sa.admin.module.system.login.service.LoginService#login`
```java

        // 生成 sa-token的 loginId，对于万能密码：受限制sa token 要求loginId唯一，万能密码只能插入一段uuid
        String saTokenLoginId = null;
        if (superPasswordFlag) {
            saTokenLoginId = SUPER_PASSWORD_LOGIN_ID_PREFIX + StringConst.COLON + UUID.randomUUID().toString().replace("-", "") + StringConst.COLON + employeeEntity.getEmployeeId();
        } else {
            saTokenLoginId = UserTypeEnum.ADMIN_EMPLOYEE.getValue() + StringConst.COLON + employeeEntity.getEmployeeId();
        }
```


## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 代码生成原理

# 代码生成原理

## 一、背景与问题

笔者认为代码生成在某些情景下非常有用，但是在某些情景下还是建议慎用，因为不是手敲出的代码毕竟缺少了一个思考的过程，会很容易产生bug，相比于速度和bug，我们相信速度在bug面前不值一提。
 那么为什么还是做了代码生成呢？ **因为 “ 真香 ” 定律。代码生成真的很香，但是依然建议在使用代码生成之前多去思考业务，三思之后再去使用它。**

## 二、架构与思想

### 2.1、表结构

代码生成都离不开数据库的表结构的信息，所以表结构信息是要完善的，比如表的注释、列的注释等等

代码生成可能不止一次，所以代码生成的配置信息最好也能存储下来；

### 2.2 生成内容

对于中后台，大部分的功能都是类似的，无外乎“增删查改”，所以将需求拆分一下：

* 对于java的“增删查改”需要有如下信息：Controller、Service、Manager、Dao、Mapper、JavaBean(domain)
* 对于前端而言：列表：list.vue , 表单：form.vue，请求 api.js

## 三、具体使用

### 3.1、使用要求

**表注释、表的每一列 必须有注释！！**

### 3.2、使用方法

以`超管`身份登录系统，打开`代码生成` 菜单页面

* 1）搜索 要生成的表信息
* 2）点击`代码配置`
* 3）

|  |  |
| --- | --- |
|  |  |
| 在线代码配置 | 代码生成配置持久化 |

### 3.3 下载

点击列表中的 `下载代码` 即可 下载一个 代码压缩包了

### 3.4 代码在线预览

|  |
| --- |
|  |

## 四、实现原理

### 4.1、表设计

表`t_code_generator_config`，将每一个步骤的信息都存下来。

|  |
| --- |
|  |

### 4.2、代码

具体代码在`sa-base`项目的`support.codegenerator`包
 具体细节如下：

* 模板文件：`src/main/resource/code-generator-template/`
* 模板技术： velocity
* 具体实现： 不难很简单 对于每一个模板文件，都有对应的 `service`处理类，具体如下

|  |
| --- |
|  |

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### Java17与SpringBoot3升级

# Java 17+ 与 Spring Boot 3.0 升级指南

## **前言**

> 岁月如梭，转眼间，自2014年3月Java8的正式发布，已过去10年有余
>
> 在这段漫长而又充实的时光里，是无数次的键盘跌宕起伏，成千上万行的代码如繁星般涌动闪现，日落月升，星辰更迭。
>
> Java的生态不断演进 -- Java9、Java11、Java17... 直到最新的Java22，它们如同照耀在我们平凡开发生涯中的一颗颗恒星，不断前行，留下技术进步的轨迹。
>
> 身为开发者，我们应有对新知的渴望与探索的勇气，积极拥抱新技术，面向未来，以编程为笔，让每一行代码都更有意义，实现自我的价值。


技术在不断的进步，保持软件的更新迭代是确保项目长期健康发展的关键。我们的开源 SmartAdmin 项目迎来了一波升级，全面支持Java 17及以上版本，并同步升级至 Spring Boot 3.0+。

这一升级不仅带来了技术栈的现代化，更是为了提升项目的性能、安全性、以及更好地适应未来技术的发展趋势。


## **升级理由**

1. **技术前沿**：Java 17 作为Java平台的最新长期支持（LTS）版本，引入了多项新特性与性能改进，为构建高效、安全的应用提供了有力支持。Spring Boot 3.0则紧跟Java生态的步伐，提供了与新版Java平台无缝集成的开发体验。
2. **性能提升**：Java 17 在垃圾回收、即时编译器（JIT）等方面进行了优化，使得应用运行更加流畅，响应速度更快。Spring Boot 3.0则通过优化依赖管理、提升自动配置效率等方式，进一步增强了应用的性能表现。
3. **安全性增强**：Java 17修复了以往版本中的多个安全漏洞，并引入了新的安全特性，如密封类（Sealed Classes），为应用提供了更高级别的安全保障。Spring Boot 3.0也跟随Spring Framework的步伐，加强了对安全性的关注和支持。
4. **生态支持**：随着Java和Spring社区的不断发展，越来越多的库和框架开始支持Java 17和Spring Boot 3.0。升级后，项目将能更好地融入这一生态系统，享受更丰富的资源和更广泛的社区支持。

## **升级好处**

1. **提升开发效率**：Spring Boot 3.0带来了更简洁的配置、更强大的自动配置能力，以及更丰富的Starter，这些都将极大地提升开发效率，让开发者能够更专注于业务逻辑的实现。
2. **优化运维体验**：新版本的Spring Boot提供了更完善的监控、诊断和调试工具，使得运维工作更加便捷高效。同时，Java 17的性能优化也将有助于降低运维成本，提升应用的稳定性。
3. **增强可扩展性和可维护性**：借助Java 17的新特性和Spring Boot 3.0的架构优化，项目将更加易于扩展和维护。这将有助于项目在长期发展中保持竞争力。
4. **未来兼容性**：选择Java 17和Spring Boot 3.0意味着项目将站在技术发展的前沿，为未来可能的技术变革做好充分准备。


 综上所述，升级至Java 17 与 SpringBoot3.0 是一个明智且必要的选择。

它不仅能让项目享受到最新的技术成果，还能为项目的长期发展奠定坚实的基础。我们期待这一升级能为所有使用者带来更加美好的开发体验和应用性能。

## **升级指南无论您是想直接使用新版本项目代码，还是想尝试升级原有项目，都可以参考以下指南（2选1）1、使用新代码** 为了与原有 Java8 + SpringBoot2.0 的项目代码区分，我们将升级后的 Java17 + SpringBoot3.0 项目代码放在了新文件夹《smart-admin-api-java17》中。 以后的项目迭代维护，我们也会同时更新 Java8 与 Java17 两个版本。


**2、更新老代码** 以下是本次更新一些主要改动，如果您是老版本的 SmartAdmin项目 或者 SpringBoot2.0项目，也想要升级到 Java17+SpringBoot3.0
 那么您可以参考升级，当然每个项目的技术栈，依赖的第三方框架，可能不一致，仅供参考，

更详细的 Springboot3.0 升级指南请参考官方文档：<https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide>

* 更新pom文件 java 与 springboot 版本
```java

<java.version>17</java.vversion>
<springboot.version>3.3.1</springboot.version>
```


* 更新Maven打包配置
```java

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.13.0</version>
  <configuration>
    <source>17</source>
    <target>17</target>
    <encoding>UTF-8</encoding>
	<compilerArgument>-parameters</compilerArgument>
  </configuration>
</plugin>
```


* 全局替换导入路径 ，Java17中 JavaEE 相关已迁移到 Jakarta中，影响到 Servlet API、JPA、Bean Validation 等引用需要修改。
```java

javax.persistence.*   -> jakarta.persistence.*
javax.validation.*    -> jakarta.validation.*
javax.servlet.*       -> jakarta.servlet.*
javax.annotation.*    -> jakarta.annotation.*
javax.transaction.*   -> jakarta.transaction.*
```


> 以下为一些主要依赖项的更新，仅供参考。

* 更新 mysql 依赖
```html

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```


* 更新 druid 依赖
```html

<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid-spring-boot-3-starter</artifactId>
  <version>1.2.23</version>
</dependency>
```


* 更新 mybatis-plus 依赖
```html

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
```


* 更新 sa-token 依赖
```html

<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot3-starter</artifactId>
    <version>1.38.0</version>
</dependency>
```


## 结语

尽管我们已进行测试和优化，但任何大型的技术升级都可能伴随着未知的问题和潜在的Bug。

我们诚挚地邀请开发同志们在实际使用过程中积极反馈遇到的问题和意见。

您可以通过Git、Gitee 、微信群反馈问题，欢迎提出改进建议、参与代码审查或者直接贡献代码。

感谢所有参与和支持本项目的朋友们，你们的反馈和建议对我们至关重要！

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


---

## 前端解读


### 多种登录页样式风格

# 多种登录页样式风格

## 前言

一个好看的登录页是任何系统的开始，提供：3种登录页样式，3个背景图片， 3乘3共计9种登录页风格供大家选择！

## 使用修改

三种样式：

* 1、进入前端`/src/views/system`目录，其中有 三个 `login` 目录，系统默认使用 `login3`目录作为默认
* 2、打开路由文件：`/src/router/system/login.js`，将`component: () => import('/@/views/system/login2/login.vue'),`，改为对应的目录即可，如使用和预览环境一样的:`component: () => import('/@/views/system/login/login.vue')`

三个背景图片：

* 1、进入前端`/src/assets/images/login`目录，其中有`login-bg.png、login-bg1.png、login-bg2.png` 三个背景图片选择
* 2、打开上面对应`login`目录中的`login.less`文件，找到对应背景图片进行更换

## 9种风格展示


|  |
| --- |
|  |
|  |
|  |
|  |
|  |
|  |
|  |
|  |
|  |

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 不同环境配置和打包

## 技术体系

后端基于SpringBoot2/3 + SaToken + Mybatis-plus;前端基于Vue3 + Vite5 + Ant Design Vue;移动端基于uniapp (vue3 版本) + uni-ui；支持电脑端、H5、小程序、原生app等多端。


### 数据变动记录

## 技术体系

后端基于SpringBoot2/3 + SaToken + Mybatis-plus;前端基于Vue3 + Vite5 + Ant Design Vue;移动端基于uniapp (vue3 版本) + uni-ui；支持电脑端、H5、小程序、原生app等多端。


### 用户自定义表格列

## 技术体系

后端基于SpringBoot2/3 + SaToken + Mybatis-plus;前端基于Vue3 + Vite5 + Ant Design Vue;移动端基于uniapp (vue3 版本) + uni-ui；支持电脑端、H5、小程序、原生app等多端。


### 常量和枚举

## 技术体系

后端基于SpringBoot2/3 + SaToken + Mybatis-plus;前端基于Vue3 + Vite5 + Ant Design Vue;移动端基于uniapp (vue3 版本) + uni-ui；支持电脑端、H5、小程序、原生app等多端。


### Api请求

## 技术体系

后端基于SpringBoot2/3 + SaToken + Mybatis-plus;前端基于Vue3 + Vite5 + Ant Design Vue;移动端基于uniapp (vue3 版本) + uni-ui；支持电脑端、H5、小程序、原生app等多端。


### 帮助文档

# 帮助文档

## 一、背景与问题

如果你用过阿里云，用过有赞，有过银行等等很多中大型的系统，它都在合适的地方，给介绍相关的帮助文档信息；
 对于中后台系统而言，系统业务逻辑强，复杂度高，所以帮助文档也是一个刚需功能；
 与此同时，当在某个具体操作页面的时候，最好右侧也有帮助文档的提示信息，这样最好不过了。

## 二、架构思想

拆分下具体的需求，可以理解为有以下几点：

* 有维护帮助文档的功能
* 有查看帮助文档的功能
* 帮助文档可以关联到具体的页面

以上分析可以得出如下技术结论：

* 帮助文档，富文本，增删查改
* 帮助文档目录，树形结构，增删查改
* 帮助文档查看与阅读，并记录阅读痕迹
* 帮助文档关联菜单页面

## 三、具体使用

直接打开菜单`系统文档->系统手册`；
 左侧添加 `帮助文档` 的目录树形结构；
 右侧添加具体的`帮助文档`的文章；

## 四、技术实现

### 4.1、表结构

一共需要四张表。关系分别如下：
```sql

t_help_doc                     帮助文档（富文本）
t_help_doc_catalog             帮助文档目录
t_help_doc_relation            帮助文档关联菜单
t_help_doc_view_record         帮助文档阅读记录表
```


具体表设计如下：

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| t\_help\_doc | t\_help\_doc\_catalog | t\_help\_doc\_relation | t\_help\_doc\_view\_record |

### 4.2、Java代码

因为`帮助文档` 可能会在多个项目中使用，所以:

* 用户查询帮助文档功能放在了`sa-base`项目的`support`包中；
* 增删查改帮助文档功能放在了`sa-admin`项目的`support`包中，因为只有后管才能管理；

1）`sa-base`项目的controller层代码如下
```java

    @ApiOperation("帮助文档目录-获取全部 @author 卓大")
    @GetMapping("/helpDoc/helpDocCatalog/getAll")
    public ResponseDTO<List<HelpDocCatalogVO>> getAll() {
        return ResponseDTO.ok(helpDocCatalogService.getAll());
    }

    // --------------------- 帮助文档 【用户】-------------------------

    @ApiOperation("【用户】帮助文档-查看详情 @author 卓大")
    @GetMapping("/helpDoc/user/view/{helpDocId}")
    @RepeatSubmit
    public ResponseDTO<HelpDocDetailVO> view(@PathVariable Long helpDocId, HttpServletRequest request) {
        return helpDocUserService.view(SmartRequestUtil.getRequestUser(),helpDocId);
    }

    @ApiOperation("【用户】帮助文档-查询全部 @author 卓大")
    @GetMapping("/helpDoc/user/queryAllHelpDocList")
    @RepeatSubmit
    public ResponseDTO<List<HelpDocVO>> queryAllHelpDocList() {
        return helpDocUserService.queryAllHelpDocList();
    }


    @ApiOperation("【用户】帮助文档-查询 查看记录 @author 卓大")
    @PostMapping("/helpDoc/user/queryViewRecord")
    @RepeatSubmit
    public ResponseDTO<PageResult<HelpDocViewRecordVO>> queryViewRecord(@RequestBody @Valid HelpDocViewRecordQueryForm helpDocViewRecordQueryForm) {
        return ResponseDTO.ok(helpDocUserService.queryViewRecord(helpDocViewRecordQueryForm));
    }
}
```


2）`sa-admin`项目的controller层代码有：

* 帮助文档目录-添加 @author 卓大
* 帮助文档目录-更新 @author 卓大
* 帮助文档目录-删除 @author 卓大
* 【管理】帮助文档-分页查询 @author 卓大
* 【管理】帮助文档-获取详情 @author 卓大
* 【管理】帮助文档-添加 @author 卓大
* 【管理】帮助文档-更新 @author 卓大
* 【管理】帮助文档-删除 @author 卓大
* 【管理】帮助文档-根据关联id查询 @author 卓大

具体代码:[代码](https://gitee.com/lab1024/smart-admin/blob/master/smart-admin-api/sa-admin/src/main/java/net/lab1024/sa/admin/module/system/support/AdminHelpDocController.java)

3）`sa-base`项目的service和manager代码
```java

support/helpdoc/
| -- service
| -- | -- HelpDocCatalogService     目录 增删查改
| -- | -- HelpDocService            文档 增删查改
| -- | -- HelpDocUserService        阅读查看
| -- manager
| -- | -- HelpDocManager            文档关联菜单，会涉及多个表的事务manager

帮助文档中使用到了Manager层，目的是为了解决事务问题，代码如下：
```
@Service
public class HelpDocManager {
    @Autowired
    private HelpDocDao helpDocDao;

    @Transactional(rollbackFor = Throwable.class)
    public void save(HelpDocEntity helpDocEntity, List<HelpDocRelationForm> relationList) {
        helpDocDao.insert(helpDocEntity);
        Long helpDocId = helpDocEntity.getHelpDocId();
        // 保存关联
        if (CollectionUtils.isNotEmpty(relationList)) {
            helpDocDao.insertRelation(helpDocId, relationList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(HelpDocEntity helpDocEntity, List<HelpDocRelationForm> relationList) {
        helpDocDao.updateById(helpDocEntity);
        Long helpDocId = helpDocEntity.getHelpDocId();
        // 保存关联
        if (CollectionUtils.isNotEmpty(relationList)) {
            helpDocDao.deleteRelation(helpDocId);
            helpDocDao.insertRelation(helpDocId, relationList);
        }
    }
}
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36

### 4.3、Layout引入帮助文档

在前端页面中，页面右侧为帮助文档，这个需要在layout文件中插入sider
`smart-side-layout.vue`
```vue

<template>
  <a-layout class="admin-layout" style="min-height: 100%">
    <!-- 右侧帮助文档 help-doc -->
    <a-layout-sider v-show="helpDocFlag" theme="light" :width="180" class="help-doc-sider" :trigger="null" style="min-height: 100%">
      <SideHelpDoc />
    </a-layout-sider>
  </a-layout>
</template>
```

1
2
3
4
5
6
7
8

效果如图

|  |
| --- |
|  |

### 3.3、帮助文档布局

在新的页面，展示整个帮助文档，左侧为：帮助文档目录，右侧为具体的文档内容（即）
 代码：[smart-help-doc-layout.vue](https://gitee.com/lab1024/smart-admin/blob/master/smart-admin-web/javascript-ant-design-vue3/src/layout/smart-help-doc-layout.vue)
 页面效果：

|  |
| --- |
|  |

### 3.4、前端代码总结
```js

/views/support/help-doc
| -- help-doc
| -- | -- management                      帮助文档管理目录
| -- | -- | -- components                     组件
| -- | -- | -- help-doc-manage-list.vue       帮助文档列表
| -- | -- | -- help-doc-mitt.js               eventbus
| -- | -- user-view                        用户查看目录
| -- | -- | -- components                     组件
| -- | -- | -- help-doc-user-view.vue         查看页面
```

1
2
3
4
5
6
7
8
9

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### Layout布局

# Layout布局

## 一、背景与问题

很多框架都有多种布局形态，即 菜单在左侧、顶部；菜单是展开形态、菜单是传统折叠菜单 等等，但是呢，很多框架layout这块封装的就特别复杂，事情为什么变得复杂呢，想有以下几个原因：

* 1、多种布局相同的组件很多，所以会抽象的组件较多
* 2、因为要提供配置页面，所以布局要和状态管理vuex挂钩
* 3、多种布局，大部分是layout的位置（即layout组件的属性变化）
* 4、炫技各种抽象 or 不炫技代码堆积到了一起

以上的以上，很多写的就很复杂，一个layout文件弄几千行，或者超级多的变量，不敢想象

## 二、架构思想

1） layout 种类大概就3，4中，索性就为每个layout 设计一个 layout.vue文件，作为入口就可以了 2） 对于大类的公共组件，抽出来放到 `layout/components` 中 3) 对于多个layout中相同代码，不再抽象，保持独立，给予最大的扩展性

以上三点虽然 可能会有相同的代码，但是并不会太多，好处是 大大的降低了复杂度，提高的阅读性和维护性。
 这也是可以满足的好的代码原则的。[好的代码](./../standard/basic.html##_1-1、什么是好的代码)

> 1）满足业务需要：代码是来实现业务的，如果业务都实现不了，代码也就没什么价值了
>  2）代码尽可能的清晰明了：就是让小白也能看懂你的代码
>  3）代码尽可能的少：在保证清晰明了的前提下，能少一行少一行，能少一个类少一个类，能少一行注释少一行注释
>  4）代码尽可能复用性和模块化：在保证清晰明了和尽可能少的前提下，能复用的代码尽量复用，能模块的尽量模块

## 三、具体实现

目录结构如下：
```js

| layout /                       布局目录
| --- index.vue                  引入文件（在这里vuex判断是哪种具体的layout）
| --- side-layout.vue            传统菜单布局layout
| --- side-expand-layout.vue     展开菜单布局layout
| --- top-layout.vue             顶部菜单布局layout
| --- components/                公共组件
```

1
2
3
4
5
6

在 `index.vue` 中代码， 使用if 直接判断是哪一种类型的 layout，很清晰
```vue

<template>
  <!--左侧菜单 模式-->
  <SideLayout v-if="layout === LAYOUT_ENUM.SIDE.value" />
  <!--左侧展开菜单 模式-->
  <SideExpandLayout v-if="layout === LAYOUT_ENUM.SIDE_EXPAND.value" />
  <!--顶部菜单 模式-->
  <TopLayout v-if="layout === LAYOUT_ENUM.TOP.value" />
</template>
<script setup>
  import { computed } from 'vue';
  import { LAYOUT_ENUM } from '/@/constants/layout-const';
  import SideExpandLayout from './side-expand-layout.vue';
  import SideLayout from './side-layout.vue';
  import TopLayout from './top-layout.vue';
  import { useAppConfigStore } from '/@/store/modules/system/app-config';

  const layout = computed(() => useAppConfigStore().$state.layout);
</script>
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18

传统菜单layout，`side-layout.vue`  中
```vue

<template>
  <a-layout class="admin-layout" style="min-height: 100%">
    <!-- 侧边菜单 side-menu -->
    <a-layout-sider class="side-menu" :width="sideMenuWidth" :collapsed="collapsed" :theme="theme">
      <!-- 左侧菜单 -->
      <SideMenu :collapsed="collapsed" />
    </a-layout-sider>

    <!--中间内容，一共三部分：1、顶部;2、中间内容区域;3、底部（一般是公司版权信息）;-->
    <a-layout id="smartAdminMain" :style="`height: ${windowHeight}px`" class="admin-layout-main">
      <!-- 顶部头部信息 -->
      <a-layout-header class="layout-header">
        <a-row class="layout-header-user" justify="space-between">
          <a-col class="layout-header-left">
            <!-- 菜单收缩 -->
            <span class="collapsed-button">
              <menu-unfold-outlined v-if="collapsed" class="trigger" @click="() => (collapsed = !collapsed)" />
              <menu-fold-outlined v-else class="trigger" @click="() => (collapsed = !collapsed)" />
            </span>
            <!-- 首页 按钮 -->
            <a-tooltip placement="bottom">
              <template #title>首页</template>
              <span class="home-button" @click="goHome">
                <home-outlined class="trigger" />
              </span>
            </a-tooltip>
            <!-- 面包屑 -->
            <span class="location-breadcrumb">
              <MenuLocationBreadcrumb />
            </span>
          </a-col>
          <!---用戶操作区域：搜索、消息、国际化、的-->
          <a-col class="layout-header-right">
            <HeaderUserSpace />
          </a-col>
        </a-row>
        <PageTag />
      </a-layout-header>

      <!--中间内容-->
      <a-layout-content id="smartAdminLayoutContent" class="admin-layout-content">
        <!--不keepAlive的iframe使用单个iframe组件-->
        <IframeIndex v-if="iframeNotKeepAlivePageFlag" :key="route.name" :name="route.name" :url="route.meta.frameUrl" />
        <!--keepAlive的iframe 每个页面一个iframe组件-->
        <IframeIndex
          v-for="item in keepAliveIframePages"
          v-show="route.name == item.name"
          :key="item.name"
          :name="item.name"
          :url="item.meta.frameUrl"
        />
        <!--非iframe使用router-view-->
        <div v-show="!iframeNotKeepAlivePageFlag && keepAliveIframePages.every((e) => route.name != e.name)">
          <router-view v-slot="{ Component }">
            <keep-alive :include="keepAliveIncludes">
              <component :is="Component" :key="route.name" />
            </keep-alive>
          </router-view>
        </div>
      </a-layout-content>

      <!-- footer 版权公司信息 -->
      <a-layout-footer class="layout-footer" v-show="footerFlag">
        <smart-footer />
      </a-layout-footer>
      <!--- 回到顶部 -->
      <a-back-top :target="backTopTarget" :visibilityHeight="80" />
    </a-layout>
    <!-- 右侧帮助文档 help-doc -->
    <a-layout-sider v-show="helpDocFlag" theme="light" :width="180" class="help-doc-sider" :trigger="null" style="min-height: 100%">
      <SideHelpDoc />
    </a-layout-sider>
  </a-layout>
</template>

<script setup>
  import 具体的导入;

  //菜单宽度
  const sideMenuWidth = computed(() => useAppConfigStore().$state.sideMenuWidth);
  //主题颜色
  const theme = computed(() => useAppConfigStore().$state.sideMenuTheme);
  //是否显示标签页
  const pageTagFlag = computed(() => useAppConfigStore().$state.pageTagFlag);
  // 是否显示帮助文档
  const helpDocFlag = computed(() => useAppConfigStore().$state.helpDocFlag);
  // 是否显示页脚
  const footerFlag = computed(() => useAppConfigStore().$state.footerFlag);
  //是否隐藏菜单
  const collapsed = ref(false);

  //页面初始化的时候加载水印
  onMounted(() => {
    watermark.set('smartAdminLayoutContent', useUserStore().actualName);
  });

  //回到顶部
  const backTopTarget = () => {
    return document.getElementById('smartAdminMain');
  };

  const router = useRouter();
  function goHome() {
    router.push({ name: HOME_PAGE_NAME });
  }

  const windowHeight = ref(window.innerHeight);
  window.addEventListener('resize', function () {
    windowHeight.value = window.innerHeight;
  });

  // ----------------------- keep-alive相关 -----------------------
  let { route, keepAliveIncludes, iframeNotKeepAlivePageFlag, keepAliveIframePages } = smartKeepAlive();
</script>
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
110
111
112
113
114

展开菜单和顶部菜单，具体可见，[具体请见](https://gitee.com/lab1024/smart-admin/blob/master/smart-admin-web/javascript-ant-design-vue3/src/layout/)

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 定制主题

## 一、背景与问题

知道任何的 UI框架只是提供了一种最基础的颜色、布局、样式等风格，但是对于某些特定的行业、特定的领域，都需要有行业属性的定制，那么，在SmartAdmin中是如何解决的呢？

## 二、定制

具体在 `src/theme` 目录中做了样式定制

### 2.1 smart-admin.less 文件

在这个文件中，定义了属于 `SmartAdmin`自己的样式，这些样式有个特定，均已`smart` 开头，这样可以很好的进行区分自己本项目的样式 还是 其他的样式。举例如下：

less
```
/ 查询表格样式 /
.smart-query-form {
  background-color: #ffffff;
  padding: 5px 10px;
  margin-bottom: 10px;
}

.smart-table-operate {
  .ant-btn {
    padding: 0px 3px !important;
  }
}

.smart-table-column-operate {
  float: right;
}

.smart-query-form-row:not(:first-child) {
  margin-top: 8px;
}
...
...
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22

### 2.1 index.less 文件

这个是所有样式的入口文件，同时因为了 所有 `ant design vue` 的样式文件，并没有做按需导入，以及全局 样式变量的定义。

less
```
@import 'ant-design-vue/dist/antd.css';
@import './smart-admin.less';
/**  页面头部 begin **/
@hover-bg-color: rgba(0, 0, 0, 0.025);
@hover-bg-color-night: rgba(255, 255, 255, 0.025);
@header-light-bg-hover-color: #f6f6f6;
@header-height: 80px;
@header-user-height: 40px;
@page-tag-height: 40px;
@theme-list: light, dark, night;
....
....
```

1
2
3
4
5
6
7
8
9
10
11
12

#### 为什么没有按需导入？

仔细看第一行`@import 'ant-design-vue/dist/antd.css';`，导入了 `ant-design-vue`全部样式，且为css文件;

* 因为SmartAdmin 是中后台，且一定是电脑端操作，网速、硬件都会很好，所以按需导入就显得价值不大了
* 因为SmartAdmin 是中后台，页面很多且复杂，几乎会用到所有的 `ant-design-vue`组件，所以按需意义也不大了

#### 为什么导入css

请先注意如下代码的区别：

less
```
@import '~ant-design-vue/dist/antd.less';
@import 'ant-design-vue/dist/antd.css';
```

1
2

一个是导入css，另一个是导入less；SmartAdmin用的是css！

* 选择导入css，这样vite3启动速度很快，因为不用解析和编译less；
* 选择导入less，这样vite3启动第一次很慢，以后会很快，因为需要解析大量的less变量；除非从`/dist/antd.less`中抽丝剥茧，只导入某些less变量；
* 如果必须导入less，vite有没有方法变得第一次启动快？ 尤大已经回复过了，无解，因为 less都需要提前编译，即使webpack也需要提前编译less，所以启动速度 取决于你 导入了 多少less；

以上，懂了吗？


### 水印效果

# 水印效果

## 一、背景与问题

知道中后台一般都是企业内部使用，里面含有重要的信息，不可 拍照或者截图外传，那么这个时间水印就变得非常重要。
 水印一般为：`企业 + 部门 + 姓名 + 时间`

## 二、架构思想

都知道 中后台一般左侧为 菜单，顶部为用户操作区，只有中间为具体的`content`内容区。所以水印只需要加在中间 `content区域即可`。
 在SmartAdmin中 中间的content 为layout，所以在 多个layout中进行水印操作；
 水印可以单出抽成一个库lib，因为其与任何业务无关。

**水印代号 watermark**

## 三、具体使用

在`src/layout/side-expand-layout.vue`中加入水印，代码如下：
```js

<script setup>
  import { computed, onMounted, ref } from 'vue';
  import watermark from '/@/lib/smart-wartermark';
  import { useAppConfigStore } from '/@/store/modules/system/app-config';
  import { useUserStore } from '/@/store/modules/system/user';
  import { HOME_PAGE_NAME } from '/@/constants/system/home-const';

  //页面初始化的时候加载水印
  onMounted(() => {
    watermark.set('smartAdminLayoutContent', useUserStore().actualName);
  });
</script>
```

1
2
3
4
5
6
7
8
9
10
11
12

## 四、实现原理

水印方法`src/lib/smart-wartermark.js`
```js

/*
 * 水印
 * @Author: jw
 * @lastUpdated:      2023-09-06 20:50:10
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import dayjs from 'dayjs';
/**
 *  水印DOM id
 */
const WATER_MARK_DOM_ID = 'smart_admin_water_mark';
let smartAdminWaterMarkIntervalId = null;
/**
 * 因为modal的z-index为1000，所以为了modal的黑色背景隐藏掉，z-index为 999
 */
function setWatermark(id, str) {
  //删掉之前的水印
  if (document.getElementById(WATER_MARK_DOM_ID) !== null) {
    document.getElementById(WATER_MARK_DOM_ID).remove();
  }
  str = str + ' ' + dayjs().format('YYYY-MM-DD HH:mm');
  //创建一个画布
  const can = document.createElement('canvas');
  //设置画布的长宽
  can.width = 400;
  can.height = 200;
  const cans = can.getContext('2d');
  //旋转角度
  cans.rotate((-15 * Math.PI) / 150);
  cans.font = '16px Microsoft JhengHei';
  //设置填充绘画的颜色、渐变或者模式
  cans.fillStyle = 'rgba(190, 190, 190, 0.30)';
  //设置文本内容的当前对齐方式
  cans.textAlign = 'left';
  //设置在绘制文本时使用的当前文本基线
  cans.textBaseline = 'middle';
  //在画布上绘制填色的文本（输出的文本，开始绘制文本的X坐标位置，开始绘制文本的Y坐标位置）
  cans.fillText(str, can.width / 8, can.height / 2);
  const div = document.createElement('div');
  div.id = WATER_MARK_DOM_ID;
  div.style.pointerEvents = 'none';
  div.style.top = '0px';
  div.style.left = '0px';
  div.style.position = 'absolute';
  div.style.zIndex = '999';
  div.style.width = '100%';
  div.style.height = '100%';
  div.style.background = 'url(' + can.toDataURL('image/png') + ') left top repeat';
  document.getElementById(id).appendChild(div);
}

const watermark = {
  show: function () {
    document.getElementById(WATER_MARK_DOM_ID).style.display = 'block';
  },
  hide: function () {
    document.getElementById(WATER_MARK_DOM_ID).style.display = 'hide';
  },
  // 该方法只允许调用一次
  set: function (id, str) {
    // 如果存在水印，则不允许再调用了
    if (document.getElementById(WATER_MARK_DOM_ID) !== null) {
      alert('已经添加过全局水印了，请不要再重复添加!');
      return;
    }
    setWatermark(id, str);
    //每隔1分钟检查一次水印
    smartAdminWaterMarkIntervalId = setInterval(() => {
      setWatermark(id, str);
    }, 60000);

    window.onresize = () => {
      setWatermark(id, str);
    };
  },
  // 清空水印
  clear: function () {
    document.getElementById(WATER_MARK_DOM_ID).remove();
    window.removeEventListener('resize', setWatermark);
    if (smartAdminWaterMarkIntervalId) {
      clearInterval(smartAdminWaterMarkIntervalId);
    }
  },
};
export default watermark;
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |


### 好用的内置组件

# 好用的内置组件

## 一、背景与问题

在开发的过程中经常会复用一些组件，这些组件没有业务属性，但是很常用。

## 二、Loading组件

内置SmartLoading组件，代码位于`src/framework/smart-loading`

* 此Loading基于 pinia状态管理，全局唯一
* 使用时候直接import 引入
* 开启： SmartLoadin.show()
* 关闭： SmartLoadin.hide()

举例：
```js

  import { SmartLoading } from '/@/components/framework/smart-loading';

  async function getUserTableColumns(tableId, columns)  catch (e) {
      smartSentry.captureError(e);
    } finally 
  }
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15

## 三、枚举组件

枚举组件支持:

* 下拉框 `smart-enum-select`
* 单选框 `smart-enum-radio`
* 复选框 `smart-enum-checkbox`

具体代码，请见`src/components/framework`;

使用举例：
```js

<template 中>
<SmartEnumSelect enum-name="GOODS_STATUS_ENUM" v-model:value="queryForm.goodsStatus" width="160px" />
....
<script 中>
import { GOODS_STATUS_ENUM } from '/@/constants/business/erp/goods-const';
import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
...
```

1
2
3
4
5
6
7

## 四、字典组件

`dict-select` 字典下拉框。 代码在 `src/components/support/dict-select`;

## 五、文件组件

* 文件预览 file-priview
* 文件预览弹窗 file-priview-modal
* 文件上传 file-upload


### 项目默认配置

# 项目默认配置

## 一、背景与问题

前端支持默认的项目配置，可以直接针对自己的项目提前进行默认配置

## 二、具体使用

具体代码在 `src/config/app-config.js`， 代码如下：
```js

/*
 * 应用默认配置
 *
 * @Author: jw
 * @lastUpdated:      2023-09-03 22:07:01
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
export const appDefaultConfig = {
    // i18n 语言选择
    language: 'zh_CN',
    // 布局: side 或者 side-expand 或者 top
    layout: 'side',
    // 侧边菜单宽度 ， 默认为200px
    sideMenuWidth: 200,
    // 菜单主题
    sideMenuTheme: 'dark',
    // 顶部菜单页面宽度
    pageWidth: '99%',
    // 标签页
    pageTagFlag: true,
    // 面包屑
    breadCrumbFlag: true,
    // 页脚
    footerFlag: true,
    // 帮助文档
    helpDocFlag: true,
    // 水印
    watermarkFlag: true,
    // 网站名称
    websiteName: 'SmartAdmin 3.X',
};
```

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33

以上可以根据 实际项目需要进行修改

## 三、实现原理

在状态管理pinia中，有个 `appConfig` 状态，其中默认读取`src/config/app-config.js` 作为初始值，然后在其他各个地方引用状态管理使用。

## **联系我们**

[1024创新实验室-主任：卓大](https://zhuoda.vip)，混迹于各个技术圈，研究过计算机，熟悉点 java，略懂点前端。

[1024创新实验室](https://1024lab.net)， 卓大的软件公司，致力于成为中原领先、国内一流的技术团队， 以AI+数字化为驱动，用技术为产业互联网提供无限可能， 业务如下:

* 供应链（网络货运、大宗贸易进销存ERP、物流TMS、B2B电商、仓储WMS、AI提效等）
* 教育（就业创业大数据平台、继续教育平台、在线教育系统、题库等）
* AI+软件（软件定制外包、数据大屏、国产化改造、人员外包、技术顾问、技术培训等）
* 欢迎各类合作哦~

|  |  |  |  |
| --- | --- | --- | --- |
|  |  |  |  |
| 加微信： 卓大   拉你入群，一起学习 | 公众号 ：六边形工程师   分享：赚钱、代码、生活 | 请 “1024创新实验室”   烩面里加肉  咖啡配胡辣汤,提神又饱腹 | 抖音 : 六边形工程师  直播：赚钱、代码、中医 |