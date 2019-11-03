/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package lsafer.util;

/**
 * Useful utils for strings.
 *
 * @author LSafer
 * @version 5 release (02-Nov-2019)
 * @since 11 Jun 2019
 */
final public class Strings {
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

//
//	/**
//	 * The prefixes used with si units. The center index is 6.
//	 *
//	 * <ul>
//	 * <li>note: ["c", "d", "da", "h"] have been removed. Because it's not multiple of 1000.</li>
//	 * </ul>
//	 */
//	final public static String[] SI_PREFIXES = {
//			/*x10^-18*/ "a", /*x10^-15*/ "f", /*x10^-12*/ "p", /*x10^-09*/ "n",
//			/*x10^-06*/ "mi", /*x10^-03*/ "m", /*x10^+00*/ "", /*x10^+03*/ "k",
//			/*x10^+06*/ "M", /*x10^+09*/ "G", /*x10^+12*/ "T", /*x10^+15*/ "F",
//			/*x10^+18*/ "E"
//	};
//	/**
//	 * Transform the given number to string and add best measurement unit for it.
//	 *
//	 * @param number to format
//	 * @return the value with unit
//	 */
//	public static String format(long number) {
//		int unit = 6;
//		for (; number > 1024 && unit < SI_PREFIXES.length - 1; unit++)
//			number /= 1024;
//
//		return number + " " + SI_PREFIXES[unit];
//	}
//
//	/**
//	 * @param string
//	 * @param afterIndex
//	 * @param start
//	 * @param stop
//	 * @return
//	 */
//	@Underdevelopment(value = "not sure that is what we want.")
//	static String collect(String string, int afterIndex, String start, String stop) {
//		int i0 = string.indexOf(start, afterIndex) + 1, i1 = string.indexOf(stop, i0);
//		return i0 > 0 && i1 >= 0 ? new String(string.toCharArray(), i0, i1 - i0) : null;
//	}