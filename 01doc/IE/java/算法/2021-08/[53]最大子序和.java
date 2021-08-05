//给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [-2,1,-3,4,-1,2,1,-5,4]
//输出：6
//解释：连续子数组 [4,-1,2,1] 的和最大，为 6 。
// 
//
// 示例 2： 
//
// 
//输入：nums = [1]
//输出：1
// 
//
// 示例 3： 
//
// 
//输入：nums = [0]
//输出：0
// 
//
// 示例 4： 
//
// 
//输入：nums = [-1]
//输出：-1
// 
//
// 示例 5： 
//
// 
//输入：nums = [-100000]
//输出：-100000
// 
//
// 
//
// 提示： 
//
// 
// 1 <= nums.length <= 3 * 104 
// -105 <= nums[i] <= 105 
// 
//
// 
//
// 进阶：如果你已经实现复杂度为 O(n) 的解法，尝试使用更为精妙的 分治法 求解。 
// Related Topics 数组 分治 动态规划 
// 👍 3518 👎 0

package editor.cn;
public class MaximumSubarray {
    public static void main(String[] args) {
        Solution solution = new MaximumSubarray().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int maxSubArray(int[] nums) {
      // return solution1_bruteForce(nums);
      return solution2_DivideAndConquer(nums);
    }

    private int solution1_bruteForce(int[] nums){
      // 暴力破解法
      int max = Integer.MIN_VALUE;

      for(int i = 0;i < nums.length;i++){
        int sum = 0;
        for(int j = i;j < nums.length;j++){
          sum += nums[j];
          max = Math.max(max, sum);
        }
      }
      return max;
      /**
       * 时间复杂度：O(n^2)
       * 空间复杂度：O(1)
       */
    }


    private int solution2_DivideAndConquer(int[] nums){
      return maxSubArrayDivide(nums, 0, nums.length - 1);
    }

    private int maxSubArrayDivide(int[] nums,int start, int end){
      if(start == end){
        return nums[start];
      }

      int middle = (start + end) / 2;
      int leftMax = maxSubArrayDivide(nums, start, middle);
      int rightMax = maxSubArrayDivide(nums, middle + 1, end);

      int leftTmpMax = Integer.MIN_VALUE;
      int leftSum = 0;
      for(int i = middle; i >= start;i--){
        leftSum += nums[i];
        leftTmpMax = Math.max(leftTmpMax, leftSum);
      }

      int rightTmpMax = nums[middle + 1];
      int rightSum = 0;
      for(int i = middle + 1;i <= end;i++){
        rightSum += nums[i];
        rightTmpMax = Math.max(rightTmpMax, rightSum);
      }

      int sum = rightTmpMax + leftTmpMax;
      return Math.max(sum, Math.max(leftMax, rightMax));

      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       *
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}