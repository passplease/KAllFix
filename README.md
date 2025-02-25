
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
- 修复Biolith兼容问题(范围包括重新生成MultiNoiseBiomeSource的mod，例如所有的使用MCreator的所有有这个操作的mod)
- 并不是修复了所有的使用MCreator的模组，小部使用分模组需要定制修复代码（例如）Terramity
- (MCreator所有模组, terramity（尝试解决这一类问题）)
- 添加可独立关闭的多线程
- 解决alexscaves磁场依托答辩问题
- 解决createdieselgenerators不会用导致他的mixin依托答辩问题
- (不安全)（需要开启-DKAF-NbtIoMixin_NotGZip=true)加一个try catch解决nbtio的gzip问题
- 解决老destroy的数据库连不上就崩服问题
- 指令:
- |- debug_GetterClassFile 类名
- |-| 获取游戏最终游戏运行的类型文件数据，会保存在游戏目录下面“保存时间的时间戳_save.class”
- |- SetterWorldConfig [维度的注册id, ClearErrorSize, RemoveRemoveErrorSize]
- |-| 设置之后并不会保存
- |-| 维度的注册id:
- |--| setM2 [true, false] 设置维度的实现过程方式是另一种，但是可能会卡死
- |--| setMultiThreading [数量] 默认0，设置多线程同时运行任务的数量，但是可能会卡死
- |-| ClearErrorSize 直接清除报错导致服务器崩溃的次数
- |-| RemoveRemoveErrorSize 让服务器不会纪录崩溃的次数，无限拦截

##  可开启
- -DIndependencePlayer=true 开启玩家异步，这玩意大概率是负优化
- -DKAF-gtceu.MedicalConditionTrackerMixin=true 禁止添加gtm的辐射
- -DKAF-Fix_fabric-object-builder-api.jar=true 修复信雅互联的fabric-object-builder-api不兼容47.3.27的问题，我因为这个问题让这个mod晚发了半个月他们还没有解决

## 问题
- 多线程会出现线程池拦截报错不会崩溃的情况，但是有一个拦截次数超过这个次数会报错，这个次数是可以设置的