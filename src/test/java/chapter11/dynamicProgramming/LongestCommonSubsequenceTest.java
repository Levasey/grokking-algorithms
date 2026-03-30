package chapter11.dynamicProgramming;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongestCommonSubsequenceTest {

    @Test
    public void length_emptyStrings() {
        assertEquals(0, LongestCommonSubsequence.length("", ""));
        assertEquals(0, LongestCommonSubsequence.length("", "abc"));
        assertEquals(0, LongestCommonSubsequence.length("abc", ""));
    }

    @Test
    public void length_fishAndFosh_isThree() {
        assertEquals(3, LongestCommonSubsequence.length("fish", "fosh"));
    }

    @Test
    public void length_identical_isFullLength() {
        assertEquals(4, LongestCommonSubsequence.length("abcd", "abcd"));
    }

    @Test
    public void subsequence_lengthMatchesReturnedString() {
        String a = "fish";
        String b = "fosh";
        String s = LongestCommonSubsequence.subsequence(a, b);
        assertEquals(3, s.length());
        assertEquals(LongestCommonSubsequence.length(a, b), s.length());
        assertTrue(isSubsequence(s, a));
        assertTrue(isSubsequence(s, b));
    }

    @Test
    public void subsequence_bd_inAbcdAndBd() {
        assertEquals("bd", LongestCommonSubsequence.subsequence("abcd", "bd"));
    }

    private static boolean isSubsequence(String sub, String s) {
        int j = 0;
        for (int i = 0; i < s.length() && j < sub.length(); i++) {
            if (s.charAt(i) == sub.charAt(j)) {
                j++;
            }
        }
        return j == sub.length();
    }
}
