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
package lsafer.json;

import lsafer.util.Arrays;
import lsafer.util.StringParser;
import lsafer.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A text parser for JSON files.
 *
 * @author LSaferSE
 * @version 6 release (11-Oct-2019)
 * @since 09-Jul-19
 */
@SuppressWarnings("unused")
public class JSON extends StringParser {
	/**
	 * The global instance to avoid unnecessary instancing.
	 */
	final public static JSON instance = new JSON();

	/**
	 * Parse the passed json-string into a java object.
	 *
	 * @param string to be parsed
	 * @return an object parsed from the given string
	 */
	public static Object Parse(String string) {
		return instance.parse(string);
	}

	/**
	 * Stringify the given object to be a json-text.
	 *
	 * @param object to be stringed
	 * @param shift  the shift that the string should have
	 * @return a string from stringing the given object
	 */
	public static String Stringify(Object object, String shift) {
		return instance.stringify(object, shift);
	}

	/**
	 * Stringify the given object to be a json-text.
	 *
	 * @param object to be stringed
	 * @return a string from stringing the given object
	 */
	public static String Stringify(Object object) {
		return instance.stringify(object);
	}

	/**
	 * Check if the given JSON text is an {@link ArrayList array} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is an array or not
	 */
	@QueryMethod(ArrayList.class)
	public boolean is_array(String string) {
		return string.length() > 1 && (string.charAt(0) == '[' || string.charAt(1) == '[') &&
			   (string.charAt(string.length() - 1) == ']' || string.charAt(string.length() - 2) == ']');
	}

	/**
	 * Check if the given JSON text is a {@link Boolean boolean} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is an boolean or not
	 */
	@QueryMethod(Boolean.class)
	public boolean is_boolean(String string) {
		return string.equals("true") || string.equals("false");
	}

	/**
	 * Check if the given JSON text is a {@link Character character} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is a character or not
	 */
	@QueryMethod(Character.class)
	public boolean is_char(String string) {
		return string.length() == 3 && string.charAt(0) == '\'' && string.charAt(2) == '\'';
	}

	/**
	 * Check if the given JSON text is a {@link Double double} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is a double or not
	 */
	@QueryMethod(Double.class)
	public boolean is_double(String string) {
		try {
			Double.valueOf(string);
			return !string.toLowerCase().endsWith("f") &&
				   string.contains(".");
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Check if the given JSON text is a {@link Float float} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is a float or not
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
	 * Check if the given JSON text is an {@link Integer integer} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is an integer or not
	 */
	@QueryMethod(Integer.class)
	public boolean is_integer(String string) {
		try {
			Integer.valueOf(string);
			return !string.endsWith("L") &&
				   !string.endsWith("f") &&
				   !string.contains(".");
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Check if the given JSON text is a {@link Long long} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is an long or not
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
	 * Check if the given JSON text is a {@link HashMap map} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is a map or not
	 */
	@QueryMethod(HashMap.class)
	public boolean is_map(String string) {
		return string.length() > 1 && (string.charAt(0) == '{' || string.charAt(1) == '{') &&
			   (string.charAt(string.length() - 1) == '}' || string.charAt(string.length() - 2) == '}');
	}

	/**
	 * Check if the given JSON text is a {@link String string} or not.
	 *
	 * @param string JSON text to be checked
	 * @return whether the passed JSON text is a string or not
	 */
	@QueryMethod(String.class)
	public boolean is_string(String string) {
		return string.length() > 1 &&
			   string.charAt(0) == '"' &&
			   string.charAt(string.length() - 1) == '"';
	}

	/**
	 * Parse the given JSON text into an {@link ArrayList array}.
	 *
	 * @param string json text to be parsed
	 * @return an array from the given JSON text
	 */
	@SuppressWarnings("DuplicatedCode")
	@ParsingMethod
	public ArrayList parse_array(String string) {
		if (string.charAt(0) != '[' || string.charAt(string.length() - 1) != ']')
			string = '[' + string + ']';

		ArrayList<Object> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();

		boolean quotation = false, escape = false;
		int brackets = -1, karly = 0;

		for (char point : string.toCharArray()) {
			if (escape) {
				//case reading between quotation marks and the previous char is '\'
				escape = false;
				switch (point) {
					case 't':
						builder.append('\t');
						break;
					case 'n':
						builder.append('\n');
						break;
					case 'r':
						builder.append('\r');
						break;
					default:
						builder.append(point);
				}
			} else if (quotation) {
				//case reading between quotation marks
				switch (point) {
					case '\\':
						escape = true;
						break;
					case '"':
						builder.append(point);
						quotation = false;
						break;
					default:
						builder.append(point);
						break;
				}
			} else if (brackets > 0) {
				//case reading inside a brackets
				builder.append(point);
				switch (point) {
					case '[':
						brackets++;
						break;
					case ']':
						brackets--;
						break;
					case '"':
						quotation = true;
						break;
				}
			} else if (karly > 0) {
				//case reading inside a karly brackets
				builder.append(point);
				switch (point) {
					case '{':
						karly++;
						break;
					case '}':
						karly--;
						break;
					case '"':
						quotation = true;
						break;
				}
			} else {
				//case reading in the main section
				switch (point) {
					case '\n':
					case '\t':
					case '\r':
					case ' ':
					case 65533:
						break;
					case '[':
						if (brackets != -1)
							builder.append(point);
						brackets++;
						break;
					case '{':
						builder.append(point);
						karly++;
						break;
					case '"':
						builder.append(point);
						quotation = true;
						break;
					case ',':
					case ']':
						String collected = builder.toString();

						if (!collected.equals(""))
							list.add(collected.equals("#") ? list : this.parse(collected));

						builder = new StringBuilder();
						break;
					default:
						builder.append(point);
						break;
				}
			}
		}

		return list;
	}

	/**
	 * Parse the given JSON text into a {@link Boolean boolean}.
	 *
	 * @param string json text to be parsed
	 * @return a boolean from the given JSON text
	 */
	@ParsingMethod
	public Boolean parse_boolean(String string) {
		return Boolean.valueOf(string);
	}

	/**
	 * Parse the given JSON text into a {@link Character character}.
	 *
	 * @param string json text to be parsed
	 * @return a character from the given JSON text
	 */
	@ParsingMethod
	public Character parse_char(String string) {
		return string.charAt(1);
	}

	/**
	 * Parse the given JSON text into a {@link Double double}.
	 *
	 * @param string json text to be parsed
	 * @return a double from the given JSON text
	 */
	@ParsingMethod
	public Double parse_double(String string) {
		return Double.valueOf(string);
	}

	/**
	 * Parse the given JSON text into a {@link Float float}.
	 *
	 * @param string json text to be parsed
	 * @return a float from the given JSON text
	 */
	@ParsingMethod
	public Float parse_float(String string) {
		return Float.valueOf(string);
	}

	/**
	 * Parse the given JSON text into an {@link Integer integer}.
	 *
	 * @param string json text to be parsed
	 * @return an integer from the given JSON text
	 */
	@ParsingMethod
	public Integer parse_integer(String string) {
		return Integer.valueOf(string);
	}

	/**
	 * Parse the given JSON text into a {@link Long long}.
	 *
	 * @param string json text to be parsed
	 * @return a long from the given JSON text
	 */
	@ParsingMethod
	public Long parse_long(String string) {
		return Long.valueOf(string);
	}

	/**
	 * Parse the given JSON text into a {@link HashMap map}.
	 *
	 * @param string json text to be parsed
	 * @return a map from the given JSON text
	 */
	@SuppressWarnings("DuplicatedCode")
	@ParsingMethod
	public HashMap<Object, Object> parse_map(String string) {
		if (string.charAt(0) != '{' || string.charAt(string.length() - 1) != '}')
			string = '{' + string + '}';

		HashMap<Object, Object> map = new HashMap<>(); //result
		StringBuilder builder = new StringBuilder(); //temporary builder

		String key = ""; //temporary key-string holders

		boolean quotation = false, escape = false; //modes
		int brackets = 0, karly = -1; //positions

		for (char point : string.toCharArray())
			if (escape) { //escape mode
				escape = false;
				switch (point) {
					case 't':
						builder.append('\t');
						break;
					case 'n':
						builder.append('\n');
						break;
					case 'r':
						builder.append('\r');
						break;
					default:
						builder.append(point);
				}
			} else if (quotation) { //quotation mode
				switch (point) {
					case '\\':
						escape = true;
						break;
					case '"':
						builder.append(point);
						quotation = false;
						break;
					default:
						builder.append(point);
						break;
				}
			} else if (brackets > 0) { //square brackets
				builder.append(point);
				switch (point) {
					case '[':
						brackets++;
						break;
					case ']':
						brackets--;
						break;
					case '"':
						quotation = true;
						break;
				}
			} else if (karly > 0) { //karly brackets
				builder.append(point);
				switch (point) {
					case '{':
						karly++;
						break;
					case '}':
						karly--;
						break;
					case '"':
						quotation = true;
						break;
				}
			} else {
				switch (point) {
					case '\n':
					case '\t':
					case '\r':
					case ' ':
					case 65533:
						break;
					case '"':
						builder.append(point);
						quotation = true;
						break;
					case '[':
						builder.append(point);
						brackets++;
						break;
					case '{':
						if (karly != -1)
							builder.append(point);
						karly++;
						break;
					case ':':
					case '=':
						key = builder.toString();
						builder = new StringBuilder();
						break;
					case ',':
					case '}':
						String value = builder.toString();

						if (!key.equals(""))
							map.put(Arrays.contains(key, "#", "(this Map)") ? map : this.parse(key),
									Arrays.contains(value, "#", "(this Map)") ? map : this.parse(value));

						key = "";
						builder = new StringBuilder();
						break;
					default:
						builder.append(point);
						break;
				}
			}

		return map;
	}

	/**
	 * Parse the given JSON text into a {@link String string}.
	 *
	 * @param string json text to be parsed
	 * @return a string from the given JSON text
	 */
	@ParsingMethod
	public String parse_string(String string) {
		return Strings.crop(string, 1, 1);
	}

	/**
	 * Stringify the given {@link Object[] array} as a JSON text.
	 *
	 * @param array to stringify
	 * @param shift shifting string
	 * @return a JSON text from the given array
	 */
	@SuppressWarnings("DuplicatedCode")
	@StringingMethod
	public String stringify_array(Object[] array, String shift) {
		StringBuilder text = new StringBuilder();
		text.append("[");

		for (Object object : array)
			text.append(text.length() == 1 ? "" : ",")
					.append("\n\t")
					.append(shift)
					.append(object == array ? "#" : this.stringify(object, shift + "\t"));

		text.append("\n")
				.append(shift)
				.append("]");

		return text.toString();
	}

	/**
	 * Stringify the given {@link Character character} as a JSON text.
	 *
	 * @param character to stringify
	 * @return a JSON text from the given character
	 */
	@StringingMethod
	public String stringify_character(Character character) {
		return "\'" + character + "\'";
	}

	/**
	 * Stringify the given {@link Collection collection} as a JSON text.
	 *
	 * @param collection to stringify
	 * @param shift      shifting string
	 * @return a JSON text from the given collection
	 */
	@SuppressWarnings("DuplicatedCode")
	@StringingMethod
	public String stringify_collection(Collection collection, String shift) {
		StringBuilder text = new StringBuilder();
		text.append("[");

		for (Object object : collection)
			text.append(text.length() == 1 ? "" : ",")
					.append("\n\t")
					.append(shift)
					.append(object == collection ? "#" : this.stringify(object, shift + "\t"));

		text.append("\n")
				.append(shift)
				.append("]");

		return text.toString();
	}

	/**
	 * Stringify the given {@link Float float} as a JSON text.
	 *
	 * @param f float to stringify
	 * @return a JSON text from the given float
	 */
	@StringingMethod
	public String stringify_float(Float f) {
		return f + "F";
	}

	/**
	 * Stringify the given {@link Long long} as a JSON text.
	 *
	 * @param l long to stringify
	 * @return a JSON text from the given long
	 */
	@StringingMethod
	public String stringify_long(Long l) {
		return l + "L";
	}

	/**
	 * Stringify the given {@link Map map} as a JSON text.
	 *
	 * @param map   to stringify
	 * @param shift shifting string
	 * @return a JSON text from the given map
	 */
	@StringingMethod
	public String stringify_map(Map<?, ?> map, String shift) {
		StringBuilder text = new StringBuilder();
		text.append("{");

		map.forEach((key, value) ->
				text.append(text.length() == 1 ? "" : ",")
						.append("\n")
						.append(shift)
						.append("\t")
						.append(key == map ? "#" : this.stringify(key, shift + "\t"))
						.append(":\t")
						.append(value == map ? "#" : this.stringify(value, shift + "\t")));

		text.append("\n")
				.append(shift)
				.append("}");

		return text.toString();
	}

	/**
	 * Stringify the given {@link String string} as a JSON text.
	 *
	 * @param string to stringify
	 * @return a JSON text from the given string
	 */
	@StringingMethod
	public String stringify_string(String string) {
		return '"' + string.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t") + '"';
	}
}
