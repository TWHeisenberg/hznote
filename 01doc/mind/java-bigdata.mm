{"objectClass":"NSDictionary","root":{"objectClass":"MindNode","ID":"1K3CZ","rootPoint":{"objectClass":"CGPoint","x":360,"y":2622},"lineColorHex":"#BBBBBB","children":{"0":{"objectClass":"MindNode","ID":"L6E7T","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"1NU8T","lineColorHex":"#DC306C","text":"线程之间如何通信"},"1":{"objectClass":"MindNode","ID":"J1Y6S","lineColorHex":"#DC306C","text":"实现一个生产者和消费者模型"},"2":{"objectClass":"MindNode","ID":"B45H0","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"31R4Z","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"74SUZ","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"Y1F54","lineColorHex":"#DC306C","text":"Mark Word, 存储对象的HashCode,锁标志和分代年龄"},"1":{"objectClass":"MindNode","ID":"67927","lineColorHex":"#DC306C","text":"Class Pointer,对象指向它的类元数据的指针，虚拟机通过这个指针来确定对象是哪个类的实例"},"2":{"objectClass":"MindNode","ID":"9RDG6","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"2E91Y","lineColorHex":"#DC306C","text":"EntryList"},"1":{"objectClass":"MindNode","ID":"WLHX4","lineColorHex":"#DC306C","text":"Owner,指向持有Monitor对象的线程"},"2":{"objectClass":"MindNode","ID":"8177V","lineColorHex":"#DC306C","text":"WaitSet"},"objectClass":"NSArray"},"text":"Monitor"},"objectClass":"NSArray"},"text":"对象头"},"1":{"objectClass":"MindNode","ID":"3LY1D","lineColorHex":"#DC306C","text":"实例数据"},"2":{"objectClass":"MindNode","ID":"AEMRU","lineColorHex":"#DC306C","text":"对其填充"},"objectClass":"NSArray"},"text":"对象"},"1":{"objectClass":"MindNode","ID":"54332","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"S331T","lineColorHex":"#DC306C","text":"代码块：锁住的是括号内的对象， 通过monitorenter和monitorexit锁对象，程序计数器count"},"1":{"objectClass":"MindNode","ID":"C103F","lineColorHex":"#DC306C","text":"方法上：锁住的是当前调用对象,ACC_SYNCHRONIZED"},"2":{"objectClass":"MindNode","ID":"AGK88","lineColorHex":"#DC306C","text":"静态方法：这个类对象，而不是实例对象"},"objectClass":"NSArray"},"text":"使用"},"2":{"objectClass":"MindNode","ID":"8J552","lineColorHex":"#DC306C","text":"锁升级过程：偏向锁 -> 轻量级锁（乐观锁） -> 重量级锁"},"objectClass":"NSArray"},"text":"synchronized"},"3":{"objectClass":"MindNode","ID":"BSG2S","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"P2L8B","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"5UGJ8","lineColorHex":"#DC306C","text":"get"},"1":{"objectClass":"MindNode","ID":"C3V11","lineColorHex":"#DC306C","text":"put"},"objectClass":"NSArray"},"text":"实现"},"1":{"objectClass":"MindNode","ID":"MZ16X","lineColorHex":"#DC306C","text":"内存泄露"},"2":{"objectClass":"MindNode","ID":"757T9","lineColorHex":"#DC306C","text":"InheritableThreadLocal"},"3":{"objectClass":"MindNode","ID":"IH33R","lineColorHex":"#DC306C","text":"用来解决什么问题，为什么使用"},"objectClass":"NSArray"},"text":"ThreadLocal"},"4":{"objectClass":"MindNode","ID":"4D163","lineColorHex":"#DC306C","text":"volatile"},"5":{"objectClass":"MindNode","ID":"NB7M6","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"56112","lineColorHex":"#DC306C","text":"Lock"},"1":{"objectClass":"MindNode","ID":"4O714","lineColorHex":"#DC306C","text":"CountDownLatch"},"2":{"objectClass":"MindNode","ID":"86B16","lineColorHex":"#DC306C","text":"CyclicBarrier"},"objectClass":"NSArray"},"text":"JUC"},"6":{"objectClass":"MindNode","ID":"PVY47","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"27745","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"NFR47","lineColorHex":"#DC306C","text":"CachedThreadPool"},"1":{"objectClass":"MindNode","ID":"56LNL","lineColorHex":"#DC306C","text":"FixedThreadPool"},"2":{"objectClass":"MindNode","ID":"3051P","lineColorHex":"#DC306C","text":"SingleThreadPool"},"3":{"objectClass":"MindNode","ID":"SY32N","lineColorHex":"#DC306C","text":"ScheduledThreadPool"},"objectClass":"NSArray"},"text":"4种线程池"},"1":{"objectClass":"MindNode","ID":"KJ6YX","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"3YDCG","lineColorHex":"#DC306C","text":"corePoolSize"},"1":{"objectClass":"MindNode","ID":"H8176","lineColorHex":"#DC306C","text":"maximumPoolSize"},"2":{"objectClass":"MindNode","ID":"GE45Z","lineColorHex":"#DC306C","text":"keepAliveTime"},"3":{"objectClass":"MindNode","ID":"884IB","lineColorHex":"#DC306C","text":"TimeUnit"},"4":{"objectClass":"MindNode","ID":"3MRJV","lineColorHex":"#DC306C","text":"workQueue"},"5":{"objectClass":"MindNode","ID":"5L2G3","lineColorHex":"#DC306C","text":"threadFactory"},"6":{"objectClass":"MindNode","ID":"F4IYD","lineColorHex":"#DC306C","text":"RejectedExecutionHandler"},"objectClass":"NSArray"},"text":"7个参数"},"2":{"objectClass":"MindNode","ID":"T68K2","lineColorHex":"#DC306C","text":"如何优雅的指定线程名字"},"3":{"objectClass":"MindNode","ID":"95R86","lineColorHex":"#DC306C","text":"阿里不建议newCachedThreadPool等方法，建议自己构造原因"},"4":{"objectClass":"MindNode","ID":"CK7XC","lineColorHex":"#DC306C","text":"工厂方法"},"5":{"objectClass":"MindNode","ID":"U47LF","lineColorHex":"#DC306C","text":"创建线程几种方式"},"6":{"objectClass":"MindNode","ID":"QN8LB","lineColorHex":"#DC306C","text":"线程池运行原理，状态"},"7":{"objectClass":"MindNode","ID":"12H34","lineColorHex":"#DC306C","text":"使用场景"},"8":{"objectClass":"MindNode","ID":"33IMO","lineColorHex":"#DC306C","text":"怎么尽可能提高多线程并发数"},"objectClass":"NSArray"},"text":"线程池"},"objectClass":"NSArray"},"text":"并发"},"1":{"objectClass":"MindNode","ID":"O2CH7","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"2GXVL","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"5B8O8","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"C78N8","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"26GLP","lineColorHex":"#BF58F5","text":"如何保证线程安全"},"objectClass":"NSArray"},"text":"ArrayList"},"1":{"objectClass":"MindNode","ID":"87Q31","lineColorHex":"#BF58F5","text":"LinkedList"},"objectClass":"NSArray"},"text":"List"},"1":{"objectClass":"MindNode","ID":"5L514","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"261F6","lineColorHex":"#BF58F5","text":"HashSet"},"1":{"objectClass":"MindNode","ID":"07PF4","lineColorHex":"#BF58F5","text":"TreeSet"},"objectClass":"NSArray"},"text":"Set"},"2":{"objectClass":"MindNode","ID":"GB293","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"6H262","lineColorHex":"#BF58F5","text":"LinkedBlockingQueue"},"objectClass":"NSArray"},"text":"Queue"},"objectClass":"NSArray"},"text":"Collection"},"1":{"objectClass":"MindNode","ID":"1LM23","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"KF67L","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"T4231","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"Y2I46","lineColorHex":"#BF58F5","text":"数组+链表"},"1":{"objectClass":"MindNode","ID":"A7S57","lineColorHex":"#BF58F5","text":"头插法"},"objectClass":"NSArray"},"text":"1.7"},"1":{"objectClass":"MindNode","ID":"42PZH","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"42328","lineColorHex":"#BF58F5","text":"数组+链表+红黑树"},"1":{"objectClass":"MindNode","ID":"5VGNX","lineColorHex":"#BF58F5","text":"尾插法"},"objectClass":"NSArray"},"text":"1.8"},"2":{"objectClass":"MindNode","ID":"82UL3","lineColorHex":"#BF58F5","text":"扩容机制：默认0.75"},"3":{"objectClass":"MindNode","ID":"1EYN1","lineColorHex":"#BF58F5","text":"capacity是2的幂"},"4":{"objectClass":"MindNode","ID":"Y0N23","lineColorHex":"#BF58F5","text":"put过程"},"5":{"objectClass":"MindNode","ID":"E22FL","lineColorHex":"#BF58F5","text":"get过程"},"objectClass":"NSArray"},"text":"HashMap"},"1":{"objectClass":"MindNode","ID":"43E4U","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"D5RW4","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"641V5","lineColorHex":"#BF58F5","text":"分段锁segment实现并发"},"1":{"objectClass":"MindNode","ID":"IPMUO","lineColorHex":"#BF58F5","text":"每个segment负责一段桶，通过给segment加锁保证线程安全"},"2":{"objectClass":"MindNode","ID":"6M5Y6","lineColorHex":"#BF58F5","text":"数组+链表"},"3":{"objectClass":"MindNode","ID":"8P45S","lineColorHex":"#BF58F5","text":"DEFAULT_CONCURRENCY_LEVEL默认16，支持多少线程并发写"},"objectClass":"NSArray"},"text":"jdk1.7及之前"},"1":{"objectClass":"MindNode","ID":"JNB67","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"YVWO4","lineColorHex":"#BF58F5","text":"CAS+syncronized实现线程安全"},"1":{"objectClass":"MindNode","ID":"QKGQ0","lineColorHex":"#BF58F5","text":"数组+链表+红黑树"},"2":{"objectClass":"MindNode","ID":"Y8U0P","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"546S6","lineColorHex":"#BF58F5","text":"第一次put,加载表，避免第一次操作就resize,如putAll"},"1":{"objectClass":"MindNode","ID":"B4J1S","lineColorHex":"#BF58F5","text":" 如果put位置桶空的，则通过CAS添加新节点"},"2":{"objectClass":"MindNode","ID":"9SF58","lineColorHex":"#BF58F5","text":" 如果put位置桶不为空，但hash状态==MOVED(-1),说明当前表正在进行resize,当前线程进入helpTransfer方法"},"3":{"objectClass":"MindNode","ID":"2UIY3","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"60PBO","lineColorHex":"#BF58F5","text":"判断是链表还是红黑树，用对应的方法去添加"},"1":{"objectClass":"MindNode","ID":"5J418","lineColorHex":"#BF58F5","text":"如果有相同key则覆盖"},"2":{"objectClass":"MindNode","ID":"66VRE","lineColorHex":"#BF58F5","text":"没有相同key,添加新节点到后面"},"objectClass":"NSArray"},"text":"如果put位置桶不为空，说明桶已经有人了，对当前的桶进行加锁"},"4":{"objectClass":"MindNode","ID":"W1725","lineColorHex":"#BF58F5","text":"调用addCount(int count, int check),count表示新增元素个数，check是链表的长度. 计算并且检查是否需要resize"},"objectClass":"NSArray"},"text":"put"},"3":{"objectClass":"MindNode","ID":"X8230","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"X677G","lineColorHex":"#BF58F5","text":"判断表不为空，并且状态是-1，正在转移,判断正在扩容，并且扩容线程是否满了"},"1":{"objectClass":"MindNode","ID":"Q762I","lineColorHex":"#BF58F5","text":"通过CAS设置扩容的线程数量+1"},"2":{"objectClass":"MindNode","ID":"6356Y","lineColorHex":"#BF58F5","text":"调用transfer方法，进行扩容"},"objectClass":"NSArray"},"text":"helpTransfer"},"4":{"objectClass":"MindNode","ID":"5ZQQT","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"RR947","lineColorHex":"#BF58F5","text":"根据cpu个数，配置的最小步数：16和表长度计算步长，即每次转移多少节点"},"1":{"objectClass":"MindNode","ID":"C4M9W","lineColorHex":"#BF58F5","text":"初始化一个长度为原来2倍的数组"},"2":{"objectClass":"MindNode","ID":"QWC21","lineColorHex":"#BF58F5","text":"进行转移"},"objectClass":"NSArray"},"text":"transfer"},"objectClass":"NSArray"},"text":"jdk1.8"},"objectClass":"NSArray"},"text":"ConcurrentHashMap"},"2":{"objectClass":"MindNode","ID":"O1W56","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"PBYX5","lineColorHex":"#BF58F5","text":"继承自HashMap，内部实现双向链表"},"1":{"objectClass":"MindNode","ID":"SK80P","lineColorHex":"#BF58F5","text":"两种排序方式，通过accessOrder选择，为true时实现LRU"},"2":{"objectClass":"MindNode","ID":"W4188","lineColorHex":"#BF58F5","children":{"0":{"objectClass":"MindNode","ID":"Y0LK1","lineColorHex":"#BF58F5","text":"void afterNodeAccess(Node<K,V> p) "},"1":{"objectClass":"MindNode","ID":"O71DW","lineColorHex":"#BF58F5","text":"void afterNodeInsertion(boolean evict)"},"2":{"objectClass":"MindNode","ID":"6G1D7","lineColorHex":"#BF58F5","text":"void afterNodeRemoval(Node<K,V> p)"},"3":{"objectClass":"MindNode","ID":"SXI6R","lineColorHex":"#BF58F5","text":"Node<K,V> newNode(int hash, K key, V value, Node<K,V> e"},"4":{"objectClass":"MindNode","ID":"7583B","lineColorHex":"#BF58F5","text":"void afterNodeRemoval(Node<K,V> e)"},"objectClass":"NSArray"},"text":"重写了HashMap的几个方法"},"objectClass":"NSArray"},"text":"LinkedHashMap"},"objectClass":"NSArray"},"text":"Map"},"objectClass":"NSArray"},"text":"集合"},"2":{"objectClass":"MindNode","ID":"831L8","lineColorHex":"#26BBFF","text":"jvm"},"3":{"objectClass":"MindNode","ID":"722M1","lineColorHex":"#37C45A","text":"io"},"4":{"objectClass":"MindNode","ID":"C45XW","lineColorHex":"#1BD6E7","text":"反射"},"objectClass":"NSArray"},"text":"java基础","style2":{"objectClass":"NSDictionary","fontSize":1,"strikethrough":0}},"ID":"235N2","style":1,"subjects":{"0":{"objectClass":"NSDictionary","root":{"objectClass":"MindNode","ID":"87JVB","rootPoint":{"objectClass":"CGPoint","x":360,"y":1136.5},"lineColorHex":"#BBBBBB","children":{"0":{"objectClass":"MindNode","ID":"8HW5Q","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"2I19P","lineColorHex":"#DC306C","text":"mysql"},"1":{"objectClass":"MindNode","ID":"5VTQ9","lineColorHex":"#DC306C","text":"zookeeper"},"2":{"objectClass":"MindNode","ID":"7FP56","lineColorHex":"#DC306C","text":"redis"},"3":{"objectClass":"MindNode","ID":"6YV84","lineColorHex":"#DC306C","text":"kafka"},"4":{"objectClass":"MindNode","ID":"GFJEH","lineColorHex":"#DC306C","text":"solr或者es"},"objectClass":"NSArray"},"text":"数据库和中间件"},"1":{"objectClass":"MindNode","ID":"YM40W","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"9V9Z9","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"ZWP8L","lineColorHex":"#DC306C","text":"UDP和TCP"},"1":{"objectClass":"MindNode","ID":"02825","lineColorHex":"#DC306C","text":"HTTP和HTTPS"},"2":{"objectClass":"MindNode","ID":"U2U62","lineColorHex":"#DC306C","text":"FTP和SFTP"},"3":{"objectClass":"MindNode","ID":"5TP03","lineColorHex":"#DC306C","text":"BIO和NIO"},"4":{"objectClass":"MindNode","ID":"5H6Q3","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"491HO","lineColorHex":"#DC306C","text":"select"},"1":{"objectClass":"MindNode","ID":"2U875","lineColorHex":"#DC306C","text":"poll"},"2":{"objectClass":"MindNode","ID":"1432C","lineColorHex":"#DC306C","text":"epoll"},"objectClass":"NSArray"},"text":"多路复用"},"objectClass":"NSArray"},"text":"计算机基础"},"1":{"objectClass":"MindNode","ID":"1PX6B","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"7R84T","lineColorHex":"#DC306C","text":"SkipList"},"1":{"objectClass":"MindNode","ID":"UMTV6","lineColorHex":"#DC306C","text":"BitMap"},"2":{"objectClass":"MindNode","ID":"41A8V","lineColorHex":"#DC306C","text":"Geo"},"objectClass":"NSArray"},"text":"数据结构"},"2":{"objectClass":"MindNode","ID":"QXW0G","lineColorHex":"#DC306C","children":{"0":{"objectClass":"MindNode","ID":"JAY41","lineColorHex":"#DC306C","text":"堆"},"1":{"objectClass":"MindNode","ID":"QRY41","lineColorHex":"#DC306C","text":"栈"},"2":{"objectClass":"MindNode","ID":"Q9RPC","lineColorHex":"#DC306C","text":"二叉树"},"3":{"objectClass":"MindNode","ID":"5P8SN","lineColorHex":"#DC306C","text":"链表"},"4":{"objectClass":"MindNode","ID":"Z85AC","lineColorHex":"#DC306C","text":"排序"},"5":{"objectClass":"MindNode","ID":"W586S","lineColorHex":"#DC306C","text":"动态规划"},"6":{"objectClass":"MindNode","ID":"W3JKH","lineColorHex":"#DC306C","text":"贪心"},"7":{"objectClass":"MindNode","ID":"53V3B","lineColorHex":"#DC306C","text":"分治"},"objectClass":"NSArray"},"text":"算法"},"objectClass":"NSArray"},"text":"计算机基础，数据结构和算法"},"2":{"objectClass":"MindNode","ID":"OL420","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"78E81","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"3L6K4","lineColorHex":"#26BBFF","text":"hdfs"},"1":{"objectClass":"MindNode","ID":"2KZ7F","lineColorHex":"#26BBFF","text":"mapreduce"},"2":{"objectClass":"MindNode","ID":"X5B9U","lineColorHex":"#26BBFF","text":"hive"},"3":{"objectClass":"MindNode","ID":"5Z65B","lineColorHex":"#26BBFF","text":"hbase"},"4":{"objectClass":"MindNode","ID":"04827","lineColorHex":"#26BBFF","text":"yarn"},"objectClass":"NSArray"},"text":"hadoop"},"1":{"objectClass":"MindNode","ID":"IW2CU","lineColorHex":"#26BBFF","children":{"0":{"objectClass":"MindNode","ID":"36BKN","lineColorHex":"#26BBFF","text":"spark core"},"1":{"objectClass":"MindNode","ID":"1QP6B","lineColorHex":"#26BBFF","text":"spark sql"},"objectClass":"NSArray"},"text":"spark"},"2":{"objectClass":"MindNode","ID":"R83YL","lineColorHex":"#26BBFF","text":"flink"},"objectClass":"NSArray"},"text":"大数据"},"3":{"objectClass":"MindNode","ID":"0UB8Q","lineColorHex":"#37C45A","children":{"0":{"objectClass":"MindNode","ID":"TU9U5","lineColorHex":"#37C45A","text":"solea"},"1":{"objectClass":"MindNode","ID":"BJ47H","lineColorHex":"#37C45A","text":"titan"},"2":{"objectClass":"MindNode","ID":"13M2R","lineColorHex":"#37C45A","text":"datacenter"},"objectClass":"NSArray"},"text":"项目"},"4":{"objectClass":"MindNode","ID":"4951E","lineColorHex":"#1BD6E7","children":{"0":{"objectClass":"MindNode","ID":"N4263","lineColorHex":"#1BD6E7","text":"GC activity :90% ，导致进程运行特别慢"},"1":{"objectClass":"MindNode","ID":"8T1J8","lineColorHex":"#1BD6E7","text":"hbase region 分裂或者刷新导致有延迟，regionMoveException"},"objectClass":"NSArray"},"text":"遇到的问题"},"objectClass":"NSArray"},"text":"大数据"},"ID":"7L58F"},"objectClass":"NSArray"}}