package lsafer.util;

/**
 * useful utils for strings.
 *
 * @author LSafer
 * @version 3
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess"})
final public class Strings {

    /**
     * the prefixes used with si units.
     * <p>
     * i removed "c", "d", "da", "h"
     * because it's not multiple of 1,000
     * center index = 6
     */
    final public static String[] SI_PREFIXES = {
            "a",        //x10^-18
            "f",        //x10^-15
            "p",        //x10^-12
            "n",        //x10^-9
            "mi",       //x10^-6
            "m",        //x10^-3
            /*"c",*/    //x10^-2
            /*"d",*/    //x10^-1
            "",         //x10^0
            /*"da",*/   //x10^1
            /*"h",*/    //x10^2
            "k",        //x10^3
            "M",        //x10^6
            "G",        //x10^9
            "T",        //x10^12
            "F",        //x10^15
            "E",        //x10^18
    };

    /**
     * this is a util class and shall
     * not be instanced as an object.
     */
    private Strings() {

    }

    /**
     * check if the given string contains
     * all of the given queries.
     *
     * @param string  to check
     * @param queries to check with
     * @return if given string contains all of the given query
     */
    public static boolean all(String string, CharSequence... queries) {
        for (CharSequence q : queries)
            if (!string.contains(q))
                return false;

        return true;
    }

    /**
     * check if the given string contains
     * any of the given queries.
     *
     * @param string  to check
     * @param queries to check with
     * @return if given string contains any of the given query
     */
    public static boolean any(String string, CharSequence... queries) {
        for (CharSequence q : queries)
            if (string.contains(q))
                return true;

        return false;
    }

    /**
     * remove first/last characters with specific range.
     * <p>
     * example :
     * crop("example string", 2, 3) == "ample str"
     *
     * @param string String to crop
     * @param start  range to remove
     * @param end    range to remove
     * @return cropped string
     */
    public static String crop(String string, int start, int end) {
        return String.copyValueOf(string.toCharArray(), start, string.length() - start - end);

    }

    /**
     * transform the given number to string
     * and add best measurement unit for it.
     *
     * @param number to format
     * @return : the value with unit
     */
    public static String format(long number) {
        int unit = 6;
        for (; number > 1024 && unit < SI_PREFIXES.length - 1; unit++)
            number /= 1024;
        return number + " " + SI_PREFIXES[unit];
    }

    /**
     * joining the given strings accordingly
     * with a specific query between them.
     * <p>
     * example :
     * join(" / ", "abc", "def", "ghi") == "abc/def/ghi"
     *
     * @param joiner  query to add between strings
     * @param string  to start with
     * @param strings to add to the main string
     * @return given strings joined accordingly with a specific query between them
     */
    public static String join(String joiner, String string, String... strings) {
        for (String string2 : strings)
            //noinspection StringConcatenationInLoop
            string += (string.equals("") ? "" : joiner) + string2;
        return string;
    }

    /**
     * run the margin queries of the given string
     * and fill it with new string.
     *
     * @param string to run from
     * @param fill   characters to fill between
     * @param start  margins length on the given string
     * @param end    margins length on the given string
     * @return : string with new content but with the same margins
     */
    public static String refill(String string, String fill, int start, int end) {
        String[] split = string.split("");
        String res = "";

        for (int i = 0; i < start; i++)
            //noinspection StringConcatenationInLoop
            res += split[i];

        res += fill;

        for (int i = split.length - end; i < split.length; i++)
            //noinspection StringConcatenationInLoop
            res += split[i];

        return res;
    }

    /**
     * run given string repeated many times as given.
     * <p>
     * example :
     * repetitive("abc ", 3) == "abc abc abc"
     *
     * @param string to repeat from
     * @param times  to repeat
     * @return new string created from repeated given string
     */
    public static String repetitive(String string, int times) {
        String str = "";
        for (int i = 0; i < times; i++)
            //noinspection StringConcatenationInLoop
            str += string;
        return str;
    }

    /**
     * from the given string
     * replace all given queries
     * with given replacement.
     *
     * @param string      to replace from
     * @param replacement string to replace with
     * @param queries     to replace
     * @return string with replaced queries from it
     */
    public static String replace(String string, CharSequence replacement, CharSequence... queries) {
        for (CharSequence query : queries)
            string = string.replace(query, replacement);
        return string;
    }

}
