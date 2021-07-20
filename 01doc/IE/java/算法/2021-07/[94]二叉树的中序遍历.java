//给定一个二叉树的根节点 root ，返回它的 中序 遍历。 
//
// 
//
// 示例 1： 
//
// 
//输入：root = [1,null,2,3]
//输出：[1,3,2]
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
//输入：root = [1]
//输出：[1]
// 
//
// 示例 4： 
//
// 
//输入：root = [1,2]
//输出：[2,1]
// 
//
// 示例 5： 
//
// 
//输入：root = [1,null,2]
//输出：[1,2]
// 
//
// 
//
// 提示： 
//
// 
// 树中节点数目在范围 [0, 100] 内 
// -100 <= Node.val <= 100 
// 
//
// 
//
// 进阶: 递归算法很简单，你可以通过迭代算法完成吗？ 
// Related Topics 栈 树 哈希表 
// 👍 981 👎 0

package editor.cn;
public class BinaryTreeInorderTraversal {
    public static void main(String[] args) {
        Solution solution = new BinaryTreeInorderTraversal().new Solution();
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
  List<Integer> nodes = new ArrayList();
    public List<Integer> inorderTraversal(TreeNode root) {

/*    递归方式：
      if(root == null){
        return nodes;
      }

      inorderTraversal(root.left);
      nodes.add(root.val);
      inorderTraversal(root.right);*/

      // 迭代方式（栈）：
      // 用来临时保存走过的节点
      Stack<TreeNode> stack = new Stack();
      while(root != null || !stack.isEmpty()){

        while(root != null){
          // 一直往左遍历
          stack.push(root);
          root = root.left;
        }

        if(!stack.isEmpty()){
          root = stack.pop();
          nodes.add(root.val);
          root = root.right;
        }

      }

      return nodes;
    }
  /**
   * 时间复杂度：O(n), 空间复杂度：O(n)
   *
   */

}
//leetcode submit region end(Prohibit modification and deletion)

}