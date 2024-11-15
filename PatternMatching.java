import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * My implementations of various string searching algorithms.
 *
 * @author AKSHAT KARWA
 */
public class PatternMatching {

    /**
     * Knuth-Morris-Pratt (KMP) algorithm relies on the failure table (also
     * called failure function). Works better with small alphabets.
     *
     * We have implemented the buildFailureTable() method which we will use 
     * in this method.
     *
     * @param pattern    the pattern you are searching for in a body of text
     * @param text       the body of text where you search for pattern
     * @param comparator comparator to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> kmp(CharSequence pattern, CharSequence text,
                                    CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Pattern cannot be null or have length 0!!");
        } else if (text == null || comparator == null) {
            throw new IllegalArgumentException("Text or comparator cannot be null!!");
        }
        List<Integer> list = new ArrayList<>();
        if (pattern.length() <= text.length()) {
            int[] failureTable = buildFailureTable(pattern, comparator);
            int patternIndex = 0;
            int textIndex = 0;
            while ((text.length() - textIndex) >= (pattern.length() - patternIndex)) {
                if (comparator.compare(pattern.charAt(patternIndex), text.charAt(textIndex)) == 0) {
                    if (patternIndex == (pattern.length() - 1)) {
                        list.add(textIndex - patternIndex);
                        patternIndex = failureTable[pattern.length() - 1];
                    } else {
                        patternIndex++;
                    }
                    textIndex++;
                } else if (patternIndex == 0) {
                    textIndex++;
                } else {
                    patternIndex = failureTable[patternIndex - 1];
                }
            }
        }
        return list;
    }

    /**
     * Builds failure table that will be used to run the Knuth-Morris-Pratt
     * (KMP) algorithm.
     *
     * The table built has the length of the input pattern.
     *
     * Note that a given index i will contain the length of the largest prefix
     * of the pattern indices [0..i] that is also a suffix of the pattern
     * indices [1..i]. This means that index 0 of the returned table will always
     * be equal to 0.
     *
     * Ex. pattern = ababac
     *
     * table[0] = 0
     * table[1] = 0
     * table[2] = 1
     * table[3] = 2
     * table[4] = 3
     * table[5] = 0
     *
     * If the pattern is empty, we return an empty array.
     *
     * @param pattern    a pattern we're building a failure table for
     * @param comparator comparator to check if characters are equal
     * @return integer array holding our failure table
     * @throws java.lang.IllegalArgumentException if the pattern or comparator
     *                                            is null
     */
    public static int[] buildFailureTable(CharSequence pattern,
                                          CharacterComparator comparator) {
        if (pattern == null || comparator == null) {
            throw new IllegalArgumentException("Pattern or comparator is null!!");
        }
        int[] failureTable = new int[pattern.length()];
        if (pattern.length() == 0) {
            return failureTable;
        }
        int prefixIndex = 0;
        int queryIndex = 1;
        failureTable[0] = 0;
        while (queryIndex < pattern.length()) {
            if (comparator.compare(pattern.charAt(prefixIndex), pattern.charAt(queryIndex)) == 0) {
                failureTable[queryIndex++] = ++prefixIndex;
            } else if (prefixIndex == 0) {
                failureTable[queryIndex++] = 0;
            } else {
                prefixIndex = failureTable[prefixIndex - 1];
            }
        }
        return failureTable;
    }

    /**
     * Boyer Moore algorithm that relies on last occurrence table. Works better
     * with large alphabets.
     *
     * @param pattern    the pattern we are searching for in a body of text
     * @param text       the body of text where we search for the pattern
     * @param comparator comparator to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> boyerMoore(CharSequence pattern,
                                           CharSequence text,
                                           CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Pattern is null or has length 0!!");
        } else if (text == null || comparator == null) {
            throw new IllegalArgumentException("Text is null or Comparator is null!!");
        }
        List<Integer> list = new ArrayList<>();
        if (pattern.length() <= text.length()) {
            HashMap<Character, Integer> lastTable = (HashMap<Character, Integer>) buildLastTable(pattern);
            int index = 0;
            while (index <= (text.length() - pattern.length())) {
                int indexToCompare = (pattern.length() - 1);
                while (indexToCompare >= 0 && comparator.compare(text.charAt(index + indexToCompare),
                        pattern.charAt(indexToCompare)) == 0) {
                    indexToCompare--;
                }
                if (indexToCompare == -1) {
                    list.add(index);
                    index++;
                } else {
                    int shift = lastTable.getOrDefault(text.charAt(index + indexToCompare), -1);
                    if (shift < indexToCompare) {
                        index += indexToCompare - shift;
                    } else {
                        index++;
                    }
                }
            }
        }
        return list;
    }

    /**
     * Builds last occurrence table that will be used to run the Boyer Moore
     * algorithm.
     *
     * Note that each char x will have an entry at table.get(x).
     * Each entry should be the last index of x where x is a particular
     * character in our pattern.
     * If x is not in the pattern, then the table will not contain the key x,
     * and we will have to check for that in our Boyer Moore implementation.
     *
     * Ex. pattern = octocat
     *
     * table.get(o) = 3
     * table.get(c) = 4
     * table.get(t) = 6
     * table.get(a) = 5
     * table.get(everything else) = null, which you will interpret in
     * Boyer-Moore as -1
     *
     * If the pattern is empty, returns an empty map.
     *
     * @param pattern a pattern we are building last table for
     * @return a Map with keys of all of the characters in the pattern mapping
     * to their last occurrence in the pattern
     * @throws java.lang.IllegalArgumentException if the pattern is null
     */
    public static Map<Character, Integer> buildLastTable(CharSequence pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern to build last table cannot be null!!");
        }
        int m = pattern.length();
        HashMap<Character, Integer> lastTable = new HashMap<>();
        for (int i = 0; i < m; i++) {
            lastTable.put(pattern.charAt(i), i);
        }
        return lastTable;
    }

    /**
     * Prime base used for Rabin-Karp hashing.
     */
    private static final int BASE = 113;

    /**
     * Runs the Rabin-Karp algorithm. This algorithms generates hashes for the
     * pattern and compares this hash to substrings of the text before doing
     * character by character comparisons.
     *
     * When the hashes are equal and we do character comparisons, we compare
     * starting from the beginning of the pattern to the end, not from the end
     * to the beginning.
     *
     * We use the Rabin-Karp Rolling Hash for this implementation. The
     * formula for it is:
     *
     * sum of: c * BASE ^ (pattern.length - 1 - i)
     *   c is the integer value of the current character, and
     *   i is the index of the character
     *
     * We build the hash for the pattern and the first m characters
     * of the text by starting at index (m - 1) to efficiently exponentiate the
     * BASE. This allows us to avoid using Math.pow().
     *
     * Note that if we were dealing with very large numbers here, our hash
     * will likely overflow; we will not handle this case not.
     * We assume that all powers and calculations CAN be done without
     * overflow. 
     *
     * Ex. Hashing "bunn" as a substring of "bunny" with base 113
     * = (b * 113 ^ 3) + (u * 113 ^ 2) + (n * 113 ^ 1) + (n * 113 ^ 0)
     * = (98 * 113 ^ 3) + (117 * 113 ^ 2) + (110 * 113 ^ 1) + (110 * 113 ^ 0)
     * = 142910419
     *
     * Another key point of this algorithm is that updating the hash from
     * one substring to the next substring has to be O(1). To update the hash,
     * we subtract the oldChar times BASE raised to the length - 1, multiply by
     * BASE, and add the newChar as shown by this formula:
     * (oldHash - oldChar * BASE ^ (pattern.length - 1)) * BASE + newChar
     *
     * Ex. Shifting from "bunn" to "unny" in "bunny" with base 113
     * hash("unny") = (hash("bunn") - b * 113 ^ 3) * 113 + y
     *              = (142910419 - 98 * 113 ^ 3) * 113 + 121
     *              = 170236090
     *
     * Keep in mind that calculating exponents is not O(1) in general, so we'll
     * need to keep track of what BASE^(m - 1) is for updating the hash.
     *
     * @param pattern    a string we're searching for in a body of text
     * @param text       the body of text where we search for pattern
     * @param comparator comparator to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> rabinKarp(CharSequence pattern,
                                          CharSequence text,
                                          CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Pattern cannot be null or have length 0!!");
        } else if (text == null || comparator == null) {
            throw new IllegalArgumentException("Text or comparator cannot be null!!");
        }
        List<Integer> list = new ArrayList<>();
        if (pattern.length() <= text.length()) {
            int lenMinus1 = pattern.length() - 1;
            int patternHashCode = ((Character) (pattern.charAt(lenMinus1))).hashCode();
            int textHashCode = ((Character) (text.charAt(lenMinus1))).hashCode();
            int basePower = 1;
            int counter = 1;
            while (counter < pattern.length()) {
                basePower *= BASE;
                patternHashCode += ((Character) (pattern.charAt(lenMinus1 - counter))).hashCode() * basePower;
                textHashCode += ((Character) (text.charAt(lenMinus1 - counter))).hashCode() * basePower;
                counter++;
            }
            int textIndex = 0;
            while (textIndex <= (text.length() - pattern.length())) {
                if (patternHashCode == textHashCode) {
                    int index = textIndex;
                    int patternIndex = 0;
                    boolean equal = true;
                    while (patternIndex < pattern.length() && equal) {
                        equal = false;
                        if (comparator.compare(pattern.charAt(patternIndex++), text.charAt(index++)) == 0) {
                            equal = true;
                        }
                    }
                    if ((patternIndex - 1) == (lenMinus1)) {
                        list.add(textIndex);
                    }
                }
                if (textIndex < (text.length() - pattern.length())) {
                    int oldCharHash = ((Character) (text.charAt(textIndex))).hashCode();
                    int newCharHash = ((Character) (text.charAt(textIndex + pattern.length()))).hashCode();
                    textHashCode = ((textHashCode - (oldCharHash * basePower)) * BASE) + newCharHash;
                }
                textIndex++;
            }
        }
        return list;
    }

    /**
     * The Galil Rule is an addition to Boyer Moore that optimizes how we shift the pattern
     * after a full match.
     * Utilizes the buildLastTable() method and the buildFailureTable() method.
     *
     * @param pattern    the pattern we are searching for in a body of text
     * @param text       the body of text where we search for the pattern
     * @param comparator comparator to check if characters are equal
     * @return list containing the starting index for each match found
     * @throws java.lang.IllegalArgumentException if the pattern is null or has
     *                                            length 0
     * @throws java.lang.IllegalArgumentException if text or comparator is null
     */
    public static List<Integer> boyerMooreGalilRule(CharSequence pattern,
                                          CharSequence text,
                                          CharacterComparator comparator) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Pattern is null or has length 0!!");
        } else if (text == null || comparator == null) {
            throw new IllegalArgumentException("Text or comparator cannot be null!!");
        }
        List<Integer> list = new ArrayList<>();
        if (pattern.length() <= text.length()) {
            HashMap<Character, Integer> lastTable = (HashMap<Character, Integer>) buildLastTable(pattern);
            int[] failureTable = buildFailureTable(pattern, comparator);
            int periodicity = pattern.length() - failureTable[pattern.length() - 1];
            int index = 0;
            boolean galilRule = false;
            while (index <= (text.length() - pattern.length())) {
                int indexToCompare = (pattern.length() - 1);
                while (indexToCompare >= 0 && comparator.compare(text.charAt(index + indexToCompare),
                        pattern.charAt(indexToCompare)) == 0) {
                    if (galilRule && indexToCompare == (pattern.length() - periodicity)) {
                        indexToCompare = -1;
                        break;
                    }
                    indexToCompare--;
                }
                if (indexToCompare == -1) {
                    list.add(index);
                    galilRule = true;
                    index += periodicity;
                } else {
                    int shift = lastTable.getOrDefault(text.charAt(index + indexToCompare), -1);
                    if (shift < indexToCompare) {
                        index += indexToCompare - shift;
                    } else {
                        index++;
                    }
                    galilRule = false;
                }
            }
        }
        return list;
    }
}