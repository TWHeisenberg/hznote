//给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。 
//
// 
//
// 示例 1： 
//
// 
//
// 
//输入：height = [0,1,0,2,1,0,1,3,2,1,2,1]
//输出：6
//解释：上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水）。 
// 
//
// 示例 2： 
//
// 
//输入：height = [4,2,0,3,2,5]
//输出：9
// 
//
// 
//
// 提示： 
//
// 
// n == height.length 
// 0 <= n <= 3 * 104 
// 0 <= height[i] <= 105 
// 
// Related Topics 栈 数组 双指针 动态规划 
// 👍 2410 👎 0

package editor.cn;
public class TrappingRainWater {
    public static void main(String[] args) {
        Solution solution = new TrappingRainWater().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int trap(int[] height) {
/*      // 暴力破解法
      return solution1(height);*/

      // 用栈实现
      return solution2(height);
    }

      // 暴力破解法
      private int solution1(int[] height) {

        int total = 0;
        for(int i = 0;i < height.length; i++){

          int leftMax = 0;
          for(int j = 0;j < i;j++){
            leftMax = Math.max(leftMax, height[j]);
          }

          int rightMax = 0;
          for(int j = i+1;j < height.length;j++){
            rightMax = Math.max(rightMax, height[j]);
          }

          int min = Math.min(leftMax, rightMax);
          if(min > height[i]){
            total += (min - height[i]);
          }
        }

        // 时间复杂度：O(n^2), 空间复杂度：O(1)
        return total;
      }

      // 用栈实现
      private int solution2(int[] height) {
        Stack<Integer> stack = new Stack();

        int total = 0;
        int index = 0, len = height.length;
        while(index < len){

          while(!stack.isEmpty() && height[index] > height[stack.peek()]){
            int lowIndex = stack.pop();
            if (stack.isEmpty()) {
              break;
            }
            int rightMaxIndex = index;
            int leftMaxIndex = stack.peek();
            int rightMax = height[rightMaxIndex];
            int leftMax = height[leftMaxIndex];
            int min = Math.min(leftMax, rightMax);

            int distance = rightMaxIndex - leftMaxIndex - 1;

            int tempHeight = Math.min(height[rightMaxIndex], height[leftMaxIndex]) - height[lowIndex];
            total += (tempHeight * distance);
          }

          stack.push(index);
          index++;
        }


        // 空间复杂度：O(n), 时间复杂度：0(n)
        return total;
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}