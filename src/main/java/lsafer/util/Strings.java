package lsafer.util;

/**
 * Useful utils for strings.
 *
 * @author LSafer
 * @version 4 release (06-Seo-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
final public class Strings {
	/**
	 * The prefixes used with si units. The center index is 6.
	 *
	 * <ul>
	 * <li>note: ["c", "d", "da", "h"] have been removed. Because it's not multiple of 1000.</li>
	 * </ul>
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
	 * This is a util class. And shall not be instanced as an object.
	 */
	private Strings() {
	}

	/**
	 * Check if the given string contains all of the given queries.
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
	 * Check if the given string contains any of the given queries.
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
	 * Remove first/last characters with specific range.
	 * <br><br><b>example:</b>
	 * <pre>
	 * crop("example string", 2, 3) == "ample str"
	 * </pre>
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
	 * Transform the given number to string and add best measurement unit for it.
	 *
	 * @param number to format
	 * @return the value with unit
	 */
	public static String format(long number) {
		int unit = 6;
		for (; number > 1024 && unit < SI_PREFIXES.length - 1; unit++)
			number /= 1024;
		return number + " " + SI_PREFIXES[unit];
	}

	/**
	 * Joining the given strings accordingly with a specific query between them.
	 * <br><br><b>example</b>
	 * <pre>
	 * join("/", "abc", "def", "ghi") == "abc/def/ghi"
	 * </pre>
	 *
	 * @param joiner  query to add between strings
	 * @param string  to start with
	 * @param strings to add to the main string
	 * @return given strings joined accordingly with a specific query between them
	 */
	public static String join(String joiner, String string, String... strings) {
		StringBuilder builder = new StringBuilder(string);

		if (!string.equals("") && strings.length > 0)
			builder.append(strings[0]);

		for (int i = 1; i < strings.length; i++)
			builder.append(joiner).append(strings[i]);

		return builder.toString();
	}

	/**
	 * Get given string repeated many times as given.
	 * <br><br><b>example</b>
	 * <pre>
	 * repetitive("abc", " ", 3) == "abc abc abc "
	 * </pre>
	 *
	 * @param string  to repeat from
	 * @param spacing to be in the middle of the repeated strings
	 * @param times   to repeat
	 * @return new string created from repeated given string
	 */
	public static String repetitive(String string, String spacing, int times) {
		StringBuilder builder = times < 1 ? new StringBuilder() : new StringBuilder(string);

		for (int i = 2; i <= times; i++)
			builder.append(spacing).append(string);

		return builder.toString();
	}

	/**
	 * From the given string. Replace all given queries with the given replacement.
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
