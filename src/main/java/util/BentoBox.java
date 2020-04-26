package util;

import java.math.BigDecimal;

/**
 * This class is designed to hold simple, generic static methods that may be
 * needed in a variety of situations. It's a grab-bag of methods or a bento-box
 * of goodness.
 * 
 * @author Sarah-Jane Whittaker
 */
public class BentoBox {
    // /////////////////////////////////////////////////////////////////
    // Static Member Variables
    // /////////////////////////////////////////////////////////////////

    /* ASCII integer representations */
    public static final int INT_ASCII_a = 97;

    public static final int INT_ASCII_DELETE = 127;

    public static final int INT_ASCII_BACKSPACE = 8;

    public static final int INT_ASCII_RETURN = 13;

    public static final int INT_ASCII_NULL = 0;

    public static final String STR_ASCII_STANDARD_RETURN = "" + ((char) 13) + ((char) 10);

    // /////////////////////////////////////////////////////////////////
    // Static Member Functions
    // /////////////////////////////////////////////////////////////////
    public static float getMinValue(float num1, float num2, float num3, float num4) {
        float min1 = (num1 < num2) ? num1 : num2;
        float min2 = (num3 < num4) ? num3 : num4;

        return ((min1 < min2) ? min1 : min2);
    }

    public static float getMaxValue(float num1, float num2, float num3, float num4) {
        float max1 = (num1 > num2) ? num1 : num2;
        float max2 = (num3 > num4) ? num3 : num4;

        return ((max1 > max2) ? max1 : max2);
    }

    public static int convertFloatToInt(float num) {
        Float floatNum = new Float(num);
        return floatNum.intValue();
    }

    public static int convertDoubleToInt(double num) {
        Double doubleNum = new Double(num);
        return doubleNum.intValue();
    }

    public static double roundDouble(double value, int numDigits) {
        BigDecimal roundDecimal = new BigDecimal(value);
        roundDecimal = roundDecimal.setScale(numDigits, BigDecimal.ROUND_UP);
        return roundDecimal.doubleValue();
    }

    /**
     * Replaces all occurences of "find" in "source" with "replace". Moves left to
     * right, so if "replace" contains "find" it will not be further modified. Use
     * this when String.replaceAll is too complicated (i.e. replace < with $<$)
     * <p>
     * author Mike Wood, modified by Sarah-Jane Whittaker
     * 
     * @param source  The source string.
     * @param find    The substring to be found.
     * @param replace The substring to replace all occurences of find.
     * @return A modified version of "source" will all occurences of "find" replaced
     *         with "replace".
     */
    public static String replaceAll(String source, String find, String replace) {
        int i = 0;
        String r = "" + source;
        String left_half = null;
        String right_half = null;

        if (find.length() > 0) {
            i = 0;
            while (i >= 0 && i < r.length()) {
                i = r.indexOf(find, i);
                if (i >= 0) {
                    left_half = (i > 0) ? r.substring(0, i) : "";
                    right_half = (i + find.length() < r.length()) ? r.substring(i + find.length()) : "";
                    r = left_half + replace + right_half;
                    i = left_half.length() + replace.length();
                }
            }
        }

        return r;
    }

}
