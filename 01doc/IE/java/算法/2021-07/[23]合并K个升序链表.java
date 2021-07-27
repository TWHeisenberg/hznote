//给你一个链表数组，每个链表都已经按升序排列。 
//
// 请你将所有链表合并到一个升序链表中，返回合并后的链表。 
//
// 
//
// 示例 1： 
//
// 输入：lists = [[1,4,5],[1,3,4],[2,6]]
//输出：[1,1,2,3,4,4,5,6]
//解释：链表数组如下：
//[
//  1->4->5,
//  1->3->4,
//  2->6
//]
//将它们合并到一个有序链表中得到。
//1->1->2->3->4->4->5->6
// 
//
// 示例 2： 
//
// 输入：lists = []
//输出：[]
// 
//
// 示例 3： 
//
// 输入：lists = [[]]
//输出：[]
// 
//
// 
//
// 提示： 
//
// 
// k == lists.length 
// 0 <= k <= 10^4 
// 0 <= lists[i].length <= 500 
// -10^4 <= lists[i][j] <= 10^4 
// lists[i] 按 升序 排列 
// lists[i].length 的总和不超过 10^4 
// 
// Related Topics 链表 分治 堆（优先队列） 归并排序 
// 👍 1408 👎 0

package editor.cn;
public class MergeKSortedLists {
    public static void main(String[] args) {
        Solution solution = new MergeKSortedLists().new Solution();
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
  public ListNode mergeKLists(ListNode[] lists) {
    /**
     * 分治
     *
     */

    return mergeSort(0,  lists.length - 1, lists);
  }

  private ListNode mergeSort(int left, int right, ListNode[] lists){
    if(left == right){
      return lists[left];
    }

    if(left > right){
      return null;
    }

    int middle = (left + right) >> 1;
    return merge(mergeSort(left, middle, lists), mergeSort(middle + 1, right, lists));

    /**
     * 时间复杂度： O(kn×logk) k是数组长度，n是链表长度
     * 空间复杂度：O(logk)
     */

  }

  private ListNode merge(ListNode left, ListNode right){
    ListNode head = new ListNode();
    ListNode p = head;
    while(left != null && right != null){
      if(left.val < right.val){
        p.next = left;
        left = left.next;
      }else{
        p.next = right;
        right = right.next;
      }
      p = p.next;
    }

    // 最多只有一个链表没有合完
    p.next = left == null ? right : left;

    return head.next;
  }
}
//leetcode submit region end(Prohibit modification and deletion)

}