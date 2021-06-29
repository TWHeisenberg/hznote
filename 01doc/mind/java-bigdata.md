[TOC]

# java

## 1. 集合

两种集合框架：collection和map，其中collection分为set和List。

#### hashMap

是一个kv形式的数据集合，jdk1.7以数组+链表实现，jdk1.8之后以数组+链表+红黑树实现，当链表长度达到8会自动转为红黑树。

jdk1.7采用头插法，1.8采用尾插法。

##### put方法

```java
/**
 * Implements Map.put and related methods
 *
 * @param hash hash for key
 * @param key the key
 * @param value the value to put
 * @param onlyIfAbsent if true, don't change existing value
 * @param evict if false, the table is in creation mode.
 * @return previous value, or null if none
 */
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
	// 1.计算key 的hash
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 2.如果table未初始化，则初始化并且计算key的下标
    // 3.如果数组的目标下标node是null,说明没人，直接存
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
        	// 4.如果位置有人了，并且key相等，原来的key存临时变量
            e = p;
        else if (p instanceof TreeNode)
        	// 5.如果有人的位置是红黑树，就用红黑树的方法put
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                	//7. 遍历链表，加到链表最后
                    p.next = newNode(hash, key, value, null);
                    // 8. 如果链表长度大于等于8,转为红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                	//6. 如果链表中已经有了key, 返回
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    if (++size > threshold)
        resize();
    // 7.如果长度达到阈值，resize
    afterNodeInsertion(evict);
    return null;
}
```
##### get方法

```java
/**
 * Implements Map.get and related methods
 *
 * @param hash hash for key
 * @param key the key
 * @return the node, or null if none
 */
final Node<K,V> getNode(int hash, Object key) {
	//1.计算key的hash,根据hash取模下标，判断下标节点是否存在
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
        	// 2. 不为空，检查第一个node跟想要的key是不是相等，是就返回。
            return first;
        if ((e = first.next) != null) {
        	// 3.第一个node不是,判断如果是红黑树，就用红黑树的方法get
        	//如果是链表就遍历链表get
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

##### resize

```java
/**
 * The load factor used when none specified in constructor.
 * 默认加载因子，超过0.75触发resize
 */
static final float DEFAULT_LOAD_FACTOR = 0.75f;
 /**
  * The default initial capacity - MUST be a power of two.
  * 初始化容量，包括resize后的容量必须是2的次方
  */
 static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
```
创建一个新的数组，长度是原来2倍

rehash

##### 容量为什么必须是2的幂

为了实现hash的均匀分布，减少hash碰撞：length-1是奇数，它的二进制最后一位是1，可以保证取模运算hash & (length-1) 的最后一位是1或者0。

如果容量是奇数，length-1就是偶数，它的二进制末位是0，这样hash & (length-1) 的末位也肯定是0。

##### jdk1.8为什么改成尾插法

为了安全，防止链表成环的问题。

单线程下没有问题，头插法会保证新插入的元素在第一位，所以会打乱链表的顺序，在多线程put的时候可能会引起resize，需要rehash，这个过程可能出现链表成环。而尾插法将元素插入末位，保持链表插入的顺序。

##### equals()和hashCode()

两个方法都继承自object，都是用来比较两个对象是否相等。

两个方法一起重写是为了保证相同对象的hashCode一定相等，比如hashMap中会根据对象的hash值计算数组下标，如果已经有了元素调用equals方法判断是否相等。

##### TreeMap

基于红黑树实现的kv的map，可以对key自动进行排序，需要设置Comparator或者key对象要实现Comparable的口。

##### LinkedHashMap

是HashMap的子类，基于hashMap + 链表实现。有两者有序模式：访问有序，插⼊顺序。访问有序默认关闭的，如果打开其实就是LRU的一个实现。

##### HashTable

其实跟hashMap的实现基本一样，但是get，put方法都加了synchronized实现加锁。保证线程安全但性能低。多线程场景建议使用concurrentHashMap。

##### concurrentHashMap

TODO

#### List

##### ArrayList，LinkedList，Vector区别

ArrayList底层实现是数组，特点是查询快，增删慢。初始容量10，每次扩容1.5倍。

LinkedList底层实现是红黑树，特点是是增删快，查询慢。

Vector实现跟ArrayList实现一样，但是所有的方法都加了synchronized，线程安全，效率差于ArrayList



##### ArrayList的多线程安全实现

CopyOnWriteArrayList	TODO



#### Set

##### HashSet

基于红黑树实现，无序不可重复。



## 2. 并发

#### synchronized

jvm中锁的实现，两种使用方法，本质都是每个对象都有一个监视器（monitor），通过获取监视器来实现排他功能。

修饰代码块：

对同步快使用了monitorenter和monitorexit指令实现的，线程执行monitorenter时尝试获取monitor的所有权，monitor都有自己的计数器，如果monitor的进入数为0，则该线程进入monitor，计数器+1，成为对象锁的持有者，这个过程是可重入的，如果持有线程再次获取monitro,计数器+1。如果monitor进入数不为0，说明被其他线程持有，则线程进入同步队列，状态变为BLOCKED；

修饰方法：

通过方法上的修饰符ACC_SYNCRONIZED来完成的。

syncronized锁是保存在对象头里面的Mark Word中的。

jdk1.6开始为了减少锁释放和获取的性能，引入了“偏向锁”和“轻量级锁”，所以目前锁是有4中状态：	

对象刚初始化时无锁状态，当有线程访问同步块并且获取锁时，会在对象头和帧栈的锁记录里面记录偏向线程的ID，并在Mark Word中的偏向锁标志设置为1。

如果有其他线程竞争则偏向锁被撤销（等到全局安全点，没有正在执行的字节码，先暂停偏向的线程，检查如果线程不活动，置为无锁，或者锁记录中有其他线程，偏向其他的线程），升级为轻量级锁，过程：JVM在当前线程帧栈中创建用于保存锁记录的空间，然后将地偶像头中的Mark Word复制到锁记录中，然后线程通过CAS将对象头中Mark Word的引用指向锁记录中的，如果成功则过得锁，失败，表示有其他线程竞争，自旋获取锁。jvm中有参数设置自旋的次数，默认10次，超过会膨胀为重量级锁（TODO：自适应锁？）

轻量级锁竞争过程中通过CAS替换对象头中的Mark Word，如果失败升级为重量级锁。过程不可逆

各个级别锁的优缺点：

| 锁       | 优点                           | 缺点                                   |
| -------- | ------------------------------ | -------------------------------------- |
| 偏向锁   | 快，加锁和解锁几乎不需额外消耗 | 如果有其他锁竞争，撤销锁会带来额外消耗 |
| 轻量级锁 | 响应速度快，竞争线程不会阻塞   | 自旋消耗CPU                            |
| 重量级锁 | 线程竞争不会自旋，不消耗CPU    | 线程阻塞，响应速度慢                   |

​	

#### 对象和对象头

对象组成部分：

​	对象头

​	实例数据

​	对齐填充字节：JVM要求一个对象占用内存是8bit的整数，用来填充的。

对象头：

​	Mark Word

​	指向类的引用

​	数组长度（如果是数组）

Mark Word:

​	在32位JVM中大小是32bit,在64位JVM中是64bit，有几个标志位组成：

锁标志位，是否偏向锁，分代年龄，hashCode信息。

#### volatile

自身有两种特性：

**可见性**，对一个volatile变量，可以看到其他线程的最后写入。

volatile变量执行写操作后，JMM会把工作内存中的最新变量刷新到主内存中，并且使其他线程的工作内存中的变量置为无效（总线嗅探机制），这样强制到主内存中读取。

**有序性**

volatile通过编译器生成字节码时，在指令序列中添加“lock”指令，在volatile变量的读写前后加上内存屏障禁止指令的重排序，从而保证有序性

**不保证原子性**，volatile变量的读和写都是原子的，但是对复合操作如i++,不保证原子性。

#### Java内存模型（JMM）

是JVM中定义线程和主内存的关系的一个抽象概念：线程之间的共享变量比如堆内存中的实例对象，静态变量等保存在主内存，而每个线程都有一个本地内存，保存本地的副本。从硬件层面看，这个本地内存涵盖了L1,L2高速缓存，缓冲区等。java之间线程的通信通过JMM控制。

#### happens-before

happens-before规则用来阐述操作之间的内存可见性，我个人理解是这个规则JMM对程序员的做出的约定，代码执行的操作是按照顺序的，同时也是编译器和处理器遵循的规定：只要不改变程序执行的结果，怎么优化都行。

四个规则：

1.程序顺序规则：一个线程中的每个操作happens-bore任意后续操作

2.监视器规则：解锁happens-before加锁

3.volatile变量规则：volatile变量的写happens-bore 任意后续这个volatile变量的读

4.如果A happens-before B, B happens-before C,则A happens-before C

#### 重排序

编译器和处理器为了优化程序性能而对指令进行重新排序的一种手段。

三种：编译器优化重排序，指令优化重排序，内存系统重排序

#### as-if-serial

不论编译器和处理器怎么重排序，单线程执行的结果不能被改变。

#### 双重校验锁为什么要用volatile

Singleton instance = new Singleton(),这句话实际是分三步执行：

1.分配内存空间

2.初始化instance

3.将instance指向分配的内存地址

但是指令重排序可能会改变执行顺序：1>3>2，多线程下会导致得到一个半实例化的对象，使用volatile可以禁止指令重排序保证多线程也能正常运行。

#### synchronize和ReentrantLock区别

reentrantLock是基于AQS实现的可重入锁，在jdk中实现。与synchronized相比：

1.两者都是可重入锁

2.synchronized是基于JVM，而ReentrantLock是在jdk中实现的。

并且synchronized是隐式的获取锁，解锁，ReentrantLock需要显式的去加锁解锁。

3.ReentrantLock增加了一些高级的功能：

​	等待可中断，通过lock.lockInterruptibly()实现这个机制，等待的线程可以去做其他事情。

​	可实现公平锁，synchronized是非公平锁，而ReentrantLock通过构造函数可以指定是公平还是非公平锁。

​	即每次加入新的线程，会尝试自旋获取锁。所以可能后来的线程获取到锁。

#### AQS

队列同步器AbstractQueuedSynchronized,用来构建锁或者其他同步工具的基础框架，内部维护了一个volatile修饰的成员变量state表示同步状态，内置FIFO的双端队列作为线程的等待队列。定义了一些模板方法，完成同步的工作：

```java
public final void acquire(int arg) # 获取独占锁
public final void acquireInterruptibly(int arg) # 获取独占锁，响应中断
public final boolean tryAcquireNanos(int arg, long nanosTimeout) # 指定时长获取独占锁，响应中断
public final boolean release(int arg) # 释放独占锁
    
public final void acquireShared(int arg) # 获取共享锁
public final void acquireSharedInterruptibly(int arg) # 获取共享锁，响应中断
private boolean doAcquireSharedNanos(int arg, long nanosTimeout) # 指定时长获取共享锁
public final boolean releaseShared(int arg)  #释放共享锁
```

通过继承的方式去重写实现其中的如tryAcquire，tryAcquireShared，tryRelease，tryReleaseShared等实现同步工具。

简化了锁的实现方式，隐藏了线程等待和唤醒的操作，可以很灵活的实现自己需要的并发工具。

查看源代码：AbstractQueuedSynchronized

##### ReetrantLock

##### ReentrantReadWriteLock

##### LockSupport

##### Condition

##### LinkedBlockingQueue

##### CopyOnWriteArrayList

##### ConcurrentHashMap

##### CountDownLatch和CycliBarrier

##### Semaphore和Exchanger

#### 

#### 线程池

##### 为什么用线程池

降低资源消耗；提高响应速度；提高线程可管理性。

##### callable和runnable的区别

callable有返回值，抛异常，而Runnable没有，简洁。

##### execute和submit区别

submit返回FurtuTask对象，可以判断线程的状态。

##### 几种线程池

FixedThreadPool：定长线程池。

SingleThreadPool：可以理解为一个定长为1的定长线程池

CachedThreadPool：可动态扩展线程的线程池。

几个重要参数：

corePoolSize：初始化的线程池数量

maxPoolSize：最大线程池数量，当线程数量超过corePoolSize后可以扩到的最大线程数量。

workQueue：当线程数量达到corePoolSize，新线程会加到workQueue队列等待。

handler：线程饱和的策略，默认是AbortPolicy，抛出RejectedExecutionException拒绝。

为啥阿里不建议用Executor去建立？

有弊端：

FixedThreadPool和SingleThreadPool：运行请求的队列为Integer.Max_value，可能接受大量请求，OOM

CacheThreadPool和SchedulerThreadPool：允许创建的队列数量为Integer.Max_value，可能创建大量线程，OOM。

#### ThreadLocal

是什么？

作用是让每个线程保存只有自己访问的私有变量。通过threadLocal的set和get方法赋值和获取。

从而避免线程的安全问题。

实现原理：

每个线程都有ThreadLocalMap类型的变量，类似hashMap，调用threadLocal的set和get方法时，将要线程私有的变量以threadLocal为key,变量为value存到ThreadLocalMap中。

内存泄漏问题：

ThreadLocalMap中的key是ThreadLocal的弱引用（WeakReference），而value是强引用，所以如果ThreadLocal没有被强引用的情况，在垃圾回收的时候，key会被清理，而value不会被清理掉。这样会引起内存泄漏，key手动调用remove方法，清理掉key为null的元素。

#### JVM

##### 引用类型

强软弱虚

强： new Object()，不会被回收

软：内存不足会被回收

弱：下一次gc会被回收

虚：不影响

##### java内存结构

![java内存模型]()

程序计数器：当前线程所执行的字节码的行号指示器，用于记录正在执行的虚拟机字节指令地址，线程私有。

Java虚拟栈：存放基本数据类型、对象的引用、方法出口等，线程私有。

Native方法栈：和虚拟栈相似，只不过它服务于Native方法，线程私有。

Java堆：java内存最大的一块，所有对象实例、数组都存放在java堆，GC回收的地方，线程共享。

方法区（永久区（jdk1.8之前）/元空间（jdk1.8之后））：存放已被加载的类信息、常量、静态变量、即时编译器编译后的代码数据等。回收目标主要是常量池的回收和类型的卸载，各线程共享， 在jdk1.8从划成元空间，与原来最大区别就是使用直接内存。以为元空间存放类加载信息常量池等，大小不好预测，所以使用直接内存。

什么情况会发生栈内存溢出？

栈是线程私有的，他的生命周期与线程相同，每个方法在执行的时候都会创建一个栈帧，用来存储局部变量表，操作数栈，动态链接，方法出口等信息。局部变量表又包含基本数据类型，对象引用类型。

如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError异常，方法递归调用产生这种结果。

##### 分代回收

JVM内存为什么要分成新生代，老年代，方法区。新生代中为什么要分为Eden和Survivor。

- 共享内存区 = 持久带 + 堆
- 方法区
- Java堆 = 老年代 + 新生代
- 新生代 = Eden + S0 + S1

一些参数的配置

- 默认的，新生代 ( Young ) 与老年代 ( Old ) 的比例的值为 1:2 ，可以通过参数 –XX:NewRatio 配置。
- 默认的，Edem : from : to = 8 : 1 : 1 ( 可以通过参数 –XX:SurvivorRatio 来设定)
- Survivor区中的对象被复制次数为15(对应虚拟机参数 -XX:+MaxTenuringThreshold)

为什么要分为Eden和Survivor?为什么要设置两个Survivor区？

- 如果没有Survivor，Eden区每进行一次Minor GC，存活的对象就会被送到老年代。老年代很快被填满，触发Major GC.老年代的内存空间远大于新生代，进行一次Full GC消耗的时间比Minor GC长得多,所以需要分为Eden和Survivor。
- Survivor的存在意义，就是减少被送到老年代的对象，进而减少Full GC的发生，Survivor的预筛选保证，只有经历16次Minor GC还能在新生代中存活的对象，才会被送到老年代。
- 设置两个Survivor区最大的好处就是解决了碎片化，刚刚新建的对象在Eden中，经历一次Minor GC，Eden中的存活对象就会被移动到第一块survivor space S0，Eden被清空；等Eden区再满了，就再触发一次Minor GC，Eden和S0中的存活对象又会被复制送入第二块survivor space S1（这个过程非常重要，因为这种复制算法保证了S1中来自S0和Eden两部分的存活对象占用连续的内存空间，避免了碎片化的发生）

JVM中一次完整的GC流程是怎样的，对象如何晋升到老年代

- Java堆 = 老年代 + 新生代
- 新生代 = Eden + S0 + S1
- 当 Eden 区的空间满了， Java虚拟机会触发一次 Minor GC，以收集新生代的垃圾，存活下来的对象，则会转移到 Survivor区。
- 大对象（需要大量连续内存空间的Java对象，如那种很长的字符串）直接进入老年态；
- 如果对象在Eden出生，并经过第一次Minor GC后仍然存活，并且被Survivor容纳的话，年龄设为1，每熬过一次Minor GC，年龄+1，若年龄超过一定限制（15），则被晋升到老年态。即长期存活的对象进入老年态。
- 老年代满了而无法容纳更多的对象，Minor GC 之后通常就会进行Full GC，Full GC 清理整个内存堆 – 包括年轻代和年老代。
- Major GC 发生在老年代的GC，清理老年区，经常会伴随至少一次Minor GC，比Minor GC慢10倍以上。

##### 垃圾收集算法

标记-清除算法：

首先标记所有不需要回收的对象，标记完后统一回收没有标记的对象。缺点：效率低，产生碎片。

标记-复制算法：

为了解决效率问题，将内存分成两部分，每次使用其中一块，当一块的内存使用完后，就将还存活的对象复制到另一块中去。

缺点：浪费内存

标记-整理算法：

根据老年代提出的算法，因为老年代的对象一般存活比较久。跟“标记-清除算法”相似，先标记，后续将存活的对象向一段移动。然后直接清理掉边界外的内存。

分代收集：

根据新生代，老年代的区别使用不同垃圾回收算法：比如在新生代，死亡率高，可以使用"标记-复制"算法，只需要复制少量对象的成本就可以完成垃圾回收。而老年代的存活率比较高，可以使用“标记整理”算法或者“标记-清除“算法。



你知道哪几种垃圾收集器，各自的优缺点，重点讲下cms和G1，包括原理，流程，优缺点。

1）几种垃圾收集器：

- Serial收集器：单线程的收集器，收集垃圾时，必须stop the world，使用复制算法。
- ParNew收集器： Serial收集器的多线程版本，也需要stop the world，复制算法。
- Parallel Scavenge收集器：新生代收集器，复制算法的收集器，并发的多线程收集器，目标是达到一个可控的吞吐量。如果虚拟机总共运行100分钟，其中垃圾花掉1分钟，吞吐量就是99%。
- Serial Old收集器： 是Serial收集器的老年代版本，单线程收集器，使用标记整理算法。
- Parallel Old收集器：*是Parallel Scavenge收集器的老年代版本，使用多线程，标记-整理算法。
- CMS(Concurrent Mark Sweep) 收集器：是一种以获得最短回收停顿时间为目标的收集器，标记清除算法，运作过程：初始标记，并发标记，重新标记，并发清除，收集结束会产生大量空间碎片。
- G1收集器：标记整理算法实现，运作流程主要包括以下：初始标记，并发标记，最终标记，筛选标记**。不会产生空间碎片，可以精确地控制停顿。

2）CMS收集器和G1收集器的区别：

- CMS收集器是老年代的收集器，可以配合新生代的Serial和ParNew收集器一起使用；
- CMS优点是低停顿，并发收集，缺点是使用"标记-清除算法"，会产生碎片。
- G1收集器收集范围是老年代和新生代，不需要结合其他收集器使用；
- G1收集器可预测垃圾回收的停顿时间
- G1收集器使用的是“标记-整理”算法，进行了空间整合，降低了内存空间碎片。

##### 类加载器和双亲委派机制

类加载器就是根据指定全限定名称将class文件加载到JVM内存，转为Class对象。

- 启动类加载器（Bootstrap ClassLoader）：由C++语言实现（针对HotSpot）,负责将存放在<JAVA_HOME>\lib目录或-Xbootclasspath参数指定的路径中的类库加载到内存中。
- 其他类加载器：由Java语言实现，继承自抽象类ClassLoader。如：

> - 扩展类加载器（Extension ClassLoader）：负责加载<JAVA_HOME>\lib\ext目录或java.ext.dirs系统变量指定的路径中的所有类库，在jdk 9之后被模块化所替代。
> - 应用程序类加载器（Application ClassLoader）。负责加载用户类路径（classpath）上的指定类库，我们可以直接使用这个类加载器。一般情况，如果我们没有自定义类加载器默认就是用这个加载器。

双亲委派模型工作过程是：

> 如果一个类加载器收到类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器完成。每个类加载器都是如此，只有当父加载器在自己的搜索范围内找不到指定的类时（即ClassNotFoundException），子加载器才会尝试自己去加载。

为什么需要双亲委派模型？

在这里，先想一下，如果没有双亲委派，那么用户是不是可以自己定义一个java.lang.Object的同名类，java.lang.String的同名类，并把它放到ClassPath中,那么类之间的比较结果及类的唯一性将无法保证，因此，双亲委派可以保证基础类型的一致性。防止内存中出现多份同样的字节码

怎么打破双亲委派模型？

打破双亲委派机制则不仅要继承ClassLoader类，还要重写loadClass和findClass方法。

##### 类加载过程

加载：将class文件加载到内存，生成运行时数据：常量池，静态变量等。

验证：验证安全性，是否符合JVM规划范。

准备：为静态变量分配内存并设置初始值。

解析：常量池内的符号引用替换为直接引用。

初始化：执行构造方法进行初始化。





7.几种主要的JVM参数

**-Xmx3550m：** 最大堆大小为3550m。

**-Xms3550m：** 设置初始堆大小为3550m。

**-Xmn2g：** 设置年轻代大小为2g。

**-Xss128k：** 每个线程的堆栈大小为128k。

**-XX:MaxPermSize：** 设置持久代大小为16m

**-XX:NewRatio=4:** 设置年轻代（包括Eden和两个Survivor区）与年老代的比值（除去持久代）。

**-XX:SurvivorRatio=4：** 设置年轻代中Eden区与Survivor区的大小比值。设置为4，则两个Survivor区与一个Eden区的比值为2:4，一个Survivor区占整个年轻代的1/6

**-XX:MaxTenuringThreshold=0：** 设置垃圾最大年龄。如果设置为0的话，则年轻代对象不经过Survivor区，直接进入年老代。

**-XX:+UseParallelGC：** 选择垃圾收集器为并行收集器。

**-XX:ParallelGCThreads=20：** 配置并行收集器的线程数

**-XX:+UseConcMarkSweepGC：** 设置年老代为并发收集。

**-XX:CMSFullGCsBeforeCompaction**：由于并发收集器不对内存空间进行压缩、整理，所以运行一段时间以后会产生“碎片”，使得运行效率降低。此值设置运行多少次GC以后对内存空间进行压缩、整理。

**-XX:+UseCMSCompactAtFullCollection：** 打开对年老代的压缩。可能会影响性能，但是可以消除碎片

-XX:+PrintGC :

-XX:+PrintGCDetails :

输出线程栈信息：

- 输入jps，获得进程号。
- top -Hp pid 获取本进程中所有线程的CPU耗时性能
- jstack pid命令查看当前java进程的堆栈状态
- 或者 jstack -l > /tmp/output.txt 把堆栈信息打到一个txt文件。
- 可以使用fastthread 堆栈定位

8.强引用、软引用、弱引用、虚引用的区别？

1）强引用

我们平时new了一个对象就是强引用，例如 Object obj = new Object();即使在内存不足的情况下，JVM宁愿抛出OutOfMemory错误也不会回收这种对象。

2）软引用

如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。

```java
SoftReference<String> softRef=new SoftReference<String>(str);     // 软引用
```

**用处：** 软引用在实际中有重要的应用，例如浏览器的后退按钮。按后退时，这个后退时显示的网页内容是重新进行请求还是从缓存中取出呢？这就要看具体的实现策略了。

（1）如果一个网页在浏览结束时就进行内容的回收，则按后退查看前面浏览过的页面时，需要重新构建

（2）如果将浏览过的网页存储到内存中会造成内存的大量浪费，甚至会造成内存溢出

如下代码：

```java
Browser prev = new Browser();               // 获取页面进行浏览
SoftReference sr = new SoftReference(prev); // 浏览完毕后置为软引用        
if(sr.get()!=null){ 
    rev = (Browser) sr.get();           // 还没有被回收器回收，直接获取
}else{
    prev = new Browser();               // 由于内存吃紧，所以对软引用的对象回收了
    sr = new SoftReference(prev);       // 重新构建
}
```

3）弱引用

具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。

```java
String str=new String("abc");    
WeakReference<String> abcWeakRef = new WeakReference<String>(str);
str=null;
等价于
str = null;
System.gc();
```

4）虚引用

如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。虚引用主要用来跟踪对象被垃圾回收器回收的活动。

# 大数据

## 1. linux

### 1. tcp协议，三次握手，四次挥手，timewait

## 2 . kafka

#### ZK的作用

kafka使用zk保存集群的元数据和消费者信息

#### ack参数

acks:0(生产者发送数据，不等待服务器响应)，1（只要集群的leader收到消息，生产者就收到成功响应），all(当所有参与复制的节点收到，才算成功)

#### 压缩方式

compresion.type指定压缩算法：snappy(cpu消耗低，压缩比良好)，gzip(cpu消耗高，压缩比优秀)

#### 如何保证有序性

max.in.flight.requests.per.connection:指定在收到服务器响应之前可以发送多少消息，值越高，内存也多，吞吐量越大。kafka可以保证同一个分区的消息是有序的，设置为1可以保证消息是按照顺序写入服务器的，适用于一些对消息顺序有要求的场景。

#### 常用序列化方式

String,Avro, protobuf,json，对应的消费者需要定义反序列化器

#### 主题，消费者，分区和副本

一个主题的所有分区平均分配到每个消费者组的消费者，如果消费者>分区数就会有部分消费者闲置。如果有多个消费组，主题的消息会被多个消费组消费。

kafka使用topic组织数据，每个topic可以包含多个分区，每个分区有多个副本，副本保存在broker上。两种副本：
		首领副本：每个分区有多个副本，其中一个是首领副本，负责接受生产证和消费者的请求。跟随者副本：首领以外的是跟随者副本，不处理客户端请求，唯一任务是从首领副本复制消息，保持与首领副本一致状态，如果首领死掉，选举一个跟随者为leader。
		除了当前首领，每个分区都有个首选首领，创建topics选定的首领就是首选首领，默认auto.leader.rebalance.enable是true,检查，如果首选首领不是当前首领，并且是副本同步的，就成为当前首领。

kafka的基本存储单元是分区。假设6个broker,创建了10分区，副本数为3，那么副本数30，平均每个broker保存6副本，并且同一条信息的副本在不同的broker上，也尽可能在不同机架（如果分配了机架信息）。

为了快速定为到指定的偏移量，	kafka为每个分区维护了一个索引，索引把偏移量映射到片段文件和偏移量在文件的位置。

#### reblance

再均衡(reblance),当有消费者加入或退出，分区新增或删除时，分区的所有权从一个消费者转移到另一个消费者。再均衡期间会造成整个群组不可用。消费者通过向被指派为群组协调器的broker发送心跳来维持群组关系以及分区的所有权。当消费者加入群组时，会想群组协调器发送第一个joingrou请求，第一个加入的成为群主。

#### 几种类型请求

​			元数据请求，获取指定topic信息，包含分区，每个分区的副本信息等。
​			获取请求：kafka通过zero-copy向客户端发送消息（直接从文件系统发送到网络通道，不需要经过中间任何缓冲区）
​			生产请求：写数据，ack参数

#### 如何保证数据不丢失

可以从三方面保证

​	生产者发送不丢数据： 配置ack，0，1，all

​	broker保证数据不丢失：采用分片副本机制，保证数据高可用。

​	consumer保证数据不丢失：默认offset是自动提交，可以改成手动提交，当数据写入数据库或者落地磁盘后再手动提交offset。

#### ISR

leader维护一个与之基本保持同步的副本列表。叫ISR(in-sync Replica)，每个partition都有一个ISR。

由leader动态维护

如果副本跟leader差太多会从ISR移除

leader接受消息当ISR的所有副本都向leader发送ACK时，leader才commit.

#### 生产，存储，消费过程

写流程：

1. producer 生产数据发送到指定partition，通过zk节点找到partition的leader

2. 发送给leader,leader 将消息写到本地log中

3. ISR向leader同步。

4. 当所有副本都向leader发送ack时，leader才commit。

消费数据流程：

以消费者组的方式工作，每个消费者组有多个消费者，一个分区里的消息只能由消费者组里的一个消费者消费，但可以让多个消费者组消费（订阅）。

两种消费方式：poll和push

我们solea项目采用poll模式，tika作为消费者不停的轮询kafka消息。

存储：

物理上吧topic分成多个partition,每个partition对应一个文件夹，文件夹内存储partition的所有消息和索引文件。

#### kafka为什么这么快

1.利用partition并行处理

一个topic可以分为不同partition，位于不同机器，利用集群的优势，并行处理。可以将partition分配给多个消费者并行消费。

2.顺序写

每个partition是有序的，不可变的消息序列，新的消息会加到partition的末尾

3.零拷贝技术

直接把数据从内核态拷到网络传输层。避免了用户态和内核态这些之间的切换。

用户态：每个应用程序启动运行时处于用户运行状态

内核态：当程序要读写文件时，一般要切换到内核态系统调用。

#### zero copy

TODO



## 3.  zookeeper

#### TODO

## 4 . redis

#### 几种数据类型

​			五种：
​				1.string： 可以存储2种类型的值：字符串，整数和浮点数
​				2.list
​				3.hash
​				4.set
​				5.zset：有序集合的值称为分值（ score ）, 必须是浮点类型。有序集合是redis里唯一既能根据成员访问元素，又可以根据分值以及分值的排序顺序访问元素的结构

#### 常用的命令

​			五种数据类型的常用命令，PUBLISH和SUBSCRIBE,SORT,两个事务命令：MULTI,EXEC。以及过期键清理的命令

#### 压缩列表

​			为了解决内存问题开发，在list，hash和zset的长度较短或体积较小时，redis可以选择压缩列表紧凑存储方式来存储这些结构，它是这三种不同数据类型的对象的一种非结构化表示，以序列化的方式存储数据。每次读取和写入都要解码和编码，并且可能对内存里的数据进行移动。
​			当元素超过一定大小时， 紧凑型存储方式会转为普通的方式，因为体积过大的紧凑编码操作也会消耗性能。
​			zlbytes(总长字节)，zltail(表示尾节点的位置)，zlen(包含节点个数)， entry(节点，多个相邻)， zlend(结束符)
每个节点由三部分组成：previous_entry_length(前节点的长度)，encoding(编码方式)，content(内容)

#### 整数集合

​			跟list, hash, zset一样，体积较小的set也有自己的紧凑表示：

​	   	 1.如果所有整数都可以被解释成十进制整数并且处于平台有符号整数范围内并且数量足够少，会以有序整数数组的方式存储集合
​			2.整数集合是集合键的底层实现之一
​			3.底层实现为数组，在有需要时会自动将整数类型升级为高位的，如int32_t升级为int64_t,这样可以灵活扩展，并且解决内存。不支持降级操作。

#### redis里几种数据接口

SDS,链表,字典,跳跃表。

链表的用途：
			1.redis中的链表是双端链表，每个节点都有next和prev指针。
			2.无环链表，表头的prev和表尾的next都是null
			3.广泛用于列表键，发布和订阅等

字典的用途：
			1.用于redis的数据库和哈希键等。
			2.底层用hash表实现，每个字典表包括两个哈希表，一个正常用另一个在rehash使用
			3.键冲突时，通过单向链表解决

跳表：
			redis使用跳表作为有序集合键的底层实现之一

#### SDS

简单动态字符串（SDS）,redis的默认字符串表示，还被用来做缓冲区：AOF模块的缓冲区和客户端状态的输入缓冲区等。
			sds和c字符串区别：
				1 sds由3属性组成，len(记录已使用字符串长度)，free(记录剩余长度)，char[]保存字符串，跟c字符串一样以空字符“\0”结尾
					获取长度属性快， 不用跟c字符串一样以遍历的方法累加。
				2 拼接字符串可以快速根据字符串长度分配足够内存，避免了缓冲区溢出。因为操作SDS时会检查长度是否够，不够就自动扩展。
				3 对SDS进行拼接或截断时，快速根据SDS长度属性预分配空间，减少连续执行字符串增长所需的内存分配次数，惰性释放，减少字符串时，空间不会立马释放，可以给下次使用，有api调用可以释放。
				4 可以存储文本和二进制，一般的字符串只能存文本。

#### redis对象

对象
			1.redis中每个键和值都是一个对象
			2.有list，string，hash,set,zset五种类型的对象，每种都有两个以上的编码方式。不同编码在不同场景优化效率
			3.对象系统带有引用计数实现的回收机制，当一个对象不再使用就会被回收。
			4.reids会共享0到9999的字符串对象。
			5.对象中属性记录最后一次的访问时间，用来计算对象的空转时间。

#### 数据库

​			redis默认创建16个数据库，由两个字典组成：dict和expire字典
​			1.EXPIRE或者PEXPIRE设置过期时间，秒或者毫秒。EXPIREAT或者EXPIREAT设置过期日期，unix时间戳，
​			TTL和PTTL返回能生存时间
​			2.expire字典保存数据库所有键的过期时间，叫过期字典，里面的键是一个指针，指向某个键对象，值是过期时间的unix时间戳

#### 过期键删除策略

​				定时删除，对cpu不友好
​				定期删除，每次从一定量的数据库取一定量的数据来判断清理
​				惰性删除：每次用某个键的时候，判断是否过期， 对内存不友好
​				redis实际使用定期和惰性删除两种策略，结合使用
​				RDB, save和bgsave时判断如果是过期键就不写了， 从文件载入时，过期也不载入
​				AOF,执行重写时判断键是否过期

#### 缓存淘汰策略

1.先进先出算法（FIFO）

2.最近使⽤最少Least Frequently Used（LFU）

3.最近最少使用Least Recently Used（LRU）

当存在热点数据时，LRU的效率很好，但偶发性的、周期性的批量操作会导致LRU命中率急剧下降，缓

存污染情况⽐较严重

#### RDB

​				用于保存和还原数据库中的所有键值对，redis默认开启的保存方法。
​				save执行由服务器直接保存rdb文件，效率高，bgsave生成子进程异步保存
​				rdb是一个压缩的二进制文件，由多个部分组成：redis, db_version, databases, EOF, check_sum、
​			  优点：
​				1.对性能影响最小，比如设置命令：save 60 100(在60s内进行了100次修改则触发RDB快照)，进行快照RDB时，会fork出一个子进程进行执行（但fork子进程的过程可能影响效率），不影响redis处理客户端效率。
​				2.RDB比AOF恢复快很多，因为AOF是重新执行所有的写操作。
​			  缺点：
​				1.快照是定期生成的，所以redis崩溃时可能丢失数据
​				2.如果数据集很大并且cpu性能不好，fork子进程的过程会影响服务端处理效率。

#### AOF

​			1.通过保存所有对数据库的写命令（set,add,del）来记录和还原数据库的状态
​			2.AOF重写，为了缩小AOF文件体积，服务器执行BGREWRITEAOF产生子进程在后台根据当前数据库的数据生成新的AOF文件，
​				这段时间服务器的重写命令添加到AOF_buffer中，子程序重写完成后，再追加到新的AOF文件。
​			3.通过配置开启：appendonly yes

			4.	appendfsync no:不进行fsync,flush文件的时机交给os决定，速度最快
				appendfsync always:每次写一条命令都写到文件中，速度最慢但安全性高
				appendfsync eversec: 折中的做法，交给后台线程没秒flush一次。
			  优点：
				相对比较安全，当配置appendfsync always时最安全，不丢数据，appendfsync eversec也可以保证最多只丢一秒的数据。
				AOF文件在发送断电等问题不会损坏，及时出现某条日志写一半的情况，也能通过redis-check-aof工具修复
			  缺点：
				1.aof文件一般比rdb文件体积大
				2.性能消耗比rdb高，恢复慢。

#### 事件

​			1.文件事件和时间事件两个类型
​			2.文件事件基于Reactor的IO多路复用模式
​			3.文件事件分为两类：AE_READABLE(读取)和AE_WRITEABLE(写)事件
​			4.基于Reactor的IO多路复用模型，select/poll/epoll

#### 复制

​			1.通过slaveof命令复制，部分重同步通过复制偏移量，复制积压缓存区，服务器运行id来实现复制

​			2.复制开始时，从服务器会成为主服务器的客户端，通过向主服务器发送命令执行复制步骤，后期主从服务器互相成为客户端，
​			3.主服务器向从服务器传播命令更新从服务器状态，从服务器向主服务器发送命令进行心跳检测和命令丢失检测。
​			4.从2.8开始PSYNC代替SYNC执行复制时的同步操作：
​				两种模式，完整重同步：用于初次复制，服务器生成RDB文件，发送给从服务器，直接复制新的RDB文件删除旧数据。之后是部分重同步：主服务器向从服务器发送写命令，从服务器执行。

#### 哨兵（sentinel）

​			1.是一个特殊模式（使用了和普通模式不同的命令表，可执行功能不同）下的redis服务器，跟普通的redis服务器使用不同的
​			2.会读取用户指定配置文件，为每个要被监视的服务器创建实例，并创建连接主服务器的命令链接和订阅链接，其中命令链接用于发送命令请求，而订阅链接用于接受指定频道的消息
​			3.通过向主服务器发送INFO命令获取从服务器的信息，并且创建实例，创建发送和订阅链接。
​			4.默认10秒向主，从服务器发送INFO命令，当主服务器下线，多个哨兵选举出leader进行故障转移:从服务器中选一个作主服务器（SLAVEOF no one），标记已下线主服务器为从，检测到上线后变成从服务器。

#### 集群

​			节点通过向其他节点发送MEET命令然后进行握手加入自己所处集群
​			集群16384个槽分配给节点，每个节点会记录自己管哪些槽，也会记录每个槽属于哪个节点
​			重新分片指某个槽的所有键值对从一个节点转移到另一个节点
​			集群通过发送和接受消息来通信：MEET,PING,PONG,PUBLISH,FAIL等

#### 发布订阅

​			服务器在pubsub_channels字典保存所有频道的订阅信息，SUBSCRIBE命令复制订阅，UNSUBSCRIBE取消订阅
​			服务器在pubsbu_patterns链表保存所有模式的订阅信息，如：redis-client1: music*

#### 事务

​			通过命令MULTI开始事务，后面执行的所有操作（除了MULTL,EXEC,DISCARD,WATCH四个）都被加到FIFO队列中，执行EXEC执行队列中的命令，
​			执行WATCH命令会将key和监视的客户端存到watched_keys字典中，key是监视的键，value是一个链表，保存监视的客户端。如果在事务期间，监视的任意一个键对应的对象被修改，则监视这个键的客户端会打开REDIS_DIRTY_CAS标识，服务器会拒绝客户端提交的事务。

#### 缓存雪崩

同⼀时刻⼤量缓存失效；

处理⽅法：

（1）：缓存数据增加过期标记

（2）：设置不同的缓存失效时间

（3）：双层缓存策略C1为短期，C2为⻓期

（4）：定时更新策略

#### 缓存穿透

频繁请求查询系统中不存在的数据导致；

处理⽅法：

（1）：cache null策略，查询反馈结果为null仍然缓存这个null结果，设置不超过5分钟过期时间

（2）：布隆过滤器，所有可能存在的数据映射到⾜够⼤的bitmap中 google布隆过滤器：基于内存，重

启失效不⽀持⼤数据量，⽆法在分布式场景 redis布隆过滤器：可扩展性，不存在重启失效问题，需要

⽹络io，性能低于google

#### Redis如何做持久化

bgsave做镜像全量持久化，aof做增量持久化。因为bgsave会耗费较⻓时间，不够实时，在停机的时候

会导致⼤量丢失数据 ，所以需要aof来配合使⽤。在redis实例重启时，会使⽤bgsave持久化⽂件重新构

建内存，再使⽤aof重放近期的操作指令来 实 现完整恢复重启之前的状态。

#### 在项目中的使用（两种以上）

TODO

​		用来文件去重：搭建redis集群，发送文件，获取文件的md5先到redis中查询是否存在，如果存在就提示文件重复，不存在加到redis中。
  		保存策略：根据现场离线上传的数据量配置内存，例如：配置：maxmemory 4gb，配置4gb，3个节点实际占用4*3 gb。约可以保存5000w文件的去重。
   	   当达到内存80%后，配置：maxmemory-policy allkeys-lru，删除最少使用的key。

## 5 .solr

solr是一个基于lucene的全文搜索服务器，基于倒排索引对非结构化文件的文本内容进行分词和建立索引，然后可以达到快速检索的目的。

建索引过程：

去除去停词和标点符号

转小写

分词和创建倒排表

搜索索引：

对关键词进行分词切割

对分词结果建立索引

根据索引找到文档id

根据id找到文档具体内容，然后通过权重分析，排序返回。



跟es区别：

都是基于lucene（全文检索引擎工具包），的搜索服务器。

solr适合对已有的数据做索引和检索，es更加适合实时索引的场景。

因为公司业务的性质，数据量大，但是查询少。



查询类型：

q：条件查询

fq：过滤查询

wt指定返回的格式，csv,json?

fl：要返回的字段

max_doc=false

collection，shard（由一个或多个副本组成）, core



几种域类型：

是否索引

是否存储

是否docValue

是否多值

#### TODO

## 6 hadoop

hadoop 是一款开源的分布式计算框架，可以使用简单的编程模型跨计算机集群进行分布式计算处理海量数据，通常来说是一个更广泛的概念-hadoop生态圈，包括：hdfs，mapreduce，yarn等组件

### 	hdfs

#### 组件架构

namenode：1.管理元数据的信息（命名空间，块信息等） 2.维护目录树 3.接受客户端请求
secondaryNamenode：帮namenode定时合并editlog到fsimage文件，加快namenode重启效率。
datanode：负责具体的数据存储

#### fsimage和editlog是什么

fsimage： namenode重启时整个文件系统的快照
editlog： 保存对文件系统修改的记录
		namenode重启时会将editlog的日志合并到fsimage时，关键namenode很少重启，时间长了editlog会很大，导致namenode重启缓慢。
		secondaryNamenode可以定时的合并editlog到fsimage中然后替换成新的fsimage减少namenode的重启时间。

#### 读写流程

​    写流程：

![1582122846737](file://E:/wt/hz/data/%E5%A4%8D%E4%B9%A0%E8%B5%84%E6%96%99/%E5%B2%97%E4%BD%8D%E6%8C%87%E5%AF%BC%E8%AF%BE%E7%A8%8B/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%BC%80%E5%8F%91/assert/1582122846737.png?lastModify=1617070999)

​		1.客户端通过Distributed FileSystem向namenode发起写数据请求
​		2.namenode检查目标文件是否存在还有目录是否存在，响应是否可以上传文件
​		3.客户端对文件进行分块，然后请求namenode：block上传到哪些datanode上面
​		4.namenode返回要上传到哪些datanode上
​		5.客户端通过FSDataOutputStream模块请求datanode建立通道
​		6.开始上传，第一个block上传完成接着后面的block上传
​	  读流程：

![1582123099100](file://E:/wt/hz/data/%E5%A4%8D%E4%B9%A0%E8%B5%84%E6%96%99/%E5%B2%97%E4%BD%8D%E6%8C%87%E5%AF%BC%E8%AF%BE%E7%A8%8B/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%BC%80%E5%8F%91/assert/1582123099100.png?lastModify=1617070999)

​		1.客户端通过Distributed FileSystem向namenode请求下载目标文件
​		2.namenode查询元数据，找到文件有哪些块，每个块在哪个datanode上，发给客户端
​		3.客户端挑选一台datanode(就近原则)服务器，请求读取数据
​		4.客户端以packet(64k)为单位，现在本地缓存然后写入目标文件。

#### 动态扩容缩容

​        动态扩容：
​			1.在新机器上传解压hadoop安装包，配置都scp过来
​			2..dfs.hosts增加新节点
​			3.新的机器启动：hadoop-daemon.sh start datanode
​			4.新的机器启动：yarn-daemon.sh start nodemanager
​		动态缩容：
​			1.配置dfs.hosts.exclude，配置要减的节点
​			2.刷新：
​				hdfs dfsadmin -refreshNodes
​				yarn rmadmin –refreshNodes

#### 安全模式

​			是hdfs的一种特殊模式,这种状态下，文件系统只接受读数据的请求，而不接受删除，修改等请求，可保护集群中数据块的安全性。
​			在namenode主节点启动时hdfs会进入安全模式，datanode就绪后整个系统达到安全标准，退出安全模式。可手动进入和离开：hdfs dfsadmin -safemode enter
​			hdfs dfsadmin -safemode leave

#### hdfs优化



#### 如何搭建高可用hadoop集群

hdfs的namenode 存在单点故障，可以通过zk启动多个namenode实现热备份，解决这个问题。

### 	 mapreduce	

#### 组件架构

​			client：每个job通过client将程序和配置打包成jar提交到hdfs,然后把路径告诉jobTracker的master,jobTracker划分出task(mapTask和reduceTask),分配到taskTracker去执行。
​			jobTracker：负责资源的监控和任务的调度。监控所有的taskTracker和job的健康状况，如果有失败的分配到其他节点执行。
​			taskTracker：汇报和分配资源。通过heartbeat定时向job汇报资源和task运行状况，以slot为单位划分本节点的资源划分给每个task,执行jobTracker发布的任务：执行任务，杀掉任务等
​			task：分两种：mapTask, reduceTask

#### 执行流程

能画图：TODO

![1582129217021](file://E:/wt/hz/data/%E5%A4%8D%E4%B9%A0%E8%B5%84%E6%96%99/%E5%B2%97%E4%BD%8D%E6%8C%87%E5%AF%BC%E8%AF%BE%E7%A8%8B/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%BC%80%E5%8F%91/assert/1582129217021.png?lastModify=1617070999)

map阶段
				inputFormat读取文件（默认TextInputFormat）,getSplits方法对文件切分，一个split生成一个mapTask,一般一个block对应一个	split。
				切分为splits后由RecordReader对象（默认LineRecordReader）读取数据。
				执行map方法
shuffle阶段（map输出之后，reduce输入之前）
				由outputCollect收集map的输入，将数据写到环形缓冲区（利用内存，减少io），在缓冲区中对数据分区，排序，溢写。缓冲区	默认100MB，默认达到0.8时会起子线程进行溢写到本地。
				shuffle阶段会有3次sort：第一次在环形缓冲区内排序，使用快排。第二次溢写到本地后，对小文件合并作了一次归并排序。第	三次是多个文件合并分发给reduce时，也会排序，保证文件整体是有序的，也是归并排序。
				分区默认hashPartition方法，按照hash取模。
				合并，溢写完会对溢写的小文件根据分区进行合并（分区内有序）。

![1582129765528](file://E:/wt/hz/data/%E5%A4%8D%E4%B9%A0%E8%B5%84%E6%96%99/%E5%B2%97%E4%BD%8D%E6%8C%87%E5%AF%BC%E8%AF%BE%E7%A8%8B/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%BC%80%E5%8F%91/assert/1582129765528.png?lastModify=1617070999)

combiner：在map端执行reduce,默认是没有的，如果小的文件比较多可以开启进行优化。对每个map任务的输出做一次reduce
	reduce阶段
				拉取map端的输出，分区个数对应reduce数量，reduce拉去对应分区的数据。
				调用reduce方法，写到hdfs中。

#### mapreduce参数优化

​			1.mapreduce.map.memory.mb 设置mapTask的内存
​			2.mapreduce.reduce.memory.mb 设置reduceTask的内存
​			3.mapreduce.task.io.sort.mb 设置环形缓冲区内存大小
​			4.mapreduce.map.sort.spill.percent 环形缓冲区溢出的阈值，缺省0.8
​			5.mapreduce.map.maxattempts mapTask失败重试次数
​			6.mapreduce.reduce.maxattempts reduceTask失败重试次数
​			7.mapreduce.map.failures.maxpercent mapTask容错比例
​			8.mapreduce.reduce.failures.maxpercent reduceTask容错比例
​			9.apreduce.map.cpu.vcores 每个mapTask最多可用core个数，缺省1
​			10.mapreduce.reduce.cpu.vcores reduceTask最多可用core个数，缺省1
​			11.数据倾斜：TODO



### 	 yarn

是一个资源管理，任务调度的框架，主要包含三大模块：resourceManager, applicationMaster, nodemanager
		

#### 组件架构

![1582169222018](file://E:/wt/hz/data/%E5%A4%8D%E4%B9%A0%E8%B5%84%E6%96%99/%E5%B2%97%E4%BD%8D%E6%8C%87%E5%AF%BC%E8%AF%BE%E7%A8%8B/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%BC%80%E5%8F%91/assert/1582169222018.png?lastModify=1617070999)

​			ResourceManager：负责整个集群资源的管理和分配
​			NodeManager：管理节点的资源和任务，定时向resourceManager汇报资源（主要是cpu,内存）状况，处理来自applicationMaster的启动，停止container的请求等。
​			AppMaster：与RM申请资源（container）,监控任务运行状态，如果失败申请资源重试。

#### 执行流程

![1582125526987](file://E:/wt/hz/data/%E5%A4%8D%E4%B9%A0%E8%B5%84%E6%96%99/%E5%B2%97%E4%BD%8D%E6%8C%87%E5%AF%BC%E8%AF%BE%E7%A8%8B/%E5%A4%A7%E6%95%B0%E6%8D%AE%E5%BC%80%E5%8F%91/assert/1582125526987.png?lastModify=1617070999)

​			1.客户端提交任务到ResourceManager中的appManager。
​			2.ResourceManager向nodemanager申请container启动appMaster用于任务的划分和监控。
​			3.appMaster和appManager心跳汇报状况
​			4.appMaster向resourceManager中的resourceScheduler申请任务需要的资源
​			5.申请到资源后，与nodemanager通信启动对应的container来运行mapTask和reduceTask
​			6.各个task向appMaster汇报进度和执行情况（以便在某任务失败后重试或者转移到其他节点执行），appMaster向appManager汇报
​			7.执行完毕后，appMaster向appManager汇报，注销appMaster回收资源。

#### 三种调度器

​			FIFO,Capacity,Fair
​			FIFO：
​				提交的任务加入队列,这是个FIFO队列，先给提交的任务分配资源，待前者资源满足后有剩余再分配给后面的。
​				如果任务较大容易造成阻塞
​			Capacity:
​				通过多个队列实现，可以为每个租户分配好队列，再为每个队列分配好集群资源，这样集群可以通过多队列方式提供服务。队列内部是FIFO
​				hadoop 2从2.x默认是Capacity
​			Fair:
​				根据运行的job,平均分配资源。如果有job进入或退出也会重新分配资源。
​				效率高但资源消耗大。
​				CDH默认是Fair。

### 	hive

是hadoop的一个数据仓库工具，可以将结构化文件映射成一张数据库表，并且提供类sql的查询功能，本质是讲sql解析成mapreduce任务执行。一般用来做离线任务分析。

#### 组件架构

client: 写sql,提交命令
driver: 包括sql parser(解析器)，query optimizer(优化器)，physical plan(物理执行计划，编译器)，转换成mapreduce任务执行
meta store：存储元数据（表名，字段类型，表数据所在目录等）
基于HDFS/Hbase存储

#### 数据模型

​			db：配置的hive.metastore.warehouse.dir在hdfs下的一个目录
​			table: 表现是db目录下的一个子目录
​			external table: hdfs指定的路径
​			partition: table下的子目录
​			bucket: 表现是hdfs目录下同一个表目录下更具hash散列后的多个文件

#### 内部表跟外部表区别

​			1.外部表创建时指定external字段：create external tabl if not exists table_a;
​			2.外部表的数据不由hive管理，hive只管元数据（表结构），删除表，数据还在。内部表删除都没了。
​			3.一般临时表用内部表，方便清理。

#### 常用的UDF函数和自定义UDF,UDAF,UDTF

实现UDF:

1.集成hive.ql.UDF类

2.实现evaluate方法，支持重载

3.添加jar包，add jar xx.jar

4.创建临时函数：create temporary function getLen as 'com.test.hive.GetLen';

实现UDAF:

输入多行，输出单行，如max(), count(),sum(),min()这些。

1.继承类：hive.ql.exec.UDAF

2.重写几个方法：init,iterator,merge这些

实现UDTF:

可以把一行拆成多行。

1.继承类hive.ql.udf.generic.GenericUDTF

2.重写方法：initialize，process,close方法

#### 常用的一些优化

​			1.配置hive.fetch.task.conversion默认是more,老版本是minimal，参数是more时，在全局查找，字段查找，limit查找都不走mapreduce
​			2.小数据的情况可以直接在本地执行，否则job初始化时间反而会长，配置参数：hive.exec.mode.local.auto=true,同时配置参数hive.exec.mode.local.auto.input.files.max（local mr最大输入文件个数），hive.exec.mode.local.auto.inputbytes.max（local mr最大输入数据量）
​			3.分区表分桶表
​			分区表对sql过滤查询是一种优化
​			分桶表：TODO
​			4.join优化
​				小表join大表，会在map端join(新版本自动优化了，以小表放在join左边)
​			5.使用分桶表，进行抽样和join时可以提高mr的效率：创建分桶表，在导入数据之前设置：hive.enforce.bucketing=true,控制reduce的个数和分桶的个数适配。
​			6.cluster by = distribute by + sort by
​			7.分区表和分桶表优点
​				分区表可以筛选查询条件，提供查询效率
​				分桶表：
​					1.对数据抽样查询可以指定抽样哪些，哪几个桶的数据：select * from table tablesample(bucket 2 of 4 on id),取2个桶的数据抽样
​					2.如果两个分桶表进行join并且分桶个数相同或者倍数，采用map join的方式避免了全表的笛卡尔积。
​			8.order by 和sort by 
​				order by是全局排序，在reduce端做，如果hive设置了mapred.reduce.tasks=4在orderby时也会重置为1，因此会影响效率，数据大的时候要加上limit限制
​				sort by 在每个reduce中进行排序，可以和distribute by 结合使用 distribute by + sort by = cluster by 保证输出的分区内结果有序
​			9.map join 和reduce join
​				map join: 小表加载到map端的内存中（map集合或其他），直接条件判断
​				reduce join: 两个表的数据都进行map过程，map方法中标记每条数据来自哪个表。在reduce的时候根据标记判断数据是哪个表，然后进行条件判断，统计等。
​			10.开启map端聚合：
​				set hive.map.aggr = true;
​				set hive.groupby.mapaggr.checkinterval = 100000; 支持聚合操作的条数
​			11.数据倾斜：
​				页面上看到任务卡在99%，发现有少量reduce任务还未完成。
​				1.开启参数，map端join：
​					set hive.map.aggr = true;
​					set hive.groupby.mapaggr.checkinterval = 100000; 支持聚合操作的条数
​				2.
​				set hive.groupby.skewindata = true; 有数据倾斜时开启负载均衡，会生成两个MR job，第一次map结果随机分发到reduce中做聚合，第二次基于第一次预处理结果再做一次聚合，按照group by key 分配到reduce,可以保证相同的grup by key分发到同一个reduce
​				常用的一些函数和sql: TODO
​			12.map数量和reduce数量
​				map数：默认一个block 128M一个mapTask,如果记录太多可以选择拆分：set mapreduce.job.reduces=10;create table table_new  as select * from table_src distribute by rand(123),这样可以用table_new查询，会用10map去完成。
​				reduce数：默认一个reduce数据大小256MB,每个任务最大reduce数：1009。可以设置reduce数：
​			13.jvm重用
​				设置mapred.job.reuse.jvm.num.tasks=10;设置jvm重用的次数。可以避免小文件或者task很多的场景,节省了jvm启动和停止的时间。
​			14.并行执行
​				当一个sql中有多个job并且不互相依赖，可以让顺序并行执行，一般是union all:
​					set hive.exec.parallel=true; # 开启任务并行执行
​					set hive.exec.parallel.thread.number=8;# 一个sql运行最大并发线程
​			15.合并小文件
​				set hive.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat; # map输入前合并小文件
​				set hive.merge.mapfiles = true；# 在map端输出进行合并，默认true
​				set hive.merge.size.per.task = 256*1000*1000; # 设置合并文件的大小

### 	 hbase

是一个建立在hdfs上，高可靠性，列存储，可伸缩，实时读写的nosql数据库。特点是大：一个表可以有上亿行，上百万列。面向列：面向列的存储和独立检索。

#### 组件架构

HMaster：1.分配region 2.监控RegionServer 3.处理RegionServer的故障转移 4.处理元数据变更 5.在空闲时间进行数据的负载均衡 6.通过zk发布自己的位置给客户端
RegionServer： 1.负责存储hbase数据 2.处理分配到的region 3.刷新缓存到hdfs 4.维护HLog 5.处理region分片

#### 读写流程

​		  写流程
​			1.client访问zk,获取meta表元数据，确定数据要写入的region和regionServer
​			2.client向regionServer发起写请求，regionServer响应请求
​			3.client先把数据写入HLog,防止数据丢失，然后写入Memstore
​			4.如果HLog和Memstore都写入成功，则这条数据就写入成功
​			5.当Memstore达到阈值(默认128MB)，flush到storefile中
​			6.当storefile越来越多，触发compact合并操作，合并成一个大的storefile
​			7.当storefile足够大时(默认10GB)，触发split,将region一分为二。
​		  读流程
​			1.RegionServer保存meta表以及表数据，要访问表数据，client先访问zookeeper,获取meta表在哪个regionServer上保存。
​			2.访问这个regionServer通过meta表获取[myuser]表的位置信息包括存储的region信息，保存在哪个regionServer上
​			3.访问这个regionServer,扫描memStore和storefile查询数据，返回给client

#### 查询数据的三种方式

​			1.通过row key 访问
​			2.通过row key 的range访问
​			3.全表扫描
​			hbase会对rowkey 按照hash排序

#### 预分区

​			优点：可增加读写效率，负载均衡防止数据倾斜，方便集群容灾调度region,优化map数量
​			如何分区：每个region维护startRowkey和endRowkey,如果加入的rowkey符合区间就分配到这个region维护

​			手动预分区：1.指定key范围：create 'staff','info','partition1',SPLITS => ['1000','2000','3000','4000']

			  2. 16进制算法分区：create 'staff2','info','partition2',{NUMREGIONS => 15, SPLITALGO => 'HexStringSplit'}

#### rowKey设计原则

​			1.长度原则
​				设计成定长并且越短越好，尽量不要超过16字节。 否则在数据大的情况占用较多的空间影响存储效率。
​			2.散列原则
​				rowkey 按照hash排序，可以充分利用这个特点，将经常一起读取的数据存储到一块，最近可能访问的数据存储到一块。提高查询效率。
​				例：如果最近写入的数据是可能访问的，可以考虑将时间戳作为rowkey一部分，又因为rowkey按照hash排序，所以可以使用Long.MAX_VALUE-timestamp作为rowkey,这样可以保证新写入的数据在读取时可以快速命中。
​			3.唯一原则
​				rowkey唯一

#### 热点问题

​			本质问题是数据分配不均匀大量的访问频率多的数据落到一个或者几个region,导致region压力大。
​			解决：
​				使数据均匀分配到region上，可以通过以下方式：
​				1.加盐，可以在原来的rowkey前加上随机数，分配到不同的region
​				2.hash，可以保证同一行用同一个前缀加盐，可以是负载分散到整个集群，并且读也是可预测的，可以计算准确计算出rowkey并查询。
​				3.反转：
​					如手机号做rowkey时，前面的号段是固定的：177， 139,186等，可以将手机号反转做rowkey
​					时间戳反转：rowkey=Long.MAX_VALUE-timestamp

### 	 ambari

##  spark

​	是一个基于内存计算的大数据分析引擎，主要用于替换hadoop中的MapReduce计算框架，存储和资源调度等还是依赖hadoop组件。
此外，hadoop可以使用廉价，异构的服务器做分布式存储和计算，但spark对cpu和内存有一定的要求。

### spark core

#### 常用三种运行模式

​			local：适合本地开发和调试用
​			standalone: 独立的集群模式，典型的master/slave架构，是否开发和测试用
​			on yarn, 又可以分为两种模式：
​				client: driver 运行在客户端，方便调试，排查问题
​				cluster: driver 作为一个appMaster在yarn集群中启动

#### rdd定义

​			是一个分布式的弹性数据集合，是spark中最基本的数据抽象，代表一个不可变，可分区，元素可计算的集合。
​			dataset：数据集合
​			Distributed：里面的元素是分布存储的，可能来自多个分区，支持并行计算。
​			Resilient: 弹性的，可以保存在内存，本地磁盘和hdfs中。

#### rdd的五大属性

​			1.a list of partitions; 一个rdd由分片列表组成。分片数决定并行度。
​			2.a function for computing each split; 函数在每个分区内并行执行
​			3.a list of dependencies on other rdds; 一个rdd生成依赖于其他的rdd,某个rdd数据丢失可由前面的rdd重新计算得到。
​			4.Optionally, a Partitioner for key-value RDDs (e.g. to say that the RDD is hash-partitioned); 对于key-value类型的rdd，支持分区。默认是hashPartition,分区数决定shuffle输出时的分片数量。
​			5.Optionally, a list of preferred locations to compute each split on (e.g. block locations for an HDFS file)：
​				可选择，比如数据存储在hdfs上，spark在进行任务调度时，会优先在存有数据的worker上进行分配任务。

#### 创建rdd的几种方式

​			1.从文件创建：sc.textFile()
​			2.从集合创建：sc.parallel()
​			3.从其他rdd转换：rdd.map(),rdd.flatMap()

#### 宽依赖和窄依赖

​			窄依赖：子rdd中的分区来源同一个父rdd
​				1.可以并行计算 2.如果有分区丢失，只要从父rdd的一个分区重新计算即可，提高容错
​			宽依赖：子rdd中的分区来源多个父rdd
​				1.触发shuffle
​				2.是划分stage的依据

#### 持久化方式

​			persist:
​				有12种storageLevel:DISK_ONLY,MEMORY_ONLY,MEMORY_AND_DISK_ONLY等。
​			cache:实质是调用storageLevel.MEMORY_ONLY级的persist方法
​			checkpoint:
​				为了产生更可靠的数据持久化，一般发数据放到hdfs上（其他两种方法不支持），实现RDD的容错和高可用。
​				sc.setCheckpointDir("hdfs://node01:8020/checkpointDir")
​				sc.checkpoint()
​				

#### 持久化和checkpointe的区别

​			1.checkpoint可保存到hdfs,其他两种方式只能到内存或磁盘
​			2.cache和persist的方法在程序结束会被清除
​			3.cache和persist不会断掉依赖链，因为这种缓存是不可靠的，出现一些错误情况如executor挂掉，需要回溯依赖链恢复
​				checkpoint会断掉依赖链，因为不需要回溯。

#### join是宽依赖还是窄依赖?

​			可能是宽依赖也可能是窄依赖:
​			如果在join之前进行了groupByKey操作，就是窄依赖不会发生shuffle，否则宽依赖。

#### DAG

​			DAG(Direct Acyclic Graph有向无环图)，表示数据的转换执行的过程，有方向闭环。其实就是rdd执行的流程
​			通过一系列的transformation 算子形成DAG,多个action算子形成多个DAG。

#### stage的划分

​			为什么要划分stage? 为了能够并行计算，同一个stage内有多个算子形成一条流水线，算子内的多个分区可以并行计算。
​			如何划分stage? 根据shuffle/宽依赖使用回溯法，从后往前对DAG进行划分stage,遇到宽依赖就断开形成新的stage,遇到窄依赖加入当前stage（taskSet）。

#### spark运行流程

​		  架构：Driver：启动sparkContext, cluster：资源调度的集群可以是yarn也可以是其他,Worker：多个节点，运行Executor,执行task.
​		  流程：
​			1.启动sparkApp,初始化sparkContext
​			2.sparkContext向ResourceManager申请资源运行Executor
​			3.ResourceManager分配资源并启动Executor
​			4.Executor 通过heatbeat和ResourceManager通信
​			5.sparkContext构建DAG,并且划分stage,拆出taskSet
​			6.sparkContext把stage(taskSet)发给taskScheduler
​			7.Executor向sparkContext申请task
​			8.TaskScheduer分发task给Executor运行
​			9.sparkContext将程序执行代码发给Executor
​			10.task在Executor执行完毕，反馈给DAG scheduler,sparkContext向resourceManager注销并释放资源

#### wordcount：TODO

#### 常见算子：TODO

#### spark 调优

​			1.基础参数调优:
​				--num-executors
​				--driver-memory
​				--executor-memory
​				--executor-cores
​			2.使用缓存和checkpoint
​				避免数据丢失，如果rdd数据丢了可以直接从checkpoint恢复避免了重新计算。
​			3.使用广播变量
​				task共用的一些变量，可以设置为广播变量，set:sc.broadcast,get:广播变量名.value。广播变量保证在Executor内共享的。不必在每个task都存一份。
​			4.序列化：
​				spark默认java的序列化机制，官方称kryo序列化机制性能高很多，但不支持所有对象的序列化，所以从spark2.0只是一些简单类型的rdd默认kryo序列化
​			5.算子调优：
​				foreachPartition和foreach:前者每次处理一个分区的数据，也就是说如果涉及数据库连接，只需每个分区建一次连接。
​				reduceByKey和groupByKey
​					groupByKey 不会进行map端的聚合，reduceByKey会在map端进行一次聚合，因此减少网络传输，效率高一些。
​			6.适当提高map端，reduce端的缓冲：
​				map端的缓冲默认配置32KB,new SparkConf().set("spark.shuffle.file.buffer", "64")
​				适当提高reduce端拉取数据的缓冲区，默认48MB：new SparkConf().set("spark.reducer.maxSizeInFlight", "96")
​			7.bypass机制：TODO
​			8.spark数据倾斜:
​				1.如果使用了groupByKey,reduceByKey类似的算子，可以考虑使用随机key实现双重聚合（给key加上随机前缀），后面再去掉前缀。
​				2.适当增加reduce数量，提高并行度，缓解数据倾斜问题，但是极端情况下也是不好使的。
​				3.使用mapjoin,如果一个rdd是比较小的，可以采用rdd作为广播变量+ map算子的操作进行mapjoin,此时就不会发生shuffle操作，也不会发生数据倾斜。

#### 常见问题

​			内存溢出，无非是三中情况：map执行oom,shuffl后oom,driver oom
​				driver heap OOM：
​					1.可能在driver端生成了大的对象，如一个大的集合，可以考虑放在executor端加载如sc.textFile(),sc.parallel()。或者增加driver内存。
​					2.从executor收集数据回来时，如collect算子，建议转成executor端的算子执行。
​				map task运行的executor OOM：
​					增大executor内存
​				shuffle阶段（reduce端）内存溢出
​					reduce task拉取map端的数据，一边拉一边聚合，reduce端有一块聚合内存：executor memory * 0.2,解决：
​					1.增加reduce聚合内存比例：spark.shuffle.memoryFraction
​					2.减少reduceTask每次拉取的数量：spark.reducer.maxSizeInFlight 16m
​			gc导致的shuffle文件拉取失败
​				后面的stage拉取上一个stage数据时，上一个stage所在的executor正在执行GC,导致报错：shuffle file not found.解决：
​				增加拉取重试的次数和时间：
​					spark.shuffle.io.maxRetries, 6
​					spark.shuffle.io.retryWait, 60
​			yarn_cluster模式JVM内存溢出
​				yarn-client模式下，driver在本机运行，使用本机的spark-class文件，JVM永久代大小是128MB,运行没有问题的
​				但是在cluster模式下，yarn 缺省永久代82MB,容易出现OOM,解决：
​					增加PermGen内存：-XX:PermSize=128M -XXMaxPerSize=256M

#### 广播变量：TODO

### spark sql

#### 数据结构

​			1.dataframe
​				是一种以RDD为基础的分布式数据集，类似传统的二维表格，带有schema元信息（可以理解为数据库的列名和类型）
​			2.dataset
​				和rdd相比，保存了更多的描述信息，概念上等同于数据库的二维表。
​				和dataFrame相比，是强类型的，提供了编译时的类型检查
​				dataSet 包含了dataFrame的功能，spark2.0中两者统一。
​				总结：
​					dataFrame = rdd - 泛型 + sql + schema + 优化
​					dataFrame = dataSet[row]
​					dataSet = dataFrame + 泛型
​					dataSet = rdd + schema + sql + 优化

#### 创建dataFrame的几种方式

​				1.指定列名添加schema:
​					rowRdd.toDF("id", "name", "age")
​				2.反射推断Schema
​				3.StructType指定schema

## 数据仓库



# 数据库

# 项目

## 1 solea

#### ceph

是一个分布式的，可扩展的文件存储系统。有三个主要的进程组件：osd,用来存储对象的实例，一般一块盘化作一个osd。

Monitor：监控集群的状况。

MDS：用来同步元数据。

#### spring-batch

是一个轻量级的批处理框架。可以通过简单的配置来操作批处理作业，支持重试，跳过，设置容错几率。

有两个重要的概念：job和step，job可以配置多个step，每个step可以配置多个批处理的process。比如翻译的process,敏感信息监测的process,关键词匹配的process等。

通过reader的open方法连接kafka,read方法轮询从kafka读取记录， writer方法发送solr。

#### tika

是apache 下一个提取文件内容的工具包，有包含几个重要的模块：

文件类型检测（由autoDetectParser实现，也支持自定义文件类型的识别）

语言检测机制（语种识别），内部是用N-gram算法语言检测。

内容提取：通过不同的文件类型调用不同的parser去提取文件内容，如：

pdf通过pdf-box去提取内容，eml通过javax下面的类库，office文件通过poi等

同时通过service loader 动态的注册扩展parser。

service loader:

1.创建一个接口文件：serviceLoader

2.在resource下面的META-info创建一个文件夹services

3.在文件加下创建一个以接口为名的文件如：com.lzy.solea.parser

4.实现这个接口。



#### IM数据

solea中有三种数据源：实时的，离线的，批IM数据，按月推送过来。

对IM数据做聚合,以id_from_id为key ,然后combineByKey合并成完整的消息记录。

发送solr, 附件发送ceph，保存文件的url,然后以广播变量的形式加载关联。

在界面显示的话，从solr查询，附件从ceph查询。





## 2 titan

##### kettle是什么

kettle是一款java编写的ETL工具，可以通过图形化的方式来管理不同来源的数据，然后以想要的格式输出。

有两种功能组件：transformation（转换）和job（作业），transformation完成对数据的基础转换，job对整个工作流程的控制。

相当于我们titan中的算子和模型。包含四大工具：

spoon：允许通过界面来设计ETL过程

pan：是一个后台执行程序，类似时间调度器

chef和kitchen

##### 如何基于kettle二次开发

kettle是由java编写的ETL工具，基于C/S模式，使用上有一定局限性，并且要开发一些业务的算子（转换）。

实现新的转换插件至少实现3个新的接口：

StepInterface：负责数据的转换和流转，主要是重写processRow方法

StepDataInterface：处理数据

StepMetaInterface：元数据处理，主要是对一个步骤的定义的基本数据。

##### sparkSql on hive

存储是基于hive, 用sparksql操作hive实际是把hive当做数据仓库，这样好处：数据存储不用改变，spark sql是基于内存，效率高。

方式：

修改hive-site.xml：指定hive.metastore.uris

启动hive，作为数据仓库服务和spark服务。

使用hiveContext（是sqlContext的子类）来操作hive的数据。执行引擎是spark。



强同行：

铁路订票的数据，同行标识+出发地点+到达地点+发车时间+车次一致就是一次强同行。

弱同行：

出发地+到达地+时间 相同 > 3 就是一条弱同行

同住算子：

拼接同住唯一标识：宾馆编号+房间号+入住时间 

每台每天80亿的电围数据，最多缓存5天的数据，需要的磁盘：

80亿 * 5天 * 0.4KB=16TB, 建议一台至少24TB的磁盘。



接入的原始数据：

用户号码

IMEI

IMSI

基站经纬度

号码归宿地



实体要素：

手机号信息，号码，归属地等。

关系要素：

主叫，被叫，短信发送、短信接收等

行为要素：

打电话，发短信

位置要素：

经度，维度，时间等。



# 数据结构和算法

#### 冒泡

#### 快排

#### 二分查找

# python

# 了解一下

## 1.  flink

## 2.ambari

## 3.



## 其他

编码
ascll： 起源于欧洲的一套字符集，基础字符加上扩展字符一共256字符
gbk: 中文编码字符集 存储字符以2个字节
gb2312： 中文编码字符集，存储字符以2个字节
gb18030： 存储字符以1，2,4字节可变编码
	单字节， 0 到0x7f 与ascll编码兼容
	双字节， 与gbk编码兼容
	四字节
unicode： 一个编码标准，包括全世界所有字符的字符集，编码规则等。
utf-8： 针对unicode的可变长度字符编码， 一般英文1字节，中文3字节，大多数情况使用utf-8编码
iso-8859-1 ：国际标准组织iso,单字节编码字符集，以ascll扩展而来，前256和ascll完全一样，后256表示其他特殊字符。因为ISO-8859-1编码范围使用了单字节内的所有空间，在支持ISO-8859-1的系统中传输和存储其他任何编码的字节流都不会被抛弃。换言之，把其他任何编码的字节流当作ISO-8859-1编码看待都没有问题。



#### 心仪的公司

蘑菇街

有赞

涂鸦智能

兑吧

51信用卡

恩牛

每日互动

海康威视

。。。



##### 面试

1. 3.31 上午 10：30 数政科技 		--14k, 大小周

​	自我介绍，项目介绍，开始根据简历提问：

说一说多线程，线程池这些

​	三种线程池，一些参数。

说一说宽窄依赖

​	宽窄依赖讲一讲，宽依赖产生shuffle，并且划分stage。

kafka怎么保证不丢数据

​	从三方面讲：生产者，broker，消费者。

hive的窗口函数有没有用过

​	没有使用过。

说一说shell常用的一些命令

​	从部署一个程序来讲，scp,tar -zxvf 这些。排查问题ps -ef, top jstack 

hive的udf函数，有没有实现过。

​	说了几个简单的，avg,count,自己实现了一个ip转换的udf算子。

oracle索引如何建立，怎么去优化？ 不知道

写过的最难的sql什么样？

用弱同行的算子讲的,出行时间+出行目的地+出发地 一致 > 3生成一条弱同行记录。

伪sql:

select a.* from record a, record b where a.send_time = b.send_time and a.src_addr = b.src.addr and a.dest_addr = b.dest_addr group by a.phone_number

hive 如何做数据去重？

​	只讲了distinct，回来搜了一些，有三种：

distinct,但是只能对单列的字段去重。

group by，不能只对一列去重，

用窗口函数row_number() over()：

给分组后的每个id加一列按某一字段倒叙排序的rang值，取rank=1.



hive里面的压缩格式

常用的snappy，gzip

2. 3.31 下午 13：30 华云科技			--工资还没具体说，应该不高

​	问了些项目的东西，也没有问技术方面，很奇怪，去了又说他们不缺人， 就闲聊了一会，然后回来等通知了。应该没有戏。

更奇怪的是面试官说 他们不看重技术和能力，主要看中经历和学历。技术占很小部分。无语。。。

3. 4.1 上午 10：30 杭州致成电子科技		--14k * 14，公积金按最高缴，加班，会出差

​		自我介绍，项目介绍。

​		spark 两种算子区别		--ok

​		写过最复杂的sql是什么 ？ 讲了下强同行算子实现

​		redis, hive, hbase区别		--ok

​		手写一个spark word count 		--ok,辛亏前一天晚上看了

​		有没有遇到数据上的问题		--ok，说了压缩包解压失败的问题，很巧引起了面试官的共鸣。

​		说一下多线程			--ok

​		是否了解scala		--ok

​		问我上手scala要多久		--ok,看半天到一天就可以上手

4. 4.1下午 14：00 能欣投资		16 * 13 ,自研，不加班

​		比较重视hbase

​	hbase rowKey的设计原则 		--ok

​	hbase 一台服务器宕机了怎么办		--ok

​	写过最难的sql是什么，多少行？ 存储过程？		--没有写过

​		二面，总监面：

​		给了纸笔，画图整个系统的一个数据流向		--ok

根据这个流程去讲一些，能想到的都讲		--ok

ocr怎么实现的，视频怎么处理？



总结一下：

hbase, hive，spark这些还是多看看，怎么用

sql，oracle这些看看。



titan的项目再想想，一问到就虚。



5. 4.2 上午 10：00 奕象科技（电话面试）

​	项目介绍

​	说一说redis

​	你们的hbase rowkey 怎么设计的

​	数据仓库怎么设计的

​	你们的数据增量导入怎么做的

​	oracle 表有多少条数据

​	hdfs读写流程

​	sql写的多不多

​	说一说常用的一些spark 算子

​	map和mapParitions区别？

​	

​	spark 的优化

下午 1：00 中通快运（电话面试）		--人事面的，我不太感兴趣

​	上六休一，没有公积金



下午2：00  杭州时代银通 （电话面）		--上线没有开成



TODO:

晚上7：00 中华财险 （视频，腾讯会议）

问了一下项目问题

hive数据倾斜

hbase读写流程

flink有没有用过                                                   

除了kettle其他的调度工具有没有用过

使用广播变量 + map join为什么这样做？

你们rowKey怎么设计的？

shuffle过程？几次排序？

为什么用solr不用es?





6. 4.6号 周二下午两点	铭师堂 现场面试（浙江 杭州市 拱墅区                         中国智慧信息产业园H座9楼）

自我介绍

用过哪些api

​		hashMap, ArrayList, FileReader这些

redis集群怎么搭建？qps? 服务器的型号？cpu型号？磁盘？基准测试？

kafka集群搭建，怎么在不重启服务器情况动态扩容？

flink有没有用过？

创建线程的方式？除了这三种还有没有其他方式？

线程怎么优雅的指定线程名？

为什么用cacheThreadPool? 用FixThreadPool也可以实现啊？

ThreadLocal有没有用过？原理？InheritableThreadLocal呢？

currentHashMap原理？jdk1.7跟jdk1.8的实现原理一样吗？分段锁为什么比HashTable效率高？

solr的分片不均匀有没有遇到过？怎么处理？

solr里面存储都有哪些数据？

你知道哪些rpc框架？zk是否了解？

最后问个简单的算法题：

​	假设有m杯水，其中有一杯有毒，小白鼠喝到有毒的第二天才会死掉。问：现在要在第二天就找出哪杯水有毒，至少要几个小白鼠。





4.6 缦图摄影	晚上六点 电话面试

问的数仓比较多

你们数仓怎么分层的？有什么规范吗？

数据量多少？

拿到一个需求怎么做？

数仓觉得有没有做的好的地方和不好的地方？

搭建数仓过程有没有遇到过什么问题？怎么处理的？

实时计算框架有没有了解过？flink?

你们的数仓有哪些主题？

数仓的一些理论知识？

python2.7跟python3主要有哪些区别？










