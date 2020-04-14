# note

**当前分支是为适应java9以后的模块化jdk而正在开发的分支**
**需要使用JDK13**

和主分支master不同，本分支不在使用spring作为注入环境，而使用我自己
制作的另外的东西作为开发环境，为此系统提供注入支持和jpa等。

超大号的幺蛾子来了，当前分支是最新的分支，删除了spring的依赖，使用自建的框架为基础，整体
重制了整个工具，以后也会从这个框架为基础完成其他的开发。

这是一个用来写作的小工具，基于JavaFX，内部采用H2数据库进行存储，可以导出为HTML格式。
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
* [ ] 实现主题功能（UI 、Themes）
* [x] ~~添加悬浮窗~~添加托盘菜单（Tray Icon）
* [x] 增加LaTeX公式支持（Latex Support）
* [x] 左右结构，允许拖动以改变两侧区域大小
* [ ] 增加公式编辑器
* [ ] 完善工具栏
* [x] 使用jdk11以上的版本，以获取性能的提升

### Bug

旧版bug日志

* [x] 编辑器没有纵向滚动条（No ScrollBar On Editor） 2019-5-17 fixed
* [x] 表格语法高亮出现bug （HighLight Bugs）2018-11-19 fixed
* [x] WebView在最大化的时候出现不能滚动的问题 2019-12-16 fixed

### how to build

1. clone FXApplication repo(https://github.com/SW-Fantastic/FXApplication)

2. 分别编译并install三个maven模块到本地。

3. clone本project并编译运行。

4. 请添加这个JVM参数：--add-opens=javafx.controls/javafx.scene.control.skin=org.controlsfx.controls
