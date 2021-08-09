//数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且 有效的 括号组合。 
//
// 
//
// 示例 1： 
//
// 
//输入：n = 3
//输出：["((()))","(()())","(())()","()(())","()()()"]
// 
//
// 示例 2： 
//
// 
//输入：n = 1
//输出：["()"]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= n <= 8 
// 
// Related Topics 字符串 动态规划 回溯 
// 👍 1948 👎 0

package editor.cn;
public class GenerateParentheses {
    public static void main(String[] args) {
        Solution solution = new GenerateParentheses().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
      private List<String> ret = new ArrayList();
      public List<String> generateParenthesis(int n) {
        if(n == 0){
          return ret;
        }

        backTrace(new StringBuffer(), n, 0, 0);
        return ret;
        /**
         * 回溯法
         * 时间复杂度：O(4^n / 开根n)
         * 空间复杂度：O(n)
         *
         */
      }

      private void backTrace(StringBuffer sbuf, int max, int open, int close){
        if(sbuf.length() == max * 2){
          ret.add(sbuf.toString());
          return;
        }

        if(open < max){
          sbuf.append("(");
          backTrace(sbuf, max, open + 1, close);
          sbuf.deleteCharAt(sbuf.length() - 1);
        }

        if(close < open){
          sbuf.append(")");
          backTrace(sbuf, max, open, close + 1);
          sbuf.deleteCharAt(sbuf.length() - 1);
        }
      }
}
//leetcode submit region end(Prohibit modification and deletion)

}