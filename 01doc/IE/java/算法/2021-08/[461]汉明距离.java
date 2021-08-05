//两个整数之间的 汉明距离 指的是这两个数字对应二进制位不同的位置的数目。 
//
// 给你两个整数 x 和 y，计算并返回它们之间的汉明距离。 
//
// 
//
// 示例 1： 
//
// 
//输入：x = 1, y = 4
//输出：2
//解释：
//1   (0 0 0 1)
//4   (0 1 0 0)
//       ↑   ↑
//上面的箭头指出了对应二进制位不同的位置。
// 
//
// 示例 2： 
//
// 
//输入：x = 3, y = 1
//输出：1
// 
//
// 
//
// 提示： 
//
// 
// 0 <= x, y <= 231 - 1 
// 
// Related Topics 位运算 
// 👍 503 👎 0

package editor.cn;
public class HammingDistance {
    public static void main(String[] args) {
        Solution solution = new HammingDistance().new Solution();
    }
    //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int hammingDistance(int x, int y) {

      return solution_BrianKernighan(x, y);
    }

    private int solution_bitCount(int x, int y){
      return Integer.bitCount(x ^ y);
      /**
       * 异或运算，然后计算1的个数
       * 时间复杂度：O(1)
       * 空间复杂度：O(1)
       */
    }

    private int solution_BrianKernighan(int x, int y){
      int s = x ^ y;
      int count = 0;
      while(s != 0){
        s &= (s-1);
        count++;
      }
      return count;
      /**
       * 对任何一个数 n，n & ( n − 1 )的结果是n的比特位最右端的1变为0的结果
       * 时间复杂度：O(logC)，其中 C 是元素的数据范围
       * 空间复杂度：O(1)
       *
       */
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}