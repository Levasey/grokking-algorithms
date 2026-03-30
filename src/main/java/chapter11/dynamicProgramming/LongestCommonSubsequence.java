package chapter11.dynamicProgramming;

import java.util.Objects;

/**
 * Самая длинная общая подпоследовательность (НОП): dp[i][j] — длина НОП для префиксов длины i и j.
 */
public final class LongestCommonSubsequence {

    private LongestCommonSubsequence() {
    }

    /**
     * Длина НОП строк {@code a} и {@code b}.
     */
    public static int length(String a, String b) {
        int[][] dp = table(a, b);
        return dp[a.length()][b.length()];
    }

    /**
     * Одна из НОП; при равенстве {@code dp[i-1][j]} и {@code dp[i][j-1]} снимается символ из {@code a} (шаг «вверх»).
     */
    public static String subsequence(String a, String b) {
        int[][] dp = table(a, b);
        int m = a.length();
        int n = b.length();
        StringBuilder out = new StringBuilder(dp[m][n]);
        int i = m;
        int j = n;
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                out.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return out.reverse().toString();
    }

    private static int[][] table(String a, String b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                if (ca == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp;
    }
}
