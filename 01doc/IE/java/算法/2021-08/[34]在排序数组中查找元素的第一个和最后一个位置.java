//给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。 
//
// 如果数组中不存在目标值 target，返回 [-1, -1]。 
//
// 进阶： 
//
// 
// 你可以设计并实现时间复杂度为 O(log n) 的算法解决此问题吗？ 
// 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [5,7,7,8,8,10], target = 8
//输出：[3,4] 
//
// 示例 2： 
//
// 
//输入：nums = [5,7,7,8,8,10], target = 6
//输出：[-1,-1] 
//
// 示例 3： 
//
// 
//输入：nums = [], target = 0
//输出：[-1,-1] 
//
// 
//
// 提示： 
//
// 
// 0 <= nums.length <= 105 
// -109 <= nums[i] <= 109 
// nums 是一个非递减数组 
// -109 <= target <= 109 
// 
// Related Topics 数组 二分查找 
// 👍 1140 👎 0

package editor.cn;
public class FindFirstAndLastPositionOfElementInSortedArray {
    public static void main(String[] args) {
        Solution solution = new FindFirstAndLastPositionOfElementInSortedArray().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int[] searchRange(int[] nums, int target) {
      int len = nums.length, left = 0, right = len - 1;

      int index = -1;
      while(right >= left){
        int mid = left + (right - left) / 2;
        if(nums[mid] > target){
          right = mid - 1;
        }else if(nums[mid] < target){
          left = mid + 1;
        }else{
          index = mid;
          break;
        }

      }

      if(index == -1){
        return new int[]{-1, -1};
      }

      // 窗口
      int wleft = index, wright = index;
      while(wleft >= 0 && nums[wleft] == target){
        wleft--;
      }

      while(wright < len && nums[wright] == target){
        wright++;
      }
      return new int[]{++wleft, --wright};
      /**
       * 二分查找
       * 时间复杂度：O(n)
       * 空间复杂度：O(1)
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}