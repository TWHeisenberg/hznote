//请根据每日 气温 列表 temperatures ，请计算在每一天需要等几天才会有更高的温度。如果气温在这之后都不会升高，请在该位置用 0 来代替。 
//
// 示例 1: 
//
// 
//输入: temperatures = [73,74,75,71,69,72,76,73]
//输出: [1,1,4,2,1,1,0,0]
// 
//
// 示例 2: 
//
// 
//输入: temperatures = [30,40,50,60]
//输出: [1,1,1,0]
// 
//
// 示例 3: 
//
// 
//输入: temperatures = [30,60,90]
//输出: [1,1,0] 
//
// 
//
// 提示： 
//
// 
// 1 <= temperatures.length <= 105 
// 30 <= temperatures[i] <= 100 
// 
// Related Topics 栈 数组 单调栈 
// 👍 819 👎 0

package editor.cn;
public class DailyTemperatures {
    public static void main(String[] args) {
        Solution solution = new DailyTemperatures().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
      // return solution1_bruteForce(temperatures);
      return solution2_stack(temperatures);
    }

    private int[] solution1_bruteForce(int[] temp){
      /**
       * 暴力破解
       *
       */
      int[] durations = new int[temp.length];

      for(int i = 0;i < temp.length;i++){
        for(int j = i;j < temp.length;j++){
          int today = temp[i], next = temp[j];
          if(next > today){
            durations[i] = j - i;
            break;
          }
        }
      }
      return durations;

      /**
       * 时间复杂度：O(n^2)
       * 空间复杂度：O(1)
       */
    }

      private int[] solution2_stack(int[] temp){
        Stack<Integer> indexStack = new Stack();
        int[] durations = new int[temp.length];

        for(int i = 0;i < temp.length;i++){
          while(!indexStack.isEmpty() && temp[indexStack.peek()] < temp[i]){
             int index = indexStack.pop();
             durations[index] = i - index;
           }
          indexStack.push(i);
        }
        return durations;

        /**
         * 时间复杂度：O(n)
         * 空间复杂度：O(n)
         */
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}