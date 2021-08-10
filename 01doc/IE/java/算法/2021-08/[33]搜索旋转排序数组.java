//整数数组 nums 按升序排列，数组中的值 互不相同 。 
//
// 在传递给函数之前，nums 在预先未知的某个下标 k（0 <= k < nums.length）上进行了 旋转，使数组变为 [nums[k], nums[
//k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]（下标 从 0 开始 计数）。例如，
// [0,1,2,4,5,6,7] 在下标 3 处经旋转后可能变为 [4,5,6,7,0,1,2] 。
//
// 给你 旋转后 的数组 nums 和一个整数 target ，如果 nums 中存在这个目标值 target ，则返回它的下标，否则返回 -1 。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [4,5,6,7,0,1,2], target = 0
//输出：4
// 
//
// 示例 2： 
//
// 
//输入：nums = [4,5,6,7,0,1,2], target = 3
//输出：-1 
//
// 示例 3： 
//
// 
//输入：nums = [1], target = 0
//输出：-1
// 
//
// 
//
// 提示： 
//
// 
// 1 <= nums.length <= 5000 
// -10^4 <= nums[i] <= 10^4 
// nums 中的每个值都 独一无二 
// 题目数据保证 nums 在预先未知的某个下标上进行了旋转 
// -10^4 <= target <= 10^4 
// 
//
// 
//
// 进阶：你可以设计一个时间复杂度为 O(log n) 的解决方案吗？ 
// Related Topics 数组 二分查找 
// 👍 1502 👎 0

package editor.cn;
public class SearchInRotatedSortedArray {
    public static void main(String[] args) {
        Solution solution = new SearchInRotatedSortedArray().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int search(int[] nums, int target) {
      if(nums.length == 1 && nums[0] == target){
        return 0;
      }
      int len = nums.length, left = 0, right = len - 1;
      // 左右肯定有一个是有序的
      while(right >= left){
        int mid = left + (right - left) / 2;
        if(nums[mid] == target){
          return mid;
        }
        if(nums[left] < nums[mid] && target <= nums[mid] && target >= nums[left]){
          // 左边有序并且在左边
          right = mid - 1;
          continue;
        }

        if(nums[mid] < nums[right] && target >= nums[mid] && target <= nums[right]){
          // 右边有序并且在右边
          left = mid + 1;
          continue;
        }

        // 说明肯定在无序的一边
        if(nums[left] > nums[mid]){
          // 左边无序， 在左边继续找
          right = mid - 1;
        }else{
          // 右边无序，继续在右边找
          left = mid + 1;
        }
      }
      return -1;

      /**
       * 二分法查找，关键在于判断左右两边肯定有一边是有序的
       * 时间复杂度：O(log n)
       * 空间复杂度：O(1)
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}