# learn-netty

专门记录学习netty 各种操作，hello-world、websocket、http、自定义协议、**代理**、心跳、与Spring整合、定制与SpringMvc-dispatcher、其他中间件整合  等等

自定义协议协议注意点：**粘包、拆包**，可以使用定长解析，网上资料有好多种解决‘方案；

在进行ChannelHander 处理业务时，请注意业务处理时间，虽然加入了主副线程，但有可能会阻塞副线程组，所以可以视情况加入**业务处理线程组**；

在配置主副线程组大小时，**一般**副线程组大小是主线程组 2倍，主线程组大小不宜设置过高，尽量与物理机的核数一直，这样减少了线程上下文切换耗时。理论上，这样设置可以达到几百万的tcp并发；

本次是基于netty4，netty每个大版本变化比较大，如果不深入分析代码，很可能导致以前代码会出问题，如netty3升级到netty4，对缓冲的处理方式不一样（netty4回收线程只能是当前线程回收，切换其它线程后就会失效，具体可以看EventLoop实现），可能会报内存溢出 等等；

另外附上个人java测试server的client代码，也上传tcp测试工具，在https://github.com/Zeb-D/my-review/tree/master/tool 下的sockettool.exe。



## websocket-room

仿照直播间成员上线时，全局发送某某成员上线消息

**运行方式**：

1. maven 编译：mvn clean install -DskipTests
2. 进入该模块下的/target 目录下，也可以copy jar 到您喜欢的目录下
3. 命令行运行：java -jar websocket-room.jar 7120 ；端口（7120）可有可无
4. 如果想指定主副线程组大小请使用：java  -Dnetty.server.parentgroup.size=2 -Dnetty.server.childgroup.size=4 -jar websocket-room.jar 7120 


**注意事项：**

一般我们启动jar方式会指定一些系统参数，可以通过Integer.getInteger("netty.server.childgroup.size") 来获取启动时参数，-D 代表系统参数，可以在程序使用System.getProperty("netty.server.childgroup.size”)，但这命令一定得在 -jar 命令之前，否则会启动不了；



