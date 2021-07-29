//给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。 
//
// 
//
// 示例： 
//二叉树：[3,9,20,null,null,15,7], 
//
// 
//    3
//   / \
//  9  20
//    /  \
//   15   7
// 
//
// 返回其层序遍历结果： 
//
// 
//[
//  [3],
//  [9,20],
//  [15,7]
//]
// 
// Related Topics 树 广度优先搜索 二叉树 
// 👍 943 👎 0

package editor.cn;
public class BinaryTreeLevelOrderTraversal {
    public static void main(String[] args) {
        Solution solution = new BinaryTreeLevelOrderTraversal().new Solution();
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
    public List<List<Integer>> levelOrder(TreeNode root) {
      List<List<Integer>> result = new ArrayList();
      if(root == null){
        return result;
      }
      /**
       * 使用先进先出的队列暂存每层的节点
       */

      Deque<TreeNode> queue = new LinkedList();
      queue.offer(root);

      while(!queue.isEmpty()){
        int size = queue.size();
        List<Integer> leveList = new ArrayList();
        for(int i = 0;i < size;i++){
          TreeNode node = queue.poll();
          if(node != null){
            leveList.add(node.val);
          }
          if(node.left != null){
            queue.offer(node.left);
          }

          if(node.right != null){
            queue.offer(node.right);
          }

        }
        result.add(leveList);
      }
      return result;

      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       *
       */
    }

}
//leetcode submit region end(Prohibit modification and deletion)

}