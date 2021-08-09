//给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。 
//
// 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。 
//
// 
//
// 
//
// 示例 1： 
//
// 
//输入：digits = "23"
//输出：["ad","ae","af","bd","be","bf","cd","ce","cf"]
// 
//
// 示例 2： 
//
// 
//输入：digits = ""
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：digits = "2"
//输出：["a","b","c"]
// 
//
// 
//
// 提示： 
//
// 
// 0 <= digits.length <= 4 
// digits[i] 是范围 ['2', '9'] 的一个数字。 
// 
// Related Topics 哈希表 字符串 回溯 
// 👍 1438 👎 0

package editor.cn;
public class LetterCombinationsOfAPhoneNumber {
    public static void main(String[] args) {
        Solution solution = new LetterCombinationsOfAPhoneNumber().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {

      List<String> ret = new ArrayList();
      public List<String> letterCombinations(String digits) {
        if(digits == null || digits.length() == 0){
          return ret;
        }
        Map<Character, String> map = new HashMap() {
          {
            put('2', "abc");
            put('3', "def");
            put('4', "ghi");
            put('5', "jkl");
            put('6', "mno");
            put('7', "pqrs");
            put('8', "tuv");
            put('9', "wxyz");
          }
        };

        backTrace(digits, 0, map, new StringBuffer());
        return ret;
        /**
         *  递归回溯法，关键在于进入下一次回溯前删除本层的字符串
         *  O(3^m * 4^n) m表示字母长度为3的数字个数，n表示字母长度为4的数字个数
         *  空间复杂度：O(m+n)O(m+n)，其中 m 是输入中对应 3 个字母的数字个数，n 是输入中对应 4 个字母的数字个数，
         *  m+n 是输入数字的总个数。除了返回值以外，空间复杂度主要取决于哈希表以及回溯过程中的递归调用层数，哈希表的大小与输入无关，可以看成常数，递归调用层数最大为 m+n。
         */
      }

      private void backTrace(String digits, Integer index, Map<Character, String> map,
          StringBuffer sbuf) {

        if (index == digits.length()) {
          ret.add(sbuf.toString());
          return;
        }

        Character num = digits.charAt(index);
        String letters = map.get(num);
        for (int i = 0; i < letters.length(); i++) {
          sbuf.append(letters.charAt(i));
          backTrace(digits, index + 1, map, sbuf);
          sbuf.deleteCharAt(index); // 关键在于这一步
        }
      }
    }
//leetcode submit region end(Prohibit modification and deletion)

}