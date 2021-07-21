//给你一个整数数组 nums ，你需要找出一个 连续子数组 ，如果对这个子数组进行升序排序，那么整个数组都会变为升序排序。 
//
// 请你找出符合题意的 最短 子数组，并输出它的长度。 
//
// 
//
// 
// 
// 示例 1： 
//
// 
//输入：nums = [2,6,4,8,10,9,15]
//输出：5
//解释：你只需要对 [6, 4, 8, 10, 9] 进行升序排序，那么整个表都会变为升序排序。
// 
//
// 示例 2： 
//
// 
//输入：nums = [1,2,3,4]
//输出：0
// 
//
// 示例 3： 
//
// 
//输入：nums = [1]
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// 1 <= nums.length <= 104 
// -105 <= nums[i] <= 105 
// 
//
// 
//
// 进阶：你可以设计一个时间复杂度为 O(n) 的解决方案吗？ 
// 
// 
// Related Topics 栈 贪心 数组 双指针 排序 单调栈 
// 👍 572 👎 0

package editor.cn;
public class ShortestUnsortedContinuousSubarray {
    public static void main(String[] args) {
        Solution solution = new ShortestUnsortedContinuousSubarray().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int findUnsortedSubarray(int[] nums) {
      // return solution1_bruteForce(nums);
      return solution1_stack(nums);
    }

    private int solution1_bruteForce(int[] nums){
      int len = nums.length;
      int left = nums.length, right = 0;
      for(int i = 0;i < len - 1;i++){
        for(int j = i + 1;j<len;j++){
          if(nums[i] > nums[j]){
            left = Math.min(i, left);
            right = Math.max(j, right);
          }
        }

      }
      return right - left < 0 ? 0 : right - left + 1;

      /**
       * 时间复杂度：O(n^2)
       * 空间复杂度：O(1)
       */
    }

      private int solution1_stack(int[] nums){
      Stack<Integer> stack = new Stack();

      int left = nums.length, right = 0;
      for(int i = 0;i < nums.length;i++){
        while(!stack.isEmpty() && nums[stack.peek()] > nums[i]){
          left = Math.min(left, stack.pop());
        }
        stack.push(i);
      }
      stack.clear();

      for(int j = nums.length - 1;j >= 0;j--){
        while(!stack.isEmpty() && nums[stack.peek()] < nums[j]){
          right = Math.max(right, stack.pop());
        }
        stack.push(j);
      }

      return right - left < 0 ? 0 : right - left + 1;
        /**
         * 时间复杂度：O(n)
         * 空间复杂度：O(n)
         *
         */
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}