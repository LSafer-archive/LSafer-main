package lsafer.microsoft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lsafer.util.StringParser;
import lsafer.util.Strings;

/**
 * A Text parser for INI files.
 *
 * @author LSaferSE
 * @version 3 release (28-Sep-19)
 * @since 21-Jul-19
 */
@SuppressWarnings({"unused"})
public class INI extends StringParser {
	/**
	 * The global instance to avoid unnecessary instancing.
	 */
	final public static INI instance = new INI();

	/**
	 * Check if the given INI text is an {@link ArrayList array} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is an array or not
	 */
	@QueryMethod(ArrayList.class)
	public boolean is_array(String string) {
		return string.contains(",");
	}

	/**
	 * Check if the given INI text is a {@link Boolean boolean} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is a boolean or not
	 */
	@QueryMethod(Boolean.class)
	public boolean is_boolean(String string) {
		return string.equals("true") || string.equals("false");
	}

	/**
	 * Check if the given INI text is a {@link Double double} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is a double or not
	 */
	@QueryMethod(Double.class)
	public boolean is_double(String string) {
		try {
			Double.valueOf(string);
			return !string.endsWith("f") &&
				   string.contains(".");
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Check if the given INI text is a {@link Float float} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is a float or not
	 */
	@QueryMethod(Float.class)
	public boolean is_float(String string) {
		try {
			Float.valueOf(string);
			return string.toUpperCase().endsWith("F");
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Check if the given INI text is an {@link Integer integer} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is an integer or not
	 */
	@QueryMethod(Integer.class)
	public boolean is_integer(String string) {
		try {
			Integer.valueOf(string);
			return !string.endsWith("L") &&
				   !string.endsWith("f") &&
				   !string.contains(".");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check if the given INI text is a {@link Long long} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is an long or not
	 */
	@QueryMethod(Long.class)
	public boolean is_long(String string) {
		try {
			Long.valueOf(string);
			return string.toUpperCase().endsWith("L") &&
				   !string.contains(".");
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Check if the given INI text is a {@link HashMap map} or not.
	 *
	 * @param string INI text to be checked
	 * @return whether the passed INI text is an map or not
	 */
	@QueryMethod(HashMap.class)
	public boolean is_map(String string) {
		return string.split("\n").length > 1;
	}

	/**
	 * Parse the given INI text into an {@link ArrayList array}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public ArrayList<Object> parse_array(String string) {
		ArrayList<Object> list = new ArrayList<>();

		for (String element : string.split(","))
			list.add(this.parse(element));

		return list;
	}

	/**
	 * Parse the given INI text into a {@link Boolean boolean}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public Boolean parse_boolean(String string) {
		return Boolean.valueOf(string);
	}

	/**
	 * Parse the given INI text into a {@link Double double}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public Double parse_double(String string) {
		return Double.valueOf(string);
	}

	/**
	 * Parse the given INI text into a {@link Float float}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public Float parse_float(String string) {
		return Float.valueOf(string);
	}

	/**
	 * Parse the given INI text into an {@link Integer integer}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public Integer parse_integer(String string) {
		return Integer.valueOf(string);
	}

	/**
	 * Parse the given INI text into a {@link Long long}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public Long parse_long(String string) {
		return Long.valueOf(string);
	}

	/**
	 * Parse the given INI text into an {@link HashMap map}.
	 *
	 * @param string INI text to be casted
	 * @return an object that matches the given INI text
	 */
	@ParsingMethod
	public HashMap<String, Object> parse_map(String string) {
		string = Strings.replace(string, "", "\r", "\t", "\u0000", String.valueOf((char) 65533));

		HashMap<String, Object> main = new HashMap<>();
		HashMap<String, Object> inner = null;

		for (String line : string.split("\n"))
			if (line.length() > 2)
				if (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']') {
					main.put(Strings.crop(line, 1, 1), inner = new HashMap<>());

				} else if (line.charAt(0) != ';') {
					String[] split = line.split("=");

					if (split.length == 2)
						(inner == null ? main : inner).put(split[0], this.parse(split[1]));
				}

		return main;
	}

	/**
	 * Stringify the given {@link Object[] array} to an INI text.
	 *
	 * @param array to stringify
	 * @return an INI text from the given array
	 */
	@StringingMethod
	public String stringify_array(Object[] array) {
		StringBuilder string = new StringBuilder();

		for (Object object : array)
			string.append(string.length() == 0 ? "" : ",")
					.append(this.stringify(object, ""));

		return string.toString();
	}

	/**
	 * Stringify the given {@link Collection collection} to an INI text.
	 *
	 * @param collection to stringify
	 * @return an INI text from the given collection
	 */
	@StringingMethod
	public String stringify_collection(Collection<?> collection) {
		StringBuilder string = new StringBuilder();

		for (Object object : collection)
			string.append(string.length() == 0 ? "" : ",")
					.append(this.stringify(object, ""));

		return string.toString();
	}

	/**
	 * Stringify the given {@link Float float} to an INI text.
	 *
	 * @param f float to stringify
	 * @return an INI text from the given float
	 */
	@StringingMethod
	public String stringify_float(Float f) {
		return f + "F";
	}

	/**
	 * Stringify the given {@link Long long} to an INI text.
	 *
	 * @param l long to stringify
	 * @return an INI text from the given long
	 */
	@StringingMethod
	public String stringify_long(Long l) {
		return l + "L";
	}

	/**
	 * Stringify the given {@link Map map} to an INI text.
	 *
	 * @param map to stringify
	 * @return an INI text from the given map
	 */
	@StringingMethod
	public String stringify_map(Map<?, ?> map) {
		StringBuilder nodes = new StringBuilder();
		StringBuilder maps = new StringBuilder();

		int[] ints = {0};
		map.forEach((key, value) -> {
			if (value instanceof Map)
				maps.append(ints[0]++ == 0 ? "" : "\n\n")
						.append("[")
						.append(this.stringify(key))
						.append("]")
						.append("\n")
						.append(this.stringify(value));
			else nodes.append(ints[0]++ == 0 ? "" : "\n")
					.append(this.stringify(key))
					.append("=")
					.append(this.stringify(value));
		});

		return nodes.append(maps).toString();
	}
}
