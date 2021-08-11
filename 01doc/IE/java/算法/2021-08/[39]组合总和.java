//给定一个无重复元素的正整数数组 candidates 和一个正整数 target ，找出 candidates 中所有可以使数字和为目标数 target 的
//唯一组合。 
//
// candidates 中的数字可以无限制重复被选取。如果至少一个所选数字数量不同，则两种组合是唯一的。 
//
// 对于给定的输入，保证和为 target 的唯一组合数少于 150 个。 
//
// 
//
// 示例 1： 
//
// 
//输入: candidates = [2,3,6,7], target = 7
//输出: [[7],[2,2,3]]
// 
//
// 示例 2： 
//
// 
//输入: candidates = [2,3,5], target = 8
//输出: [[2,2,2,2],[2,3,3],[3,5]] 
//
// 示例 3： 
//
// 
//输入: candidates = [2], target = 1
//输出: []
// 
//
// 示例 4： 
//
// 
//输入: candidates = [1], target = 1
//输出: [[1]]
// 
//
// 示例 5： 
//
// 
//输入: candidates = [1], target = 2
//输出: [[1,1]]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= candidates.length <= 30 
// 1 <= candidates[i] <= 200 
// candidate 中的每个元素都是独一无二的。 
// 1 <= target <= 500 
// 
// Related Topics 数组 回溯 
// 👍 1468 👎 0

package editor.cn;
public class CombinationSum {
    public static void main(String[] args) {
        Solution solution = new CombinationSum().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
      List<List<Integer>> ret = new ArrayList();
      public List<List<Integer>> combinationSum(int[] candidates, int target) {
        backTrace(candidates, target, 0 ,0, new ArrayList<Integer>());
        return ret;
        /**
         * 回溯+记录层数保证不会重复
         * 时间复杂度：O(S) S 为所有可行解的长度之和
         * 空间复杂度：O(target) 空间复杂度取决于递归的栈深度，在最差情况下需要递归 O(target) 层。
         */
      }

      private void backTrace(int[] candidates, int target, int sum, int index, List<Integer> nums){
        if(sum > target){
          return;
        }
        if(sum == target){
          ret.add(new ArrayList(nums));
          return;
        }
        for(int i = index;i < candidates.length;i++){
          nums.add(candidates[i]);
          backTrace(candidates, target, sum + candidates[i],index, nums);
          nums.remove(nums.size() - 1);
          index++;
        }
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}