## 关于这个工具

这个工程是我个人自己使用的一个小工具，主要是用来摘抄和记录在平时遇到的各类资料使用。
在工程最早的时候，我还不知道GitHub，也不会Markdown什么的，连javaFX也还不清楚，说起来还真是
十分久远的时候了。

在那段时间，我使用一个叫做XDoc的工程的Swing客户端里面的一个组件，作为自己的富文本编辑器，使用了
很多反射什么的，整体都乱七八糟，不过即使是这样，也最终稳定运行了相当的时间，后来就是javafx了，有了javafx
之后，有一段时间一直考虑接下来是swing还是javafx，不过最终感觉javafx的界面更好看一点。

所以后来的project都是javafx为基础的了。
后来有了spring-boot，和javafx配合起来也是相当不错，不过由于这个作品比较早期，很多地方处理的还是非常粗糙的，
到现在为止也有很大的改进空间。

到现在为止，springboot已经不再适合工程使用，因此我现在已经不再基于springboot了，
工程采用了我的自研框架FXApplication进行开发，因此我得以升级jdk到相对高的java13，
往后如果时机合适，我将会继续跟随最新版本jdk进行开发。

## Build

#### 旧版的build方式
这个是一个普通的maven工程，直接以maven形式导入就可以，
但是由于功能需要的markdown组件比较多，所以下载的时间大概会长一些
他是一个spring-boot的项目，因此可以使用 `mvn spring-boot:run`
这样的命令进行启动。


其实导入之后事情交给IDE就可以了，不管是IDEA还是Eclipse对maven支持的都是
比较全面的。

#### 新版的build方式

请首先clone我的FXApplication类库，并且在本地install，这里面有三个maven的模块
他们都需要install进本地的maven仓库，然后clone本仓库并且运行，我建议在运行前进行maven的
clean操作，以防止出现bug。

如果需要package，请使用maven的package操作，如果package失败，请再试一次。

## 工程结构

这是一个早期作品，所以结构看起来比较乱，我会在接下来的时间进行整理
