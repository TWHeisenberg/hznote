//给你一个含 n 个整数的数组 nums ，其中 nums[i] 在区间 [1, n] 内。请你找出所有在 [1, n] 范围内但没有出现在 nums 中的数
//字，并以数组的形式返回结果。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [4,3,2,7,8,2,3,1]
//输出：[5,6]
// 
//
// 示例 2： 
//
// 
//输入：nums = [1,1]
//输出：[2]
// 
//
// 
//
// 提示： 
//
// 
// n == nums.length 
// 1 <= n <= 105 
// 1 <= nums[i] <= n 
// 
//
// 进阶：你能在不使用额外空间且时间复杂度为 O(n) 的情况下解决这个问题吗? 你可以假定返回的数组不算在额外空间内。 
// Related Topics 数组 哈希表 
// 👍 785 👎 0

package editor.cn;
public class FindAllNumbersDisappearedInAnArray {
    public static void main(String[] args) {
        Solution solution = new FindAllNumbersDisappearedInAnArray().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<Integer> findDisappearedNumbers(int[] nums) {
          return solution2_formerPlace(nums);
    }

    public List<Integer> solution2_formerPlace(int[] nums) {
      // 有的数字原地加n
      int n = nums.length;
      for(int num : nums){
        int i = (num - 1) % n; // 计算坐标
        nums[i] += n;
      }

      List<Integer> ret = new ArrayList();
      for(int i = 0;i < n;i++){
        if(nums[i] <= n){ // 小于n说明这个坐标代表的数字是没有的
          ret.add(i + 1);
        }
      }
      return ret;

      /**
       * 原地修改，给出现的数字代表的坐标元素+n，而后小于等于n的坐标肯定是没有出现过的数字
       * 时间复杂度：O(n)
       * 空间复杂度：O(1)
       */
     }

    private List<Integer> solution1_bruteForce(int[] nums){
      Set<Integer> set = new HashSet();
      int i = 1;
      while(i <= nums.length){
        set.add(i++);
      }

      List<Integer> disNumbers = new ArrayList();
      for(int j = 0;j < nums.length;j++){
        if(set.contains(nums[j])){
          set.remove(nums[j]);
        }
      }
      return new ArrayList(set);
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}