//给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。
//
// 百度百科中最近公共祖先的定义为：“对于有根树 T 的两个节点 p、q，最近公共祖先表示为一个节点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大（
//一个节点也可以是它自己的祖先）。”
//
//
//
// 示例 1：
//
//
//输入：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
//输出：3
//解释：节点 5 和节点 1 的最近公共祖先是节点 3 。
//
//
// 示例 2：
//
//
//输入：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
//输出：5
//解释：节点 5 和节点 4 的最近公共祖先是节点 5 。因为根据定义最近公共祖先节点可以为节点本身。
//
//
// 示例 3：
//
//
//输入：root = [1,2], p = 1, q = 2
//输出：1
//
//
//
//
// 提示：
//
//
// 树中节点数目在范围 [2, 105] 内。
// -109 <= Node.val <= 109
// 所有 Node.val 互不相同 。
// p != q
// p 和 q 均存在于给定的二叉树中。
//
// Related Topics 树 深度优先搜索 二叉树
// 👍 1225 👎 0

package editor.cn;

import tmp.Test.TreeNode;

public class LowestCommonAncestorOfABinaryTree {
    public static void main(String[] args) {
        Solution solution = new LowestCommonAncestorOfABinaryTree().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
      // return solution1_recursion(root, p, q);
      return solution2_findParent(root, p, q);
    }

  private TreeNode solution2_findParent(TreeNode root, TreeNode p, TreeNode q){
      Set<Integer> visited = new HashSet();
      dfs(root);
      while(p != null){
        visited.add(p.val);
        TreeNode parent = parentMap.get(p.val);
        p = parent;
      }

      while(q != null){
        if(visited.contains(q.val)){
          return q;
        }
        TreeNode parent = parentMap.get(q.val);
        q = parent;
      }

      return null;
    /**
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     */
  }

  Map<Integer, TreeNode> parentMap = new HashMap();
  private void dfs(TreeNode root){
    if(root == null){
      return;
    }

    if(root.left != null){
      parentMap.put(root.left.val, root);
      dfs(root.left);
    }

    if(root.right != null){
      parentMap.put(root.right.val, root);
      dfs(root.right);
    }



  }

    private TreeNode solution1_recursion(TreeNode root, TreeNode p, TreeNode q){
      // 递归
      if(root == null){
        return null;
      }
      if(root.val == p.val || root.val == q.val){
        return root;
      }

      TreeNode left = lowestCommonAncestor(root.left, p, q);
      TreeNode right = lowestCommonAncestor(root.right, p, q);

      if(left != null && right != null){
        return root;
      }
      if(left == null){
        return right;
      }
      if(right == null){
        return left;
      }
      return null;
      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}