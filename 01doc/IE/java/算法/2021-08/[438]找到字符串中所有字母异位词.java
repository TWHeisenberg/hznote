//给定两个字符串 s 和 p，找到 s 中所有 p 的 异位词 的子串，返回这些子串的起始索引。不考虑答案输出的顺序。 
//
// 异位词 指字母相同，但排列不同的字符串。 
//
// 
//
// 示例 1: 
//
// 
//输入: s = "cbaebabacd", p = "abc"
//输出: [0,6]
//解释:
//起始索引等于 0 的子串是 "cba", 它是 "abc" 的异位词。
//起始索引等于 6 的子串是 "bac", 它是 "abc" 的异位词。
// 
//
// 示例 2: 
//
// 
//输入: s = "abab", p = "ab"
//输出: [0,1,2]
//解释:
//起始索引等于 0 的子串是 "ab", 它是 "ab" 的异位词。
//起始索引等于 1 的子串是 "ba", 它是 "ab" 的异位词。
//起始索引等于 2 的子串是 "ab", 它是 "ab" 的异位词。
// 
//
// 
//
// 提示: 
//
// 
// 1 <= s.length, p.length <= 3 * 104 
// s 和 p 仅包含小写字母 
// 
// Related Topics 哈希表 字符串 滑动窗口 
// 👍 577 👎 0

package editor.cn;

import java.util.ArrayList;

public class FindAllAnagramsInAString {
    public static void main(String[] args) {
        Solution solution = new FindAllAnagramsInAString().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
      List<Integer> ret = new ArrayList();
      if(isEmpty(s) || isEmpty(p) || p.length() > s.length()){
        return ret;
      }

      int[] target = new int[26];
      for(int i = 0;i < p.length();i++){
        target[p.charAt(i) - 'a']++;
      }

      // 初始化窗口
      int left = 0, right = p.length() - 1;
      int[] window = new int[26];
      for(int i = left;i <= right;i++){
        window[s.charAt(i) - 'a']++;
      }

      while(right < s.length()){
        if(Arrays.equals(window, target)){
          ret.add(left);
        }
        // 移动窗口
        window[s.charAt(left) - 'a']--;
        left++;
        right++;
        if(right == s.length()){
          break;
        }
        window[s.charAt(right) - 'a']++;
      }
      return ret;
      /**
       * 滑动窗口 + hash表
       * 时间复杂度：O(n)
       * 空间复杂度：O(1)
       */
    }

    private boolean isEmpty(String s){
      return s == null || s.equals("");
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}