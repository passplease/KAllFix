
##  状态 
这个项目包含了2个mod     k_all_fix， K_multi_threading

理论上1.17+ java11+都可以用但是需要改源码把没有的部分删除

多线程只能在服务器使用客户端会进不去游戏

可以用-DKMT_D=false和-DKAllFix_D=false禁用独立mod

##  使用方法

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
- 
- 修复Biolith兼容问题
- (MCreator所有模组, terramity（尝试解决这一类问题）)
- 添加可独立关闭的多线程
- 解决alexscaves磁场依托答辩问题
- 解决createdieselgenerators不会用导致他的mixin依托答辩问题
- (不安全)（需要开启-DKAF-NbtIoMixin_NotGZip=true)加一个try catch解决nbtio的gzip问题
- 解决老destroy的数据库连不上就崩服问题

##  可开启
- -DIndependencePlayer=true 开启玩家异步，这玩意大概率是负优化
- -DFixBiolithBugMode2=true 开启这个尝试解决大部分Biolith兼容问题但是可能会出大bug
- -Dgtceu.MedicalConditionTrackerMixin=true 禁止添加gtm的辐射
- -DKAF-Fix_fabric-object-builder-api.jar=true 修复信雅互联的fabric-object-builder-api不兼容47.3.27的问题，我因为这个问题让这个mod晚发了半个月他们还没有解决
