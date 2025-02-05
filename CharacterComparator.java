import java.util.Comparator;

/**
 * Comparator that allows for comparison of characters and
 * counting said comparisons.
 *
 * @author AKSHAT KARWA
 */
public class CharacterComparator implements Comparator<Character> {

    private int comparisonCount;

    /**
     * To be used when comparing characters. Keeps count of
     * how many times this method has been called.
     *
     * @param a first character to be compared
     * @param b second character to be compared
     * @return negative value if a is less than b, positive
     * if a is greater than b, and 0 otherwise
     */
    @Override
    public int compare(Character a, Character b) {
        comparisonCount++;
        return a - b;
    }

    /**
     * Gets the number of times compare has been used
     *
     * @return the comparison count
     */
    public int getComparisonCount() {
        return comparisonCount;
    }
}