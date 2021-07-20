{"objectClass":"NSDictionary","root":{"objectClass":"MindNode","ID":"1K3CZ","rootPoint":{"objectClass":"CGPoint","x":360,"y":3995.5},"lineColorHex":"#BBBBBB","children":{"0":{"objectClass":"MindNode","ID":"L6E7T","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"1NU8T","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"95602","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"XHEWN","lineColorHex":"#DC306C","text":"volatile，变量的可见性和有序性"},"objectClass":"NSArray"},"text":"共享内存"},"1":{"objectClass":"MindNode","ID":"1XN4S","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"9G898","lineColorHex":"#DC306C","text":"wait/notify"},"1":{"objectClass":"MindNode","ID":"X17S3","lineColorHex":"#DC306C","text":"join"},"2":{"objectClass":"MindNode","ID":"K1N3X","lineColorHex":"#DC306C","text":"synchronized"},"3":{"objectClass":"MindNode","ID":"W588Z","lineColorHex":"#DC306C","text":"CountDownLatch/CyclicBarrier"},"4":{"objectClass":"MindNode","ID":"34M8S","lineColorHex":"#DC306C","text":"Fork/join"},"5":{"objectClass":"MindNode","ID":"HA375","lineColorHex":"#DC306C","text":"ThreadLocal/InheritableThreadLocal"},"objectClass":"NSArray"},"text":"消息传递"},"2":{"objectClass":"MindNode","ID":"C5POM","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"58IUF","lineColorHex":"#DC306C","text":"PipedInputStream/PipedOutputStream和\nPipedReader/PipedWriter"},"objectClass":"NSArray"},"text":"管道输入/输出流"},"objectClass":"NSArray"},"text":"线程之间如何通信"},"1":{"objectClass":"MindNode","ID":"B45H0","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"31R4Z","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"74SUZ","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"Y1F54","lineColorHex":"#DC306C","text":"Mark Word, 存储对象的HashCode,锁标志和分代年龄"},"1":{"objectClass":"MindNode","ID":"67927","lineColorHex":"#DC306C","text":"Class Pointer,对象指向它的类元数据的指针，虚拟机通过这个指针来确定对象是哪个类的实例"},"2":{"objectClass":"MindNode","ID":"9RDG6","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"2E91Y","lineColorHex":"#DC306C","text":"EntryList"},"1":{"objectClass":"MindNode","ID":"WLHX4","lineColorHex":"#DC306C","text":"Owner,指向持有Monitor对象的线程"},"2":{"objectClass":"MindNode","ID":"8177V","lineColorHex":"#DC306C","text":"WaitSet"},"objectClass":"NSArray"},"text":"Monitor"},"objectClass":"NSArray"},"text":"对象头"},"1":{"objectClass":"MindNode","ID":"3LY1D","lineColorHex":"#DC306C","text":"实例数据"},"2":{"objectClass":"MindNode","ID":"AEMRU","lineColorHex":"#DC306C","text":"对其填充"},"objectClass":"NSArray"},"text":"对象"},"1":{"objectClass":"MindNode","ID":"54332","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"S331T","lineColorHex":"#DC306C","text":"代码块：锁住的是括号内的对象， 通过monitorenter和monitorexit锁对象，程序计数器count"},"1":{"objectClass":"MindNode","ID":"C103F","lineColorHex":"#DC306C","text":"方法上：锁住的是当前调用对象,ACC_SYNCHRONIZED"},"2":{"objectClass":"MindNode","ID":"AGK88","lineColorHex":"#DC306C","text":"静态方法：这个类对象，而不是实例对象"},"objectClass":"NSArray"},"text":"使用"},"2":{"objectClass":"MindNode","ID":"8J552","lineColorHex":"#DC306C","text":"锁升级过程：偏向锁 -> 轻量级锁（乐观锁） -> 重量级锁"},"objectClass":"NSArray"},"text":"synchronized"},"2":{"objectClass":"MindNode","ID":"BSG2S","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"P2L8B","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"5UGJ8","lineColorHex":"#DC306C","text":"get"},"1":{"objectClass":"MindNode","ID":"C3V11","lineColorHex":"#DC306C","text":"put"},"objectClass":"NSArray"},"text":"实现"},"1":{"objectClass":"MindNode","ID":"MZ16X","lineColorHex":"#DC306C","text":"内存泄露"},"2":{"objectClass":"MindNode","ID":"757T9","lineColorHex":"#DC306C","text":"InheritableThreadLocal"},"3":{"objectClass":"MindNode","ID":"IH33R","lineColorHex":"#DC306C","text":"用来解决什么问题，为什么使用"},"objectClass":"NSArray"},"text":"ThreadLocal"},"3":{"objectClass":"MindNode","ID":"4D163","lineColorHex":"#DC306C","text":"volatile"},"4":{"objectClass":"MindNode","ID":"NB7M6","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"56112","lineColorHex":"#DC306C","text":"Lock"},"1":{"objectClass":"MindNode","ID":"4O714","lineColorHex":"#DC306C","text":"CountDownLatch"},"2":{"objectClass":"MindNode","ID":"86B16","lineColorHex":"#DC306C","text":"CyclicBarrier"},"objectClass":"NSArray"},"text":"JUC"},"5":{"objectClass":"MindNode","ID":"PVY47","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"27745","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"NFR47","lineColorHex":"#DC306C","text":"CachedThreadPool"},"1":{"objectClass":"MindNode","ID":"56LNL","lineColorHex":"#DC306C","text":"FixedThreadPool"},"2":{"objectClass":"MindNode","ID":"3051P","lineColorHex":"#DC306C","text":"SingleThreadPool"},"3":{"objectClass":"MindNode","ID":"SY32N","lineColorHex":"#DC306C","text":"ScheduledThreadPool"},"objectClass":"NSArray"},"text":"4种线程池"},"1":{"objectClass":"MindNode","ID":"KJ6YX","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"3YDCG","lineColorHex":"#DC306C","text":"corePoolSize"},"1":{"objectClass":"MindNode","ID":"H8176","lineColorHex":"#DC306C","text":"maximumPoolSize"},"2":{"objectClass":"MindNode","ID":"GE45Z","lineColorHex":"#DC306C","text":"keepAliveTime"},"3":{"objectClass":"MindNode","ID":"884IB","lineColorHex":"#DC306C","text":"TimeUnit"},"4":{"objectClass":"MindNode","ID":"3MRJV","lineColorHex":"#DC306C","text":"workQueue"},"5":{"objectClass":"MindNode","ID":"5L2G3","lineColorHex":"#DC306C","text":"threadFactory"},"6":{"objectClass":"MindNode","ID":"F4IYD","lineColorHex":"#DC306C","text":"RejectedExecutionHandler"},"objectClass":"NSArray"},"text":"7个参数"},"2":{"objectClass":"MindNode","ID":"T68K2","lineColorHex":"#DC306C","text":"如何优雅的指定线程名字"},"3":{"objectClass":"MindNode","ID":"95R86","lineColorHex":"#DC306C","text":"阿里不建议newCachedThreadPool等方法，建议自己构造原因"},"4":{"objectClass":"MindNode","ID":"CK7XC","lineColorHex":"#DC306C","text":"工厂方法"},"5":{"objectClass":"MindNode","ID":"U47LF","lineColorHex":"#DC306C","text":"创建线程几种方式"},"6":{"objectClass":"MindNode","ID":"QN8LB","lineColorHex":"#DC306C","text":"线程池运行原理，状态"},"7":{"objectClass":"MindNode","ID":"12H34","lineColorHex":"#DC306C","text":"使用场景"},"8":{"objectClass":"MindNode","ID":"33IMO","lineColorHex":"#DC306C","text":"怎么尽可能提高多线程并发数"},"objectClass":"NSArray"},"text":"线程池"},"objectClass":"NSArray"},"text":"并发"},"1":{"objectClass":"MindNode","ID":"O2CH7","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"2GXVL","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"5B8O8","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"C78N8","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"26GLP","lineColorHex":"#BF58F5","text":"如何保证线程安全"},"objectClass":"NSArray"},"text":"ArrayList"},"1":{"objectClass":"MindNode","ID":"87Q31","lineColorHex":"#BF58F5","text":"LinkedList"},"objectClass":"NSArray"},"text":"List"},"1":{"objectClass":"MindNode","ID":"5L514","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"261F6","lineColorHex":"#BF58F5","text":"HashSet"},"1":{"objectClass":"MindNode","ID":"07PF4","lineColorHex":"#BF58F5","text":"TreeSet"},"objectClass":"NSArray"},"text":"Set"},"2":{"objectClass":"MindNode","ID":"GB293","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"6H262","lineColorHex":"#BF58F5","text":"LinkedBlockingQueue"},"objectClass":"NSArray"},"text":"Queue"},"objectClass":"NSArray"},"text":"Collection"},"1":{"objectClass":"MindNode","ID":"1LM23","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"KF67L","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"T4231","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"Y2I46","lineColorHex":"#BF58F5","text":"数组+链表"},"1":{"objectClass":"MindNode","ID":"A7S57","lineColorHex":"#BF58F5","text":"头插法"},"objectClass":"NSArray"},"text":"1.7"},"1":{"objectClass":"MindNode","ID":"42PZH","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"42328","lineColorHex":"#BF58F5","text":"数组+链表+红黑树"},"1":{"objectClass":"MindNode","ID":"5VGNX","lineColorHex":"#BF58F5","text":"尾插法"},"objectClass":"NSArray"},"text":"1.8"},"2":{"objectClass":"MindNode","ID":"82UL3","lineColorHex":"#BF58F5","text":"扩容机制：默认0.75"},"3":{"objectClass":"MindNode","ID":"1EYN1","lineColorHex":"#BF58F5","text":"capacity是2的幂"},"4":{"objectClass":"MindNode","ID":"Y0N23","lineColorHex":"#BF58F5","text":"put过程"},"5":{"objectClass":"MindNode","ID":"E22FL","lineColorHex":"#BF58F5","text":"get过程"},"objectClass":"NSArray"},"text":"HashMap"},"1":{"objectClass":"MindNode","ID":"43E4U","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"D5RW4","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"641V5","lineColorHex":"#BF58F5","text":"分段锁segment实现并发"},"1":{"objectClass":"MindNode","ID":"IPMUO","lineColorHex":"#BF58F5","text":"每个segment负责一段桶，通过给segment加锁保证线程安全"},"2":{"objectClass":"MindNode","ID":"6M5Y6","lineColorHex":"#BF58F5","text":"数组+链表"},"3":{"objectClass":"MindNode","ID":"8P45S","lineColorHex":"#BF58F5","text":"DEFAULT_CONCURRENCY_LEVEL默认16，支持多少线程并发写"},"objectClass":"NSArray"},"text":"jdk1.7及之前"},"1":{"objectClass":"MindNode","ID":"JNB67","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"YVWO4","lineColorHex":"#BF58F5","text":"CAS+syncronized实现线程安全"},"1":{"objectClass":"MindNode","ID":"QKGQ0","lineColorHex":"#BF58F5","text":"数组+链表+红黑树"},"2":{"objectClass":"MindNode","ID":"Y8U0P","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"546S6","lineColorHex":"#BF58F5","text":"第一次put,加载表，避免第一次操作就resize,如putAll"},"1":{"objectClass":"MindNode","ID":"B4J1S","lineColorHex":"#BF58F5","text":" 如果put位置桶空的，则通过CAS添加新节点"},"2":{"objectClass":"MindNode","ID":"9SF58","lineColorHex":"#BF58F5","text":" 如果put位置桶不为空，但hash状态==MOVED(-1),说明当前表正在进行resize,当前线程进入helpTransfer方法"},"3":{"objectClass":"MindNode","ID":"2UIY3","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"60PBO","lineColorHex":"#BF58F5","text":"判断是链表还是红黑树，用对应的方法去添加"},"1":{"objectClass":"MindNode","ID":"5J418","lineColorHex":"#BF58F5","text":"如果有相同key则覆盖"},"2":{"objectClass":"MindNode","ID":"66VRE","lineColorHex":"#BF58F5","text":"没有相同key,添加新节点到后面"},"objectClass":"NSArray"},"text":"如果put位置桶不为空，说明桶已经有人了，对当前的桶进行加锁"},"4":{"objectClass":"MindNode","ID":"W1725","lineColorHex":"#BF58F5","text":"调用addCount(int count, int check),count表示新增元素个数，check是链表的长度. 计算并且检查是否需要resize"},"objectClass":"NSArray"},"text":"put"},"3":{"objectClass":"MindNode","ID":"X8230","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"X677G","lineColorHex":"#BF58F5","text":"判断表不为空，并且状态是-1，正在转移,判断正在扩容，并且扩容线程是否满了"},"1":{"objectClass":"MindNode","ID":"Q762I","lineColorHex":"#BF58F5","text":"通过CAS设置扩容的线程数量+1"},"2":{"objectClass":"MindNode","ID":"6356Y","lineColorHex":"#BF58F5","text":"调用transfer方法，进行扩容"},"objectClass":"NSArray"},"text":"helpTransfer"},"4":{"objectClass":"MindNode","ID":"5ZQQT","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"RR947","lineColorHex":"#BF58F5","text":"根据cpu个数，配置的最小步数：16和表长度计算步长，即每次转移多少节点"},"1":{"objectClass":"MindNode","ID":"C4M9W","lineColorHex":"#BF58F5","text":"初始化一个长度为原来2倍的数组"},"2":{"objectClass":"MindNode","ID":"QWC21","lineColorHex":"#BF58F5","text":"进行转移"},"objectClass":"NSArray"},"text":"transfer"},"objectClass":"NSArray"},"text":"jdk1.8"},"objectClass":"NSArray"},"text":"ConcurrentHashMap"},"2":{"objectClass":"MindNode","ID":"O1W56","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"PBYX5","lineColorHex":"#BF58F5","text":"继承自HashMap，内部实现双向链表"},"1":{"objectClass":"MindNode","ID":"SK80P","lineColorHex":"#BF58F5","text":"两种排序方式，通过accessOrder选择，为true时实现LRU"},"2":{"objectClass":"MindNode","ID":"W4188","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"Y0LK1","lineColorHex":"#BF58F5","text":"void afterNodeAccess(Node<K,V> p) "},"1":{"objectClass":"MindNode","ID":"O71DW","lineColorHex":"#BF58F5","text":"void afterNodeInsertion(boolean evict)"},"2":{"objectClass":"MindNode","ID":"6G1D7","lineColorHex":"#BF58F5","text":"void afterNodeRemoval(Node<K,V> p)"},"3":{"objectClass":"MindNode","ID":"SXI6R","lineColorHex":"#BF58F5","text":"Node<K,V> newNode(int hash, K key, V value, Node<K,V> e"},"4":{"objectClass":"MindNode","ID":"7583B","lineColorHex":"#BF58F5","text":"void afterNodeRemoval(Node<K,V> e)"},"objectClass":"NSArray"},"text":"重写了HashMap的几个方法"},"objectClass":"NSArray"},"text":"LinkedHashMap"},"objectClass":"NSArray"},"text":"Map"},"objectClass":"NSArray"},"text":"集合"},"2":{"objectClass":"MindNode","ID":"831L8","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"1EXCV","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"EY183","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"H45Z0","lineColorHex":"#26BBFF","text":"执行的字节码的信号指示器；线程私有；不会OOM"},"objectClass":"NSArray"},"text":"程序计数器"},"1":{"objectClass":"MindNode","ID":"4R0YG","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"4CH70","lineColorHex":"#26BBFF","text":"存放基本数据类型，对象引用，方法出口等，线程私有"},"objectClass":"NSArray"},"text":"java虚拟机栈"},"2":{"objectClass":"MindNode","ID":"D3E2N","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"6G6I4","lineColorHex":"#26BBFF","text":"和java虚拟机栈相似，不过是服务native方法"},"objectClass":"NSArray"},"text":"native方法栈"},"3":{"objectClass":"MindNode","ID":"5IG8V","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"2W308","lineColorHex":"#26BBFF","text":"共享内存，实例对象和数组存放的区域，Gc回收的区域。"},"objectClass":"NSArray"},"text":"堆"},"4":{"objectClass":"MindNode","ID":"F17W8","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"9K74T","lineColorHex":"#26BBFF","text":"非堆，存放加载类的信息，常量，静态变量等；也会发生Gc,主要针对常量回收和类型的卸载；jdk8之前实现是永久代，1.8后实现是元空间，使用直接内存因为大小不好估算；会发生OOM"},"objectClass":"NSArray"},"text":"方法区"},"objectClass":"NSArray"},"text":"内存结构"},"1":{"objectClass":"MindNode","ID":"06QFE","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"A2D0Y","lineColorHex":"#26BBFF","text":"OutOfMemory,堆中没有空间分配对象，栈中没有足够空间分配新线程"},"1":{"objectClass":"MindNode","ID":"R4151","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"3B3R2","lineColorHex":"#26BBFF","text":"超过栈最大深度：递归"},"objectClass":"NSArray"},"text":"StackOverFlow"},"objectClass":"NSArray"},"text":"内存异常"},"2":{"objectClass":"MindNode","ID":"WI53N","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"FQE4P","lineColorHex":"#26BBFF","text":"引用计数法"},"1":{"objectClass":"MindNode","ID":"6WB34","lineColorHex":"#26BBFF","text":"根可达分析法"},"objectClass":"NSArray"},"text":"生存还是死亡"},"3":{"objectClass":"MindNode","ID":"9I977","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"N6JDX","lineColorHex":"#26BBFF","text":"强，Object o = new Object(),不会被回收"},"1":{"objectClass":"MindNode","ID":"Y50M9","lineColorHex":"#26BBFF","text":"软，内存不足会回收"},"2":{"objectClass":"MindNode","ID":"DKY4U","lineColorHex":"#26BBFF","text":"弱，下一次gc会回收，ThreadLocal"},"3":{"objectClass":"MindNode","ID":"A84MW","lineColorHex":"#26BBFF","text":"虚，不影响对象生命周期，主要用来跟踪对象是否被回收：当对象被回收时，如果有虚引用会将引用加入引用队列，可通过队列是否有虚引用判断对象是否回收。"},"objectClass":"NSArray"},"text":"引用类型"},"4":{"objectClass":"MindNode","ID":"BDX41","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"75YPJ","lineColorHex":"#26BBFF","text":"标记清除：缺点是如果回收对象多，执行效率降低；产生碎片；"},"1":{"objectClass":"MindNode","ID":"IOJ68","lineColorHex":"#26BBFF","text":"标记复制：优点是使用于新生代有大量对象回收，只需复制少量对象；不会产生内存碎片。\n缺点是需要浪费一半内存，并且如果存活对象多，复制效率就低。"},"2":{"objectClass":"MindNode","ID":"574LL","lineColorHex":"#26BBFF","text":"标记整理：适合老年代特征，优点是减少内存碎片化问题；缺点是移动对象会产生Stop The World"},"objectClass":"NSArray"},"text":"垃圾收集算法"},"5":{"objectClass":"MindNode","ID":"3H9FH","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"3W2K4","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"UDB2O","lineColorHex":"#26BBFF","text":"基础的单线程收集器；新生代标记复制，老年代标记整理；优点是内存小，缺点是Stop The World明显"},"objectClass":"NSArray"},"text":"Serial收集器"},"1":{"objectClass":"MindNode","ID":"W1815","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"4CZ12","lineColorHex":"#26BBFF","text":"跟Serial几乎一样，多线程版本；\n除了Serial唯一可以和CMS搭配使用的收集器"},"objectClass":"NSArray"},"text":"ParNew收集器"},"2":{"objectClass":"MindNode","ID":"WVMT2","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"V446C","lineColorHex":"#26BBFF","text":"吞吐量优先收集器；新生代收集器；基于标记复制算法；参数可控制收集时间也可直接设置吞吐量"},"objectClass":"NSArray"},"text":"Parallel Scavenge收集器"},"3":{"objectClass":"MindNode","ID":"LU1FA","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"64VG6","lineColorHex":"#26BBFF","text":"Serial老年代版本；标记整理算法；可作为CMS失败后的预案"},"objectClass":"NSArray"},"text":"Serial Old收集器"},"4":{"objectClass":"MindNode","ID":"UN7I5","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"6BS8Q","lineColorHex":"#26BBFF","text":"Parallel Scavenge老年代版本，可和Parallel Scavenge搭配使用。"},"objectClass":"NSArray"},"text":"Parallel Old收集器"},"5":{"objectClass":"MindNode","ID":"6JU7X","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"9X91E","lineColorHex":"#26BBFF","text":"基于标记清除算法；\n过程：初始标记，并发标记，重新标记，并发清除；\n优点是并发收集，低停顿；缺点是依赖服务器资源"},"objectClass":"NSArray"},"text":"CMS收集器"},"6":{"objectClass":"MindNode","ID":"T7SS2","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"15743","lineColorHex":"#26BBFF","text":"Region作为回收最小单元；\n局部优先回收方式保证在运行停顿时间尽可能高的回收效率；\n过程：初始标记，并发标记，最终标记，筛选回收；\n优点是局部优先回收方式，可预测的停顿；不会产生垃圾碎片；\n缺点是占用内存多"},"objectClass":"NSArray"},"text":"G1收集器"},"objectClass":"NSArray"},"text":"垃圾收集器"},"6":{"objectClass":"MindNode","ID":"HSY2Z","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"CD56U","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"27F1Q","lineColorHex":"#26BBFF","text":"启动类加载器（Bootstrap ClassLoader）"},"1":{"objectClass":"MindNode","ID":"8HM9X","lineColorHex":"#26BBFF","text":"扩展类加载器（Extension ClassLoader）/\n平台类加载器（Platform Class Loader）"},"2":{"objectClass":"MindNode","ID":"82XH2","lineColorHex":"#26BBFF","text":"应用程序类加载器（Application ClassLoader）"},"objectClass":"NSArray"},"text":"类加载器就是根据指定全限定名称将class文件加载到JVM内存，转为Class对象。"},"1":{"objectClass":"MindNode","ID":"MTTT0","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"28723","lineColorHex":"#26BBFF","text":"可以保证基础类型的一致性"},"1":{"objectClass":"MindNode","ID":"ETO54","lineColorHex":"#26BBFF","text":"打破：继承ClassLoader类，还要重写loadClass和findClass方法"},"objectClass":"NSArray"},"text":"双亲委派模型"},"objectClass":"NSArray"},"text":"类加载器"},"7":{"objectClass":"MindNode","ID":"22H3X","lineColorHex":"#26BBFF","text":"类加载过程"},"objectClass":"NSArray"},"text":"jvm"},"3":{"objectClass":"MindNode","ID":"722M1","lineColorHex":"#37C45A","text":"io"},"4":{"objectClass":"MindNode","ID":"C45XW","lineColorHex":"#1BD6E7","text":"反射"},"5":{"objectClass":"MindNode","ID":"UP5S4","lineColorHex":"#FFC700","text":"泛型"},"objectClass":"NSArray"},"text":"java基础","style2":{"objectClass":"NSDictionary","fontSize":1,"strikethrough":0}},"ID":"235N2","style":1,"subjects":{"0":{"objectClass":"NSDictionary","root":{"objectClass":"MindNode","ID":"87JVB","rootPoint":{"objectClass":"CGPoint","x":360,"y":2698},"lineColorHex":"#BBBBBB","children":{"0":{"objectClass":"MindNode","ID":"8HW5Q","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"2I19P","lineColorHex":"#DC306C","text":"mysql"},"1":{"objectClass":"MindNode","ID":"5VTQ9","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"N632Q","lineColorHex":"#DC306C","text":"基于ZAB算法的分布式协调服务：实现集群管理，master选举，分布式锁和队列等功能。"},"1":{"objectClass":"MindNode","ID":"511KO","lineColorHex":"#DC306C","text":"ZAB:专门为zk设计的协议，主要两个特性：消息广播和崩溃恢复"},"2":{"objectClass":"MindNode","ID":"EC44I","lineColorHex":"#DC306C","text":"数据发布/订阅：拉，客户端轮询。推：注册Watcher"},"3":{"objectClass":"MindNode","ID":"31142","lineColorHex":"#DC306C","text":"集群管理：基于两个特性：临时会话和Watcher监听"},"4":{"objectClass":"MindNode","ID":"E8I23","lineColorHex":"#DC306C","text":"Master选举：尝试创建master节点，失败就注册监听"},"5":{"objectClass":"MindNode","ID":"B8922","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"62B48","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"CU7Y4","lineColorHex":"#DC306C","text":""},"objectClass":"NSArray"},"text":"独占锁：争抢创建锁，失败则注册Watcher"},"1":{"objectClass":"MindNode","ID":"Q76V8","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"0GK7N","lineColorHex":"#DC306C","text":"1.创建临时节点"},"1":{"objectClass":"MindNode","ID":"GQ8B4","lineColorHex":"#DC306C","text":"2.判断自己节点的序号"},"2":{"objectClass":"MindNode","ID":"23MU0","lineColorHex":"#DC306C","text":"3.如果前面有比自己小的写节点，排队并向前一个节点注册监听"},"objectClass":"NSArray"},"text":"共享锁"},"objectClass":"NSArray"},"text":"分布式锁"},"6":{"objectClass":"MindNode","ID":"O3822","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"3RNV9","lineColorHex":"#DC306C","text":"1.getChildren获取所有任务节点"},"1":{"objectClass":"MindNode","ID":"6TBQH","lineColorHex":"#DC306C","text":"判断自己是否最小序号"},"2":{"objectClass":"MindNode","ID":"Z14WL","lineColorHex":"#DC306C","text":"如果不是，向前一个序号节点注册监听"},"objectClass":"NSArray"},"text":"分布式队列（类似全是写请求的共享锁）"},"7":{"objectClass":"MindNode","ID":"49J28","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"C404B","lineColorHex":"#DC306C","text":"YARN HA，ResourceManager主备切换"},"1":{"objectClass":"MindNode","ID":"561YT","lineColorHex":"#DC306C","text":"HDFS HA，NameNode主备切换"},"2":{"objectClass":"MindNode","ID":"3KV3S","lineColorHex":"#DC306C","text":"脑裂，ACL解决"},"objectClass":"NSArray"},"text":"hadoop应用"},"8":{"objectClass":"MindNode","ID":"7LMHZ","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"LBSWT","lineColorHex":"#DC306C","text":"管理和监听RegionServer"},"1":{"objectClass":"MindNode","ID":"1P1UX","lineColorHex":"#DC306C","text":"元数据管理，数据存储位置和Regionserver的映射信息"},"2":{"objectClass":"MindNode","ID":"ASRI7","lineColorHex":"#DC306C","text":"Replication,记录副本信息"},"3":{"objectClass":"MindNode","ID":"J4P0O","lineColorHex":"#DC306C","text":"HMaster主备切换"},"objectClass":"NSArray"},"text":"hbase应用"},"9":{"objectClass":"MindNode","ID":"A3324","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"23213","lineColorHex":"#DC306C","text":"Broker注册，监听Broker状态"},"1":{"objectClass":"MindNode","ID":"YO08K","lineColorHex":"#DC306C","text":"Topic注册，保存分区和Broker的关系"},"2":{"objectClass":"MindNode","ID":"10W14","lineColorHex":"#DC306C","text":"生产者负载均衡，监听Broker的状态，topic的状态等。"},"3":{"objectClass":"MindNode","ID":"H1E55","lineColorHex":"#DC306C","text":"消费者负载均衡，记录每个消费者消费分区的offset,当消费者变化可以继续从上次位置消费"},"objectClass":"NSArray"},"text":"kafka应用"},"10":{"objectClass":"MindNode","ID":"0G3N2","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"ED47I","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"5BCHL","lineColorHex":"#DC306C","text":"标记request注册监听"},"1":{"objectClass":"MindNode","ID":"585E9","lineColorHex":"#DC306C","text":"封装WatchRegistration对象并注册到ZKWatcherManager"},"objectClass":"NSArray"},"text":"客户端注册Watcher"},"1":{"objectClass":"MindNode","ID":"TB9FA","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"Z57LT","lineColorHex":"#DC306C","text":"保存ServerCnxn对象，看做Watcher对象，触发Watcher"},"1":{"objectClass":"MindNode","ID":"15V6B","lineColorHex":"#DC306C","text":"轻量级，只告诉客户端事件类型，通知状态和节点路径"},"2":{"objectClass":"MindNode","ID":"7I903","lineColorHex":"#DC306C","text":"触发是一次性的"},"objectClass":"NSArray"},"text":"服务端处理Watcher"},"2":{"objectClass":"MindNode","ID":"33JQ3","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"1BOQ3","lineColorHex":"#DC306C","text":"SendThread接收事件通知，EvenThread回调Watcher"},"1":{"objectClass":"MindNode","ID":"2P3IW","lineColorHex":"#DC306C","text":"串行执行"},"objectClass":"NSArray"},"text":"客户端回调Watcher"},"objectClass":"NSArray"},"text":"Watcher工作机制"},"11":{"objectClass":"MindNode","ID":"7CK99","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"X7UNE","lineColorHex":"#DC306C","text":"服务器状态：LOOKING(寻找leader状态),FOLOLOWING（跟随者状态）,LEADING（领导者状态）,OBSERVING（观察者状态）"},"1":{"objectClass":"MindNode","ID":"1D2FX","lineColorHex":"#DC306C","text":"第一次投票：<myid, ZXID(事务的id)，state(服务器状态), peerEpoch（投票轮次）>"},"2":{"objectClass":"MindNode","ID":"7TP23","lineColorHex":"#DC306C","text":"接收其他服务器的投票"},"3":{"objectClass":"MindNode","ID":"TJG5Z","lineColorHex":"#DC306C","text":"比对并变更投票"},"4":{"objectClass":"MindNode","ID":"8C8OU","lineColorHex":"#DC306C","text":"统计投票，半数原则"},"5":{"objectClass":"MindNode","ID":"14R8R","lineColorHex":"#DC306C","text":"改变服务器状态"},"objectClass":"NSArray"},"text":"Leader选举"},"12":{"objectClass":"MindNode","ID":"YD3W7","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"GGXKT","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"S5463","lineColorHex":"#DC306C","text":"DataTree，内存中完整数据"},"1":{"objectClass":"MindNode","ID":"TH8B7","lineColorHex":"#DC306C","text":"DataNode，存储最小单元：节点状态，ACL,数据内容和父子节点等。"},"objectClass":"NSArray"},"text":"内存数据"},"1":{"objectClass":"MindNode","ID":"HM0PB","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"T3H07","lineColorHex":"#DC306C","text":"数据变更操作之前写事务日志，然后响应客户端"},"objectClass":"NSArray"},"text":"事务日志"},"2":{"objectClass":"MindNode","ID":"4AG4L","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"U12E6","lineColorHex":"#DC306C","text":"zk在若干次事务日志记录后出发数据快照，通过snapCount配置"},"objectClass":"NSArray"},"text":"数据快照"},"objectClass":"NSArray"},"text":"数据存储"},"13":{"objectClass":"MindNode","ID":"02414","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"UY8U6","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"JVJ3J","lineColorHex":"#DC306C","text":"IP,通过IP粒度来控制，如192.168.*"},"1":{"objectClass":"MindNode","ID":"OH65X","lineColorHex":"#DC306C","text":"Digest,最常用权限模式，类似“username:password”的标识进行权限配置"},"2":{"objectClass":"MindNode","ID":"U6774","lineColorHex":"#DC306C","text":"World,最开放权限模式，表示对所有用户开放。可以看做特殊的Digest模式：“world:anyone”"},"3":{"objectClass":"MindNode","ID":"VV14E","lineColorHex":"#DC306C","text":"Super,可以对任何节点进行任何操作"},"objectClass":"NSArray"},"text":"权限模式"},"1":{"objectClass":"MindNode","ID":"C35WI","lineColorHex":"#DC306C","text":"授权对象，指权限赋予的用户或者一个实体，如IP地址或者机器等，在不同权限模式下授权对象是不同的"},"2":{"objectClass":"MindNode","ID":"12S2J","lineColorHex":"#DC306C","text":"权限，CREATE, DELETE,READ,WRITE,ADMIN(允许ACL相关操作)"},"objectClass":"NSArray"},"text":"ACL"},"objectClass":"NSArray"},"text":"zookeeper"},"2":{"objectClass":"MindNode","ID":"7FP56","lineColorHex":"#DC306C","text":"redis"},"3":{"objectClass":"MindNode","ID":"6YV84","lineColorHex":"#DC306C","text":"kafka"},"4":{"objectClass":"MindNode","ID":"GFJEH","lineColorHex":"#DC306C","text":"solr或者es"},"objectClass":"NSArray"},"text":"数据库和中间件"},"1":{"objectClass":"MindNode","ID":"YM40W","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"9V9Z9","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"ZWP8L","lineColorHex":"#DC306C","text":"UDP和TCP"},"1":{"objectClass":"MindNode","ID":"02825","lineColorHex":"#DC306C","text":"HTTP和HTTPS"},"2":{"objectClass":"MindNode","ID":"U2U62","lineColorHex":"#DC306C","text":"FTP和SFTP"},"3":{"objectClass":"MindNode","ID":"5TP03","lineColorHex":"#DC306C","text":"BIO和NIO"},"4":{"objectClass":"MindNode","ID":"5H6Q3","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"491HO","lineColorHex":"#DC306C","text":"select"},"1":{"objectClass":"MindNode","ID":"2U875","lineColorHex":"#DC306C","text":"poll"},"2":{"objectClass":"MindNode","ID":"1432C","lineColorHex":"#DC306C","text":"epoll"},"objectClass":"NSArray"},"text":"多路复用"},"objectClass":"NSArray"},"text":"计算机基础"},"1":{"objectClass":"MindNode","ID":"1PX6B","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"7R84T","lineColorHex":"#DC306C","text":"SkipList"},"1":{"objectClass":"MindNode","ID":"UMTV6","lineColorHex":"#DC306C","text":"BitMap"},"2":{"objectClass":"MindNode","ID":"41A8V","lineColorHex":"#DC306C","text":"Geo"},"objectClass":"NSArray"},"text":"数据结构"},"2":{"objectClass":"MindNode","ID":"QXW0G","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"JAY41","lineColorHex":"#DC306C","text":"堆"},"1":{"objectClass":"MindNode","ID":"QRY41","lineColorHex":"#DC306C","text":"栈"},"2":{"objectClass":"MindNode","ID":"Q9RPC","lineColorHex":"#DC306C","text":"二叉树"},"3":{"objectClass":"MindNode","ID":"5P8SN","lineColorHex":"#DC306C","text":"链表"},"4":{"objectClass":"MindNode","ID":"Z85AC","lineColorHex":"#DC306C","text":"排序"},"5":{"objectClass":"MindNode","ID":"W586S","lineColorHex":"#DC306C","text":"动态规划"},"6":{"objectClass":"MindNode","ID":"W3JKH","lineColorHex":"#DC306C","text":"贪心"},"7":{"objectClass":"MindNode","ID":"53V3B","lineColorHex":"#DC306C","text":"分治"},"objectClass":"NSArray"},"text":"算法"},"objectClass":"NSArray"},"text":"计算机基础，数据结构和算法"},"2":{"objectClass":"MindNode","ID":"OL420","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"78E81","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"3L6K4","lineColorHex":"#26BBFF","text":"hdfs"},"1":{"objectClass":"MindNode","ID":"2KZ7F","lineColorHex":"#26BBFF","text":"mapreduce"},"2":{"objectClass":"MindNode","ID":"X5B9U","lineColorHex":"#26BBFF","text":"hive"},"3":{"objectClass":"MindNode","ID":"5Z65B","lineColorHex":"#26BBFF","text":"hbase"},"4":{"objectClass":"MindNode","ID":"04827","lineColorHex":"#26BBFF","text":"yarn"},"objectClass":"NSArray"},"text":"hadoop"},"1":{"objectClass":"MindNode","ID":"IW2CU","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"36BKN","lineColorHex":"#26BBFF","text":"spark core"},"1":{"objectClass":"MindNode","ID":"1QP6B","lineColorHex":"#26BBFF","text":"spark sql"},"objectClass":"NSArray"},"text":"spark"},"2":{"objectClass":"MindNode","ID":"R83YL","lineColorHex":"#26BBFF","text":"flink"},"objectClass":"NSArray"},"text":"大数据"},"3":{"objectClass":"MindNode","ID":"0UB8Q","lineColorHex":"#37C45A","children":{"0":{"objectClass":"MindNode","ID":"TU9U5","lineColorHex":"#37C45A","text":"solea"},"1":{"objectClass":"MindNode","ID":"BJ47H","lineColorHex":"#37C45A","text":"titan"},"2":{"objectClass":"MindNode","ID":"13M2R","lineColorHex":"#37C45A","text":"datacenter"},"objectClass":"NSArray"},"text":"项目"},"4":{"objectClass":"MindNode","ID":"4951E","lineColorHex":"#1BD6E7","children":{"0":{"objectClass":"MindNode","ID":"N4263","lineColorHex":"#1BD6E7","text":"GC activity :90% ，导致进程运行特别慢"},"1":{"objectClass":"MindNode","ID":"8T1J8","lineColorHex":"#1BD6E7","text":"hbase region 分裂或者刷新导致有延迟，regionMoveException"},"objectClass":"NSArray"},"text":"遇到的问题"},"objectClass":"NSArray"},"text":"大数据"},"ID":"7L58F"},"objectClass":"NSArray"}}