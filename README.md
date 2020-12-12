# note

**当前分支是为适应java9以后的模块化jdk而正在开发的分支**
**需要使用JDK13**

和旧版本不同，此工具不在使用spring作为注入环境，而使用我自己
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

 * [x] 自动提示功能的提示框位置不正确 (Fixed 2020-12-12)

旧版bug日志

* [x] 编辑器没有纵向滚动条（No ScrollBar On Editor） 2019-5-17 fixed
* [x] 表格语法高亮出现bug （HighLight Bugs）2018-11-19 fixed
* [x] WebView在最大化的时候出现不能滚动的问题 2019-12-16 fixed

### how to build

1. clone FXApplication repo(https://github.com/SW-Fantastic/FXApplication)

2. 分别编译并install三个maven模块到本地。

3. clone本project并编译运行。

4. 请添加这个JVM参数：--add-opens=javafx.controls/javafx.scene.control.skin=org.controlsfx.controls

5. 请特别注意，javafx是在maven的，如果是首次使用javafx可能会下载失败从而出现找不到符号（一般是javafx的包）
编译错误，如果出现这种情况请删除用户目录-.m2-repository-org-openjfx文件夹，然后重新导入maven。

6.运行前请首先对项目执行mvn clean之类，防止出现奇怪的问题。建议添加此命令到idea的build之前。

7.更新此项目后请及时更新FXApplication项目并重新install。

* * *
## 构建JavaRuntime所需要的模块列表：
```
java.base,
java.naming,
java.scripting,
javafx.fxml,
javafx.controls,
javafx.base,
javafx.graphics,
jdk.jfr,
java.datatransfer,
java.prefs,
java.xml,
java.sql,
java.transaction.xa,
java.desktop,
jdk.unsupported,
java.instrument,
javafx.web,
javafx.media,
java.compiler,
java.xml.crypto,
jdk.unsupported.desktop,
java.net.http,
jdk.jsobject,
jdk.xml.dom,
java.prefs,
java.transaction.xa,
java.logging,
javafx.swing,
java.management.rmi,
jdk.zipfs
```
## 关于如何生成应用
首先，你需要至少jdk14以上的一个高版本jdk，虽然目前我没有使用它。

有了jdk14之后，请使用jlink，以上述列表的模块为基础生成运行时，
并且删除运行时中的lib文件夹内jrt-fs.jar。

### 在windows上创建EXE

以上基础上，你需要安装[WIX工具](https://wixtoolset.org/)，在这里下载
然后安装，如果是绿色版就解压，放到合适的位置后，将wix的目录加入PATH环境
变量。

现在，请执行maven的package指令，依赖的jar会在build的时候被复制出来。

接下来，请允许jdk14的bin目录的jpackager指令，参数如下：

```
jpackage --runtime-image [运行时路径]  --type app-image -n [应用名称] -p ../target/lib --icon [图标路径] -m noteEditor/org.swdc.note.NoteApplication
```

那么现在你就可以在工程目录下找到一个新的文件夹，这就是创建好的发布包。

请删除发布包的app的modes内部javafx相关包（javafx开头的那些）。

然后复制assets文件夹到发布目录。

那么到此为止，就全部完成了，可执行文件应该可以正确运行。