//给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。 
//
// 你可以假设数组是非空的，并且给定的数组总是存在多数元素。 
//
// 
//
// 示例 1： 
//
// 
//输入：[3,2,3]
//输出：3 
//
// 示例 2： 
//
// 
//输入：[2,2,1,1,1,2,2]
//输出：2
// 
//
// 
//
// 进阶： 
//
// 
// 尝试设计时间复杂度为 O(n)、空间复杂度为 O(1) 的算法解决此问题。 
// 
// Related Topics 数组 哈希表 分治 计数 排序 
// 👍 1083 👎 0

package editor.cn;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MajorityElement {
    public static void main(String[] args) {
        Solution solution = new MajorityElement().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int majorityElement(int[] nums) {
      return solution2_sort(nums);
    }

      private int solution1_hash(int[] nums){
        Map<Integer, Integer> counter = new HashMap();

        int len = nums.length;
        for(int i = 0;i < len;i++){
          counter.put(nums[i], counter.getOrDefault(nums[i], 0) + 1);
        }

        for(Map.Entry<Integer, Integer> entry : counter.entrySet()){
          if(entry.getValue()> len / 2){
            return entry.getKey();
          }
        }
        return 0;
      }

      private int solution2_sort(int[] nums){
        Arrays.sort(nums);
        return nums[nums.length / 2];
        /**
         * 排序，第nums.length / 2 一定是众数
         * 时间复杂度：O(nlog n) 归并排序的复杂度
         * 空间复杂度：O(1)
         */
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}