//给定一个二叉树的根节点 root ，和一个整数 targetSum ，求该二叉树里节点值之和等于 targetSum 的 路径 的数目。 
//
// 路径 不需要从根节点开始，也不需要在叶子节点结束，但是路径方向必须是向下的（只能从父节点到子节点）。 
//
// 
//
// 示例 1： 
//
// 
//
// 
//输入：root = [10,5,-3,3,2,null,11,3,-2,null,1], targetSum = 8
//输出：3
//解释：和等于 8 的路径有 3 条，如图所示。
// 
//
// 示例 2： 
//
// 
//输入：root = [5,4,8,11,null,13,4,7,2,null,null,5,1], targetSum = 22
//输出：3
// 
//
// 
//
// 提示: 
//
// 
// 二叉树的节点个数的范围是 [0,1000] 
// -109 <= Node.val <= 109 
// -1000 <= targetSum <= 1000 
// 
// Related Topics 树 深度优先搜索 二叉树 
// 👍 935 👎 0

package editor.cn;
public class PathSumIii {
    public static void main(String[] args) {
        Solution solution = new PathSumIii().new Solution();
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

  public int pathSum(TreeNode root, int targetSum) {
    // 前缀和
    Map<Integer, Integer> map = new HashMap();
    map.put(0, 1);
    return pathSum(root, targetSum, map, 0);
  }

  private int pathSum(TreeNode root, int targetSum, Map<Integer, Integer> map, int preSum){
    if(root == null){
      return 0;
    }

    int count = 0;
    preSum += root.val;

    if(map.containsKey(preSum - targetSum)){
      count += map.get(preSum - targetSum);
    }
    map.put(preSum, map.getOrDefault(preSum, 0) + 1);

    count += pathSum(root.left, targetSum, map, preSum);
    count += pathSum(root.right, targetSum, map, preSum);

    // 回到本层，恢复状态，去除当前节点的前缀和数量
    map.put(preSum, map.get(preSum) - 1);
    return count;
  }
}
//leetcode submit region end(Prohibit modification and deletion)

}