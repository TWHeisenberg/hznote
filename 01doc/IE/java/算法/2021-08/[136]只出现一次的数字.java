//给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。 
//
// 说明： 
//
// 你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？ 
//
// 示例 1: 
//
// 输入: [2,2,1]
//输出: 1
// 
//
// 示例 2: 
//
// 输入: [4,1,2,1,2]
//输出: 4 
// Related Topics 位运算 数组 
// 👍 1951 👎 0

package editor.cn;
public class SingleNumber {
    public static void main(String[] args) {
        Solution solution = new SingleNumber().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int singleNumber(int[] nums) {

      int num = nums[0];
      for(int i = 1;i < nums.length;i++){
        num ^= nums[i];
      }
      return num;

      /**
       * 异或运算
       * 时间复杂度：O(n)
       * 空间复杂度：O(1)
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}