//请判断一个链表是否为回文链表。 
//
// 示例 1: 
//
// 输入: 1->2
//输出: false 
//
// 示例 2: 
//
// 输入: 1->2->2->1
//输出: true
// 
//
// 进阶： 
//你能否用 O(n) 时间复杂度和 O(1) 空间复杂度解决此题？ 
// Related Topics 栈 递归 链表 双指针 
// 👍 1048 👎 0

package editor.cn;
public class PalindromeLinkedList {
    public static void main(String[] args) {
        Solution solution = new PalindromeLinkedList().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {

    public boolean isPalindrome(ListNode head) {
      // return solution1_stack(head);
      return solution2_doublePointer(head);
    }

    public boolean solution2_doublePointer(ListNode head){
      /**
       * 双指针法
       *  1.找出链表的前半部分的尾节点
       *  2.翻转后半部分节点
       *  3.前后比较
       *  4.还原后半部分的尾节点
       */

      ListNode firstHalfEnd = findEndOfFirstHalf(head);
      ListNode secondHalfStart = reverseNodes(firstHalfEnd.next);

      ListNode p1 = head;
      ListNode p2 = secondHalfStart;
      while(p1 != null && p2 != null){
        if(p1.val == p2.val){
          p1 = p1.next;
          p2 = p2.next;
          continue;
        }else{
          return false;
        }

      }

      // 还原
      reverseNodes(secondHalfStart);
      return true;
      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(1)
       */
    }

    public boolean solution1_stack(ListNode head){

      Stack<Integer> stack = new Stack();
      ListNode next = head;
      while(next != null){
        stack.push(next.val);
        next = next.next;
      }

      next = head; // 重置
      while(next != null){
        if(!stack.isEmpty() && stack.pop() == next.val){
          next = next.next;
          continue;
        }else{
          return false;
        }

      }
      return true;
      /**
       * 栈实现
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       *
       */
    }

    private ListNode findEndOfFirstHalf(ListNode head){
      ListNode fast = head;
      ListNode slow = head;
      while(fast != null){
        if((fast = fast.next) != null && fast.next != null){
          fast = fast.next;
          slow = slow.next;
        }
      }
      return slow;
    }

    // 反转链表
    private ListNode reverseNodes(ListNode head){
      // 遍历法反转链表
      ListNode current = head;
      ListNode prev = null;
      while(current != null){
        ListNode next = current.next;
        current.next = prev;
        prev = current;
        current = next;
      }
      return prev;
    }

}
//leetcode submit region end(Prohibit modification and deletion)

}