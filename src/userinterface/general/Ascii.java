package userinterface.general;

import java.io.File;

/**
 * This class provides various character and String manipulation and testing
 * methods.
 * 
 * @author Michael Wood
 */
public final class Ascii {
    /**
     * Integer representations of ascii characters.
     */
    public static final int a = 97, DELETE = 127, BACKSPACE = 8, RETURN = 13,
            NULL = 0;

    /**
     * Standard symbol for RETURN
     */
    public static final String STANDARD_RETURN = "" + ((char) 13) + ((char) 10);

    /**
     * Custom symbol for RETURN
     */
    public static final String CUSTOM_RETURN = "&#13";

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // static methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getFileNameFromPath(String path) {
        if (path != null) {
            int slash_position = path.lastIndexOf(File.separator);
            if (slash_position > 0 && slash_position < path.length() - 1) {
                return path.substring(slash_position + 1);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getFileNameFromPathWithNewExtension(String path,
            String extension) {
        String file_name = getFileNameFromPath(path);
        int dot_position = file_name.lastIndexOf(".");
        if (dot_position > 0) {
            return file_name.substring(0, dot_position) + extension;
        } else {
            return "";
        }
    }

    public static String escapeReturn(String normal) {
        return replaceAll(normal, Ascii.STANDARD_RETURN, Ascii.CUSTOM_RETURN);
    }

    public static String unEscapeReturn(String escaped) {
        return replaceAll(escaped, Ascii.CUSTOM_RETURN, Ascii.STANDARD_RETURN);
    }

    /**
     * Cast the given character into a String.
     * 
     * @param c
     *            The character to be cast into a String.
     * @return A String representation of the input character.
     */
    public static String string(int c) {
        return "" + (char) c;
    }

    /**
     * Count the number of occurances of the character c in the String s.
     * 
     * @param s
     *            The String to be tested.
     * @param c
     *            The character to be counted.
     * @return The number of occurances of the character c in the String s.
     */
    public static int occurrances(String s, int c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == (char) c) {
                count++;
            }
        }
        return count;
    }

    /**
     * Test if the input is a "normal" character (0x20-0x7E or CR). That is all
     * alphanumeric and punctuation and only CR from the special characters
     * 
     * @param c
     *            The character to be tested
     * @return true if the test character is "safe"
     */
    public static boolean isSafe(char c) {
        int i = (int) c;
        if ((i > 31 && i < 127) || i == 13) {
            return true;
        }
        return false;
    }

    /**
     * Replaces all occurences of "find" in "source" with "replace". Moves left
     * to right, so if "replace" contains "find" it will not be further
     * modified. Use this when String.replaceAll is too complicated (i.e.
     * replace < with $<$)
     * 
     * @param source
     *            The source string.
     * @param find
     *            The substring to be found.
     * @param replace
     *            The substring to replace all occurences of find.
     * @return A modified version of "source" will all occurences of "find"
     *         replaced with "replace".
     */
    public static String replaceAll(String source, String find, String replace) {
        String r = "" + source;
        String left_half, right_half;
        if (find.length() > 0) {
            int i = 0;
            while (i >= 0 && i < r.length()) {
                i = r.indexOf(find, i);
                if (i >= 0) {
                    if (i > 0) {
                        left_half = r.substring(0, i);
                    } else {
                        left_half = "";
                    }
                    if (i + find.length() < r.length()) {
                        right_half = r.substring(i + find.length());
                    } else {
                        right_half = "";
                    }
                    r = left_half + replace + right_half;
                    i = left_half.length() + replace.length();
                }
            }
        }
        return r;
    }

    public static int safeInt(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    public static float safeFloat(String value) {
        float f = 0;
        try {
            f = Float.parseFloat(value);
            return f;
        } catch (Exception e) {
            return 0;
        }
    }
}
