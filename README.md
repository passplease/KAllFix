
##  状态 
这个项目包含了2个mod     k_all_fix， K_multi_threading

理论上1.17+ java11+都可以用但是需要改源码把没有的部分删除

多线程只能在服务器使用客户端会进不去游戏

可以用-DKMT_D=false和-DKAllFix_D=false禁用独立mod

##  食用方法

1: 把gradle.properties里的代理删了 
    
    .\gradlew.bat jar
 
或者

    ./gradlew jar

然后把build/libs的文件复制到游戏目录

然后

    java -jar k_multi_threading-xxx.jar -i [空或安装目录]

按照提示操作或者去n1luik.K_multi_threading.core.util.Util看源码

会生成k_multi_threading-base.jar和k_multi_threading-asm.jar两个文件

##  功能
- 修复Biolith兼容问题
- 添加可独立关闭的多线程
- 解决alexscaves磁场依托答辩问题
- 解决gtm一直有辐射问题（就是把辐射ban了[捂脸]）
- 解决createdieselgenerators不会用导致他的mixin依托答辩问题

  