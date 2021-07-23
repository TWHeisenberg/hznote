//给定 n 个非负整数，用来表示柱状图中各个柱子的高度。每个柱子彼此相邻，且宽度为 1 。 
//
// 求在该柱状图中，能够勾勒出来的矩形的最大面积。 
//
// 
//
// 示例 1: 
//
// 
//
// 
//输入：heights = [2,1,5,6,2,3]
//输出：10
//解释：最大的矩形为图中红色区域，面积为 10
// 
//
// 示例 2： 
//
// 
//
// 
//输入： heights = [2,4]
//输出： 4 
//
// 
//
// 提示： 
//
// 
// 1 <= heights.length <=105 
// 0 <= heights[i] <= 104 
// 
// Related Topics 栈 数组 单调栈 
// 👍 1447 👎 0

package editor.cn;

import java.util.Stack;

public class LargestRectangleInHistogram {
    public static void main(String[] args) {
        Solution solution = new LargestRectangleInHistogram().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int largestRectangleArea(int[] heights) {
      return solution1_stack(heights);

    }

      public int solution1_stack(int[] heights) {

        Stack<Integer> indexStack = new Stack();
        int maxRange = 0;
        for(int i = 0;i < heights.length;i++){
          while(!indexStack.isEmpty() && heights[i] < heights[indexStack.peek()]){
            int height = heights[indexStack.pop()];
            int width = indexStack.isEmpty() ? i : (i - indexStack.peek() - 1) ;
            maxRange = Math.max(maxRange, height * width);
          }
          indexStack.push(i);
        }

        // 遍历完了，处理stack中的柱子
        int len = heights.length;
        while(!indexStack.isEmpty()){
          int height = heights[indexStack.pop()];
          int width = indexStack.isEmpty() ? len : len - indexStack.peek() - 1;
          maxRange = Math.max(maxRange, height * width);
        }
        return maxRange;

        /**
         * 时间复杂度：O(n)
         * 空间复杂度：O(n)
         *
         */
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}