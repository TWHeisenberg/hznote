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
    public boolean hasPathSum(TreeNode root, int sum) {

        return hasPathSum_iter(root, sum);
 
    }

    private boolean hasPathSum_recur(TreeNode root, int sum){
       if(root == null){
            return false;
        }

        if(root.left == null && root.right == null){
            return sum == root.val;
        }

        return hasPathSum(root.left, sum - root.val) || hasPathSum(root.right, sum - root.val);

    }

private boolean hasPathSum_iter(TreeNode root, int sum){
    
    if(root == null){
        return false;
    }

        Deque<TreeNode> nodeQueue = new LinkedList();
        Deque<Integer> valQueue = new LinkedList();

        nodeQueue.offer(root);
        valQueue.offer(root.val);

        while(!nodeQueue.isEmpty()){

            TreeNode node = nodeQueue.poll();
            int val = valQueue.poll();

            if(node.left == null && node.right == null){
                if(val == sum){
                    return true;
                }
                continue;
            }

            if(node.left != null){
                nodeQueue.offer(node.left);
                valQueue.offer(val + node.left.val);
            }

            if(node.right != null){
                nodeQueue.offer(node.right);
                valQueue.offer(val + node.right.val);
            }
        }
        return false;
}
}