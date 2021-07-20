//给你二叉树的根结点 root ，请你将它展开为一个单链表： 
//
// 
// 展开后的单链表应该同样使用 TreeNode ，其中 right 子指针指向链表中下一个结点，而左子指针始终为 null 。 
// 展开后的单链表应该与二叉树 先序遍历 顺序相同。 
// 
//
// 
//
// 示例 1： 
//
// 
//输入：root = [1,2,5,3,4,null,6]
//输出：[1,null,2,null,3,null,4,null,5,null,6]
// 
//
// 示例 2： 
//
// 
//输入：root = []
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：root = [0]
//输出：[0]
// 
//
// 
//
// 提示： 
//
// 
// 树中结点数在范围 [0, 2000] 内 
// -100 <= Node.val <= 100 
// 
//
// 
//
// 进阶：你可以使用原地算法（O(1) 额外空间）展开这棵树吗？ 
// Related Topics 栈 树 深度优先搜索 链表 二叉树 
// 👍 861 👎 0

package editor.cn;
public class FlattenBinaryTreeToLinkedList {
    public static void main(String[] args) {
        Solution solution = new FlattenBinaryTreeToLinkedList().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public void flatten(TreeNode root) {
      solution1_preOrderd(root);
    }

    private void solution1_preOrderd(TreeNode root){
      /**
       * 递归前序遍历,保存前序顺序的节点到list
       * 然后遍历list,转为链表
       */
      List<TreeNode> lists = new ArrayList();
      preOrderdRecursion(root, lists);

      for(int i = 1;i < lists.size();i++){
        TreeNode prev = lists.get(i-1), current = lists.get(i);
        prev.left = null;
        prev.right = current;
      }

      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       */
    }

    private void preOrderdRecursion(TreeNode root, List nodes){
      if(root == null){
        return;
      }
      nodes.add(root);
      preOrderdRecursion(root.left, nodes);
      preOrderdRecursion(root.right, nodes);
    }
}
//leetcode submit region end(Prohibit modification and deletion)


}


}