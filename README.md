# note

这是一个用来写作的小工具，基于JavaFX，以及SpringBoot，内部采用H2数据库进行存储，可以导出为HTML格式。
当前版本已经可以正常使用了，纵版UI效果比较好，如果是Mac系统的话，中文输入可能会有些输入法的bug。

目前markdown采用flex-marker库进行解析，可以支持大多数markdown语法。

this is a tool for write Markdown Format documents, implements by spring-boot javaFX 、 H2Database and flex-marker
current version is not good at mac or linux OS

### 运行截图 （Screen Shots）

![主界面](https://github.com/SW-Fantastic/note/blob/master/pages/src/assets/screenShot0.png)

![编辑界面](https://github.com/SW-Fantastic/note/blob/master/pages/src/assets/screenShot2.png)

### 开发进程（Development Progress）

* [x] 基础编辑器的实现 （Base Editor）
* [x] 使用H2存储数据 （H2 Data Persist）
* [x] 支持文档的导入和导出 （Document import and export）
* [x] 实现主题功能（UI 、Themes）
* [x] ~~添加悬浮窗~~添加托盘菜单（Tray Icon）
* [x] 增加LaTeX公式支持（Latex Support）
* [x] 左右结构，允许拖动以改变两侧区域大小
* [ ] 增加公式编辑器
* [ ] 完善工具栏

### Bug

* [x] 编辑器没有纵向滚动条（No ScrollBar On Editor） 2019-5-17 fixed
* [x] 表格语法高亮出现bug （HighLight Bugs）2018-11-19 fixed
* [x] WebView在最大化的时候出现不能滚动的问题 2019-12-16