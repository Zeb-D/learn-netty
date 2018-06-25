# learn-netty

专门记录学习netty 各种操作，hello-world、websocket、http、自定义协议、与Spring整合等等

自定义协议协议注意点：粘包、拆包，可以使用定长解析，网上资料有好多种解决‘方案；

另外附上个人java测试server的client代码，也上传tcp测试工具，在review/tool下的sockettool.jar



### websocket-room

仿照直播间成员上线时，全局发送某某成员上线消息

运行方式：

1. maven 编译：mvn clean install -DskipTests
2. 进入该模块下的/target 目录下，也可以copy jar 到您喜欢的目录下
3. 命令行运行：java -jar websocket-room.jar 7120 ；端口（7120）可有可无





运行提升区域：

1. 想通过获取vm运行参数，从而指定 主副线程组的大小， 如 java -jar xxx.jar -Dnetty.server.parentgroup.size=2 -Dnetty.server.childgroup.size=4；通过实现发现，该模块加入这参数启动不了，但其他项目加入是可以启动的，如https://github.com/Zeb-D/distributed-id ；