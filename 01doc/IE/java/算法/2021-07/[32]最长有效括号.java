//给你一个只包含 '(' 和 ')' 的字符串，找出最长有效（格式正确且连续）括号子串的长度。 
//
// 
//
// 
// 
// 示例 1： 
//
// 
//输入：s = "(()"
//输出：2
//解释：最长有效括号子串是 "()"
// 
//
// 示例 2： 
//
// 
//输入：s = ")()())"
//输出：4
//解释：最长有效括号子串是 "()()"
// 
//
// 示例 3： 
//
// 
//输入：s = ""
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// 0 <= s.length <= 3 * 104 
// s[i] 为 '(' 或 ')' 
// 
// 
// 
// Related Topics 栈 字符串 动态规划 
// 👍 1373 👎 0

package editor.cn;
public class LongestValidParentheses {
    public static void main(String[] args) {
        Solution solution = new LongestValidParentheses().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int longestValidParentheses(String s) {
      return solution1_stack(s);

    }

    private int solution1_stack(String s){
      char[] chars = s.toCharArray();
      Stack<Integer> stack = new Stack();
      stack.push(-1); // 防止边界情况
      int maxLen = 0;
      for(int i = 0;i < chars.length;i++){
        if (chars[i] == '(') {
          stack.push(i);
        }else{
          stack.pop();
          if(!stack.isEmpty()){
            maxLen = Math.max(maxLen, i - stack.peek());
          }else{
            stack.push(i);
          }
        }
      }
      return maxLen;
      /**
       * 时间复杂度：O(n)
       * 空间复杂度：O(n)
       *
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}