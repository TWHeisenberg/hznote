//给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。 
//
// 请你将两个数相加，并以相同形式返回一个表示和的链表。 
//
// 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。 
//
// 
//
// 示例 1： 
//
// 
//输入：l1 = [2,4,3], l2 = [5,6,4]
//输出：[7,0,8]
//解释：342 + 465 = 807.
// 
//
// 示例 2： 
//
// 
//输入：l1 = [0], l2 = [0]
//输出：[0]
// 
//
// 示例 3： 
//
// 
//输入：l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
//输出：[8,9,9,9,0,0,0,1]
// 
//
// 
//
// 提示： 
//
// 
// 每个链表中的节点数在范围 [1, 100] 内 
// 0 <= Node.val <= 9 
// 题目数据保证列表表示的数字不含前导零 
// 
// Related Topics 递归 链表 数学 
// 👍 6493 👎 0

package editor.cn;
public class AddTwoNumbers {
    public static void main(String[] args) {
        Solution solution = new AddTwoNumbers().new Solution();
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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

      ListNode p1 = l1, p2 = l2;
      ListNode head = new ListNode();
      ListNode current = head;

      int carry = 0;
      while(p1 != null || p2 != null || carry != 0){
        int number1 = p1 == null ? 0 : p1.val;
        int number2 = p2 == null ? 0 : p2.val;

        int sum = number1 + number2 + carry;
        carry = sum / 10;
        current.next = new ListNode(sum % 10);
        current = current.next;

        if(p1 != null){
          p1 = p1.next;
        }
        if(p2 != null){
          p2 = p2.next;
        }
      }

      return head.next;
      /**
       * 时间复杂度：O(max(m, n))
       * 空间复杂度：O(1) 返回值不计入空间复杂度
       *
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}