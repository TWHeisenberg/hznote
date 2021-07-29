//给定一棵树的前序遍历 preorder 与中序遍历 inorder。请构造二叉树并返回其根节点。 
//
// 
//
// 示例 1: 
//
// 
//Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
//Output: [3,9,20,null,null,15,7]
// 
//
// 示例 2: 
//
// 
//Input: preorder = [-1], inorder = [-1]
//Output: [-1]
// 
//
// 
//
// 提示: 
//
// 
// 1 <= preorder.length <= 3000 
// inorder.length == preorder.length 
// -3000 <= preorder[i], inorder[i] <= 3000 
// preorder 和 inorder 均无重复元素 
// inorder 均出现在 preorder 
// preorder 保证为二叉树的前序遍历序列 
// inorder 保证为二叉树的中序遍历序列 
// 
// Related Topics 树 数组 哈希表 分治 二叉树 
// 👍 1131 👎 0

package editor.cn;

import java.util.HashMap;
import java.util.Map;
import tmp.Test.TreeNode;

public class ConstructBinaryTreeFromPreorderAndInorderTraversal {
    public static void main(String[] args) {
        Solution solution = new ConstructBinaryTreeFromPreorderAndInorderTraversal().new Solution();
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
class Solution { Map<Integer, Integer> indexMap;
  public TreeNode buildTree(int[] preorder, int[] inorder) {
    /**
     * 递归
     */
    indexMap = new HashMap();
    for(int i = 0;i < inorder.length;i++){
      indexMap.put(inorder[i], i);
    }

    int len = preorder.length;
    TreeNode root = buildTree(preorder, 0, len - 1, 0, len - 1);
    return root;
  }

  private TreeNode buildTree(int[] preorder, int preorderLeft, int preorderRight, int inorderLeft, int inorderRight){
    if(preorderLeft > preorderRight){
      return null;
    }
    // 构建根节点
    TreeNode root = new TreeNode(preorder[preorderLeft]);
    int inorderRootIndex = indexMap.get(root.val);
    int leftSize = inorderRootIndex - inorderLeft;
    int rightSize = inorderRight - inorderRootIndex;

    root.left = buildTree(preorder, preorderLeft + 1, preorderLeft + leftSize, inorderLeft, inorderRootIndex - 1);
    root.right = buildTree(preorder, preorderLeft + leftSize + 1, preorderRight, inorderRootIndex + 1, rightSize);

    return root;

    /**
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     */
  }
}
//leetcode submit region end(Prohibit modification and deletion)

}