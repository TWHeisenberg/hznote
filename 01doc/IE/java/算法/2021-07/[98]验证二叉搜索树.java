//给定一个二叉树，判断其是否是一个有效的二叉搜索树。 
//
// 假设一个二叉搜索树具有如下特征： 
//
// 
// 节点的左子树只包含小于当前节点的数。 
// 节点的右子树只包含大于当前节点的数。 
// 所有左子树和右子树自身必须也是二叉搜索树。 
// 
//
// 示例 1: 
//
// 输入:
//    2
//   / \
//  1   3
//输出: true
// 
//
// 示例 2: 
//
// 输入:
//    5
//   / \
//  1   4
//     / \
//    3   6
//输出: false
//解释: 输入为: [5,1,4,null,null,3,6]。
//     根节点的值为 5 ，但是其右子节点值为 4 。
// 
// Related Topics 树 深度优先搜索 二叉搜索树 二叉树 
// 👍 1144 👎 0

package editor.cn;
public class ValidateBinarySearchTree {
    public static void main(String[] args) {
        Solution solution = new ValidateBinarySearchTree().new Solution();
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
    public boolean isValidBST(TreeNode root) {
      /**
       * 中序遍历，判断是否递增
       *
       */

      return depthSearch(root);
    }

    private boolean depthSearch(TreeNode root){

      Deque<TreeNode> stack = new LinkedList();
      Deque<Integer> minStack = new LinkedList();

      while(root != null || !stack.isEmpty()){

        while(root != null){
          stack.push(root);
          root = root.left;
        }

        if(!stack.isEmpty()){
          root = stack.pop();
          // add
          if(minStack.isEmpty()){
            minStack.push(root.val);
          }else{
            if(root.val <= minStack.peek()){
              return false;
            }
            minStack.push(root.val);
          }
          // right
          root = root.right;
        }
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