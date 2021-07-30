//给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。 
//
// 初始化 nums1 和 nums2 的元素数量分别为 m 和 n 。你可以假设 nums1 的空间大小等于 m + n，这样它就有足够的空间保存来自 nu
//ms2 的元素。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
//输出：[1,2,2,3,5,6]
// 
//
// 示例 2： 
//
// 
//输入：nums1 = [1], m = 1, nums2 = [], n = 0
//输出：[1]
// 
//
// 
//
// 提示： 
//
// 
// nums1.length == m + n 
// nums2.length == n 
// 0 <= m, n <= 200 
// 1 <= m + n <= 200 
// -109 <= nums1[i], nums2[i] <= 109 
// 
// Related Topics 数组 双指针 排序 
// 👍 1028 👎 0

package editor.cn;
public class MergeSortedArray {
    public static void main(String[] args) {
        Solution solution = new MergeSortedArray().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {

      int[] mergeNums = new int[m + n];

      int k = 0, i = 0, j = 0;
      while(i < m && j < n){
        if(nums1[i] < nums2[j]){
          mergeNums[k++] = nums1[i++];
        }else{
          mergeNums[k++] = nums2[j++];
        }
      }

      // 最多还有一个数组没有处理完
      while(i < m){
        mergeNums[k++] = nums1[i++];
      }

      while(j < n){
        mergeNums[k++] = nums2[j++];
      }


      for(int a = 0; a < nums1.length;a++){
        nums1[a] = mergeNums[a];
      }

    }
}
//leetcode submit region end(Prohibit modification and deletion)

}