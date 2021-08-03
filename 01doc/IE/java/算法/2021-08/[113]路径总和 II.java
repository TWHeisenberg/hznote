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
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList();
        if(root == null){
            return result;
        }
        // 保存符合条件的叶子节点
        List<TreeNode> targetNodes = new ArrayList();
        Map<TreeNode, TreeNode> parentMap = new HashMap();

        Deque<TreeNode> nodeQueue = new LinkedList();
        Deque<Integer> valQueue = new LinkedList();

        nodeQueue.offer(root);
        valQueue.offer(root.val);

        while(!nodeQueue.isEmpty()){

            TreeNode node = nodeQueue.poll();
            int val = valQueue.poll();

            if(node.left == null && node.right == null){
                if(val == targetSum){
                    targetNodes.add(node);
                }
                continue;

            }
 
            if(node.left != null){
                nodeQueue.offer(node.left);
                valQueue.offer(val + node.left.val);
                parentMap.put(node.left, node);
            }

            if(node.right != null){
                nodeQueue.offer(node.right);
                valQueue.offer(val + node.right.val);
                parentMap.put(node.right, node);
            }
        }
        
        // 从叶子节点找到所有父节点
        for(TreeNode targetNode : targetNodes){
            // 用一个临时的栈保存从底向上的结果
            List<Integer> pathNodes = new ArrayList();
            TreeNode p = targetNode;
            while(p != null){
                pathNodes.add(p.val);
                p = parentMap.get(p);
            }


            Collections.reverse(pathNodes);            
            result.add(pathNodes);
        }

        return result;

        /**
            时间复杂度：O(n*n)
            空间复杂度：O(n)
         */
    }
}