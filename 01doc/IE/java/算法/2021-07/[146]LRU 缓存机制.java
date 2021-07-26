//运用你所掌握的数据结构，设计和实现一个 LRU (最近最少使用) 缓存机制 。 
//
// 
// 
// 实现 LRUCache 类： 
//
// 
// LRUCache(int capacity) 以正整数作为容量 capacity 初始化 LRU 缓存 
// int get(int key) 如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1 。 
// void put(int key, int value) 如果关键字已经存在，则变更其数据值；如果关键字不存在，则插入该组「关键字-值」。当缓存容量达到上
//限时，它应该在写入新数据之前删除最久未使用的数据值，从而为新的数据值留出空间。 
// 
//
// 
// 
// 
//
// 进阶：你是否可以在 O(1) 时间复杂度内完成这两种操作？ 
//
// 
//
// 示例： 
//
// 
//输入
//["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
//[[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
//输出
//[null, null, null, 1, null, -1, null, -1, 3, 4]
//
//解释
//LRUCache lRUCache = new LRUCache(2);
//lRUCache.put(1, 1); // 缓存是 {1=1}
//lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
//lRUCache.get(1);    // 返回 1
//lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
//lRUCache.get(2);    // 返回 -1 (未找到)
//lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
//lRUCache.get(1);    // 返回 -1 (未找到)
//lRUCache.get(3);    // 返回 3
//lRUCache.get(4);    // 返回 4
// 
//
// 
//
// 提示： 
//
// 
// 1 <= capacity <= 3000 
// 0 <= key <= 10000 
// 0 <= value <= 105 
// 最多调用 2 * 105 次 get 和 put 
// 
// Related Topics 设计 哈希表 链表 双向链表 
// 👍 1516 👎 0

package editor.cn;
public class LruCache {
    public static void main(String[] args) {
        Solution solution = new LruCache().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
  /**
   * 通过HashMap和自定义链表实现
   * @param capacity
   * 时间复杂度：O(1)
   * 空间复杂度：O(n)
   */
class LRUCache {

    private Map<Integer, Node> cache;
    private Node head, tail;
    private int capacity;

    public LRUCache(int capacity) {
      this.capacity = capacity;
      cache = new HashMap(capacity);
      head = new Node(-1, -1);
      tail = new Node(-1, -1);
      head.next = tail;
      tail.prev = head;
    }
    
    public int get(int key) {
      Node node = cache.get(key);
      if(node != null){
        afterNodeAccess(node);
      }
      return node == null ? -1 : node.value;
    }
    
    public void put(int key, int value) {
      Node node = cache.get(key);
      if(node == null){
        if(cache.size() >= capacity){
          beforeNodeInsertion();
        }

        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        appendToHead(newNode);
      }else{
        node.value = value;
        moveToHead(node);
      }

    }

    private void moveToHead(Node node){
      removeNode(node);
      appendToHead(node);
    }

    private void removeEldest(){
      Node eldest = tail.prev;
      cache.remove(eldest.key);
      removeNode(eldest);
    }

    private void removeNode(Node node){
      if(node == tail || node == head){
        return;
      }
      node.prev.next = node.next;
      node.next.prev = node.prev;
      node.next = null;  // help GC
      node.prev = null;
    }

    private void appendToHead(Node node){
      Node first = head.next;
      first.prev = node;
      node.next = first;
      head.next = node;
      node.prev = head;
    }

    private void appendToTail(Node node){
      Node last = tail.prev;
      last.next = node;
      node.prev = last;
      node.next = tail;
      tail.prev = node;
    }

    private void beforeNodeInsertion(Node node){
      removeEldest();
    }

    private void afterNodeAccess(Node node){
      removeNode(node);
      appendToHead(node);
    }

    class Node{
      int key;
      int value;
      Node prev, next;
      public Node(){}
      public Node(int key, int value){
        this.key = key;
        this.value = value;
      }

    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
//leetcode submit region end(Prohibit modification and deletion)

}