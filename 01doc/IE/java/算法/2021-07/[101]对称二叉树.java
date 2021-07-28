//给定一个二叉树，检查它是否是镜像对称的。 
//
// 
//
// 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。 
//
//     1
//   / \
//  2   2
// /     \
//3       3
// 
//
// 
//
// 但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的: 
//
//     1
//   / \
//  2   2
//   \   \
//   3    3
// 
//
// 
//
// 进阶： 
//
// 你可以运用递归和迭代两种方法解决这个问题吗？ 
// Related Topics 树 深度优先搜索 广度优先搜索 二叉树 
// 👍 1460 👎 0

package editor.cn;

import java.util.concurrent.DelayQueue;

public class SymmetricTree {
    public static void main(String[] args) {
        Solution solution = new SymmetricTree().new Solution();
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
    public boolean isSymmetric(TreeNode root) {

      return solution2_iteration(root);
    }

    private boolean solution1_recursion(TreeNode root){
      return check(root.left, root.right);
    }
    private boolean check(TreeNode root1, TreeNode root2){
      if(root1 == null || root2 == null){
        return root1 == root2;
      }

      return root1.val == root2.val && check(root1.left, root2.right) && check(root1.right, root2.left);

      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       *
       */
    }

    private boolean solution2_iteration(TreeNode root){

      Deque<TreeNode> queue = new LinkedList();
      queue.offer(root.left);
      queue.offer(root.right);

      while(!queue.isEmpty()){
        TreeNode node1 = queue.poll(), node2 = queue.poll();
        if(node1 == null || node2 == null){
          if(node1 != node2){
            return false;
          }else{
            continue;
          }
        }
          if(node1.val != node2.val){
            return false;
          }

          queue.offer(node1.left);
          queue.offer(node2.right);

          queue.offer(node1.right);
          queue.offer(node2.left);
      }
      return true;

      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       */
    }

}
//leetcode submit region end(Prohibit modification and deletion)

}