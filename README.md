参考链接：上、下
https://www.jianshu.com/p/a8e43ad5d7d2
https://www.jianshu.com/p/0cca211df63c

Messager实现进程间通信
https://blog.csdn.net/luoyanglizi/article/details/51594016


----------------------------------------------------------------------------------------------------------------------------

使用AIDL：
AIDL，即Android Interface Definition Language，Android接口定义语言。它是一种IDL语言，可以拿来生成用于IPC的代码。它其实就是一个模板。为在我们的使用中，实际上起作用的并不是我们写的AIDL代码，而是系统根据它生成的一个IInterface实例的代码。其实AIDL就是为了避免我们一遍遍的写一些千篇一律的代码而出现的一个模板。
那么如何使用AIDL来通过bindService()进行线程间通信呢？基本上有下面这些步骤：
服务端创建一个AIDL文件，将暴露给客户端的接口在里面声明
在service中实现这些接口
客户端绑定服务端，并将onServiceConnected()得到的IBinder转为AIDL生成的IInterface实例
通过得到的实例调用其暴露的方法

AIDL 支持以下类型。
Java 编程语言中的所有原语类型（如 int、long、char、boolean 等等）
String
CharSequence
List 
List 中的所有元素都必须是以上列表中支持的数据类型、其他 AIDL 生成的接口或您声明的可打包类型。 可选择将 List 用作“通用”类（例如，List）。另一端实际接收的具体类始终是 ArrayList，但生成的方法使用的是 List 接口。
Map 
Map 中的所有元素都必须是以上列表中支持的数据类型、其他 AIDL 生成的接口或您声明的可打包类型。

应用场景：
支付宝（支付SDK调起支付宝进行支付，支付宝提供AIDL接口）、音乐播放（可以通过其他应用控制音乐播放）

用法总结：（参考demo）
如果需要传递非基本类型的对象，该对象类需要序列化，同时需要有一个同名的aidl文件，声明这个对象  ，声明的时候 parcelable为小写
如果该对象支持out流向传递的话，需要自己写readFromParcel(Parcel dest)方法，获取变量值顺序要与writeFromParcel(Parcel dest)一致。
当传递非基本类型的对象，该.java类建议放在aidl包下，这样方便直接把aidl文件包拷贝给使用端；为了能让gradble找到该类，需要在客户端和服务端的buidle.grable中配置
android{
 sourceSets {
    main {
        java.srcDirs = ['src/main/java', 'src/main/aidl']
    }
 }
}

5.传递非基本支持类型的对象，必须在方法的入参类型前，显示的声明int 、out、inout类型 （见demo）
int类型，可以把值传给服务端，服务端修改盖对象，不会影响客户端，
inout类型，可以把值传给服务端，服务端修改，会直接影响客户端，客户端会自动同步修改该对象
6.编译完AIDL之后，reBuild，自动生成对应的AIDL代码
7.客户端绑定服务端服务的时候，intent需要setAction（"服务的action"）以及setPackage（"服务端包名"），显示指定包名
8.客户端的aidl文件夹和服务端完全一样，直接copy，服务端的aidl文件夹，放在和java并行目录下即可，同时buidle.grable中需要配置步骤三内容


思路总结：
  AIDL其实是进程间通信的模板接口，我们只需要编写AIDL接口，他自动会生成对应的子类，子类中封装了进程间通信读写和传递数据的代码，
然后我们在服务端的服务中创建AIDL接口子类的实例，然后在onBind的时候将这个接口实例传递给客户端，客户端通过绑定的方式，在绑定成功的地方拿到该接口实例， 接下来客户端就可以使用实例进行对服务端方法的调用；

原理总结：（Binder 机制）
客户端拿到的 IBinder service 恰恰是客户端与服务端通信的核心类，正是通过用它调用的 IBinder 的transact() 方法，我们得以将客户端的数据和请求发送到服务端去。
 服务端使用Binder的onOransact方法接受数据；
---------------------------------------------------------------------------------------------------------------------

使用Messenger
跨进程通信，我们总是第一时间想到AIDL(Android接口定义语言)，实际上，使用Messenger在很多情况下是比使用AIDL简单得多的，具体是为什么下文会有比较。
大家看到Messenger可能会很轻易的联想到Message，然后很自然的进一步联想到Handler——没错，Messenger的核心其实就是Message以及Handler来进行线程间的通信。
Messenger实现IPC的步骤：
服务端实现一个Handler，由其接受来自客户端的每个调用的回调
使用实现的Handler创建Messenger对象
通过Messenger得到一个IBinder对象，并将其通过onBind()返回给客户端
客户端使用 IBinder 将 Messenger（引用服务的 Handler）实例化，然后使用后者将 Message 对象发送给服务
服务端在其 Handler 中（具体地讲，是在 handleMessage() 方法中）接收每个 Message

Messenger与AIDL的比较
 	首先，在实现的难度上，肯定是Messenger要简单的多——至少不需要写AIDL文件了(虽然如果认真的究其本质，会发现它的底层实现还是AIDL)。另外，使用Messenger还有一个显著的好处是它会把所有的请求排入队列，因此你几乎可以不用担心多线程可能会带来的问题。
但是这样说来，难道AIDL进行IPC就一无是处了么？当然不是，如果项目中有并发处理问题的需求，或者会有大量的并发请求，这个时候Messenger就不适用了——它的特性让它只能串行的解决请求。另外，我们在使用Messenger的时候只能通过Message来传递信息实现交互，但是在有些时候也许我们需要直接跨进程调用服务端的方法，这个时候又怎么办呢？只能使用AIDL。



