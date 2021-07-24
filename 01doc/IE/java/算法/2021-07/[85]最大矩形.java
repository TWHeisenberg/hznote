//给定一个仅包含 0 和 1 、大小为 rows x cols 的二维二进制矩阵，找出只包含 1 的最大矩形，并返回其面积。 
//
// 
//
// 示例 1： 
//
// 
//输入：matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"]
//,["1","0","0","1","0"]]
//输出：6
//解释：最大矩形如上图所示。
// 
//
// 示例 2： 
//
// 
//输入：matrix = []
//输出：0
// 
//
// 示例 3： 
//
// 
//输入：matrix = [["0"]]
//输出：0
// 
//
// 示例 4： 
//
// 
//输入：matrix = [["1"]]
//输出：1
// 
//
// 示例 5： 
//
// 
//输入：matrix = [["0","0"]]
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// rows == matrix.length 
// cols == matrix[0].length 
// 0 <= row, cols <= 200 
// matrix[i][j] 为 '0' 或 '1' 
// 
// Related Topics 栈 数组 动态规划 矩阵 单调栈 
// 👍 975 👎 0

package editor.cn;
public class MaximalRectangle {
    public static void main(String[] args) {
        Solution solution = new MaximalRectangle().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int maximalRectangle(char[][] matrix) {
      if(matrix.length == 0){
        return 0;
      }
      /**
       * 转成84题，然后用单调栈解决
       * 1.统计每一层的高度
       * 2.使用84题的解法
       */

      int maxArea = 0;
      // 统计每一层的高度，如果放在里面就只统计当前层的
      int[] heights = new int[matrix[0].length];
      for(int i = 0;i < matrix.length;i++){
        for(int j = 0;j < matrix[0].length;j++){
          heights[j] = matrix[i][j] == '1' ? heights[j] + 1 : 0;
        }

        maxArea = Math.max(maxArea, largestRectangleArea(heights));
      }
      return maxArea;

      /**
       * 时间复杂度：O(mn)
       * 空间复杂度：O(n)
       *
       */
    }

    private int largestRectangleArea(int[] heights){
      int maxArea = 0;
      // 单调栈计算
      Stack<Integer> indexStack = new Stack();
      for(int i = 0;i < heights.length;i++){
        while(!indexStack.isEmpty() && heights[i] < heights[indexStack.peek()]){
          int height = heights[indexStack.pop()];
          int width = indexStack.isEmpty() ? i : (i - indexStack.peek() - 1);
          maxArea = Math.max(maxArea, width * height);
        }
        indexStack.push(i);
      }

      // 处理stack中剩余的柱子
      int len = heights.length;
      while(!indexStack.isEmpty()){
        int height = heights[indexStack.pop()];
        int width = indexStack.isEmpty() ? len : len - indexStack.peek() - 1;
        maxArea = Math.max(maxArea, height * width);
      }
      return maxArea;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}