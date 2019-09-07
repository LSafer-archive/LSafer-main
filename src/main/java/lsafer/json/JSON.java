package lsafer.json;

import lsafer.util.ArrayStructure;
import lsafer.util.Strings;
import lsafer.util.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Useful utils for using JSON.
 * <br>
 * Including :
 * <ul>
 * <li>Transforming {@link Object objects} into a JSON style text</li>
 * <li>Casting objects from JSON text into a Java {@link Object object}</li>
 * </ul>
 * <br>
 *
 * @author LSaferSE
 * @version 4 release (06-Sep-2019)
 * @since 09-Jul-19
 */
@SuppressWarnings({"WeakerAccess"})
final public class JSON {
    /**
     * This is a util class. And shall not be instanced as an object.
     */
    private JSON() {
    }

    /**
     * Check if the given JSON text is an {@link Object[] array} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is an array or not
     */
    public static boolean is_array(String string) {
        return string.length() > 1 && (string.charAt(0) == '[' || string.charAt(1) == '[') &&
               (string.charAt(string.length() - 1) == ']' || string.charAt(string.length() - 2) == ']');
    }

    /**
     * Check if the given JSON text is a {@link Boolean[] boolean} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a boolean or not
     */
    public static boolean is_boolean(String string) {
        return string.equals("true") || string.equals("false");
    }

    /**
     * Check if the given JSON text is a {@link Character character} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a character or not
     */
    public static boolean is_char(String string) {
        return string.length() == 3 && string.charAt(0) == '\'' && string.charAt(2) == '\'';
    }

    /**
     * Check if the given JSON text is a {@link Double double} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a double or not
     */
    public static boolean is_double(String string) {
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
    public static boolean is_float(String string) {
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
    public static boolean is_integer(String string) {
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
    public static boolean is_long(String string) {
        try {
            Long.valueOf(string);
            return string.toUpperCase().endsWith("L") &&
                   !string.contains(".");
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Check if the given JSON text is a {@link Map map} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a map or not
     */
    public static boolean is_map(String string) {
        return string.length() > 1 && (string.charAt(0) == '{' || string.charAt(1) == '{') &&
               (string.charAt(string.length() - 1) == '}' || string.charAt(string.length() - 2) == '}');
    }

    /**
     * Check if the given JSON text is a {@link String string} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a string or not
     */
    public static boolean is_string(String string) {
        return string.length() > 1 &&
               string.charAt(0) == '"' &&
               string.charAt(string.length() - 1) == '"';
    }

    /**
     * Cast the given JSON text to {@link Object object}.
     *
     * @param string json text to be casted
     * @return an object that matches the given JSON text
     */
    public static Object parse(String string) {
        if (string == null || string.equals("null") || string.equals(""))
            return null;
        if (JSON.is_string(string))
            return JSON.parse_string(string);
        if (JSON.is_char(string))
            return JSON.parse_char(string);
        if (JSON.is_boolean(string))
            return Boolean.valueOf(string);
        if (JSON.is_float(string))
            return Float.valueOf(string);
        if (JSON.is_double(string))
            return Double.valueOf(string);
        if (JSON.is_long(string))
            return Long.valueOf(string);
        if (JSON.is_integer(string))
            return Integer.valueOf(string);
        if (JSON.is_array(string))
            return JSON.parse_array(string);
        if (JSON.is_map(string))
            return JSON.parse_map(string);

        return string;
    }

    /**
     * Cast the given JSON text to {@link Object[] object array}.
     *
     * @param string json text to be casted
     * @return an object array from the given JSON text
     */
    public static Object[] parse_array(String string) {
        //remove main braces case exist
        if (string.charAt(0) == '[' && string.charAt(string.length() - 1) == ']')
            string = Strings.crop(string, 1, 1);

        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        boolean[] skip0 = {false /*quotation mode*/, false /*escaping mode*/};
        int[] skip1 = {0 /*array braces mode*/, 0/*map braces mode*/};

        for (char point : string.toCharArray()) {
            if (skip0[1]) {
                //case reading between quotation marks and the previous char is '\'
                skip0[1] = false;
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
            } else if (skip0[0]) {
                //case reading between quotation marks
                switch (point) {
                    case '\\':
                        skip0[1] = true;
                        break;
                    case '"':
                        builder.append(point);
                        skip0[0] = false;
                        break;
                    default:
                        builder.append(point);
                        break;
                }
            } else if (skip1[0] > 0) {
                //case reading inside a brackets
                builder.append(point);
                switch (point) {
                    case '[':
                        skip1[0]++;
                        break;
                    case ']':
                        skip1[0]--;
                        break;
                    case '"':
                        skip0[0] = true;
                        break;
                }
            } else if (skip1[1] > 0) {
                //case reading inside a karly brackets
                builder.append(point);
                switch (point) {
                    case '{':
                        skip1[1]++;
                        break;
                    case '}':
                        skip1[1]--;
                        break;
                    case '"':
                        skip0[0] = true;
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
                        builder.append(point);
                        skip1[0]++;
                        break;
                    case '{':
                        builder.append(point);
                        skip1[1]++;
                        break;
                    case '"':
                        builder.append(point);
                        skip0[0] = true;
                        break;
                    case ',':
                        list.add(JSON.parse(builder.toString()));
                        builder = new StringBuilder();
                        break;
                    default:
                        builder.append(point);
                        break;
                }
            }
        }

        //leftovers
        if (builder.length() != 0)
            list.add(JSON.parse(builder.toString()));

        return list.toArray();
    }

    /**
     * Cast the given JSON text to {@link Character character}.
     *
     * @param string json text to be casted
     * @return an object array from the given JSON text
     */
    public static char parse_char(String string) {
        return string.charAt(1);
    }

    /**
     * Cast the given JSON text to {@link Map map}.
     *
     * @param string json text to be casted
     * @return a map object from the given JSON text
     */
    public static Map<Object, Object> parse_map(String string) {
        //remove main braces case exist
        if (string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}')
            string = Strings.crop(string, 1, 1);

        HashMap<Object, Object> map = new HashMap<>(); //result
        StringBuilder value_builder = new StringBuilder(); //temporary val holder
        StringBuilder key_builder = new StringBuilder(); //temporary key holder
        StringBuilder builder = key_builder; //temporary builder

        boolean[] skip0 = {false /*quotation mode*/, false /*escaping mode*/}; //string, slash
        int[] skip1 = {0 /*array braces mode*/, 0 /*map braces mode*/}; //bracket, karly bracket

        for (char point : string.toCharArray()) {
            if (skip0[1]) {
                //case reading between quotation marks and the previous char is '\'
                skip0[1] = false;
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
            } else if (skip0[0]) {
                //case reading between quotation marks
                switch (point) {
                    case '\\':
                        skip0[1] = true;
                        break;
                    case '"':
                        builder.append(point);
                        skip0[0] = false;
                        break;
                    default:
                        builder.append(point);
                        break;
                }
            } else if (skip1[0] > 0) {
                //case reading inside a brackets
                builder.append(point);
                switch (point) {
                    case '[':
                        skip1[0]++;
                        break;
                    case ']':
                        skip1[0]--;
                        break;
                    case '"':
                        skip0[0] = true;
                        break;
                }
            } else if (skip1[1] > 0) {
                //case reading inside a karly brackets
                builder.append(point);
                switch (point) {
                    case '{':
                        skip1[1]++;
                        break;
                    case '}':
                        skip1[1]--;
                        break;
                    case '"':
                        skip0[0] = true;
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
                        builder.append(point);
                        skip1[0]++;
                        break;
                    case '{':
                        builder.append(point);
                        skip1[1]++;
                        break;
                    case '"':
                        builder.append(point);
                        skip0[0] = true;
                        break;
                    case ':':
                    case '=':
                        builder = value_builder;
                        break;
                    case ',':
                        map.put(JSON.parse(key_builder.toString()), JSON.parse(value_builder.toString()));
                        key_builder = new StringBuilder();
                        value_builder = new StringBuilder();
                        builder = key_builder;
                        break;
                    default:
                        builder.append(point);
                        break;
                }
            }
        }

        //leftovers
        if (key_builder.length() != 0 && value_builder.length() != 0)
            map.put(JSON.parse(key_builder.toString()), JSON.parse(value_builder.toString()));

        return map;
    }

    /**
     * Cast the given JSON text to {@link String string}.
     *
     * @param string json text to be casted
     * @return a string object from the given JSON text
     */
    public static String parse_string(String string) {
        return Strings.crop(string, 1, 1);
    }

    /**
     * Transform the given {@link Object object} to a JSON text.
     *
     * @param object  to transform
     * @param spacing base
     * @return a JSON text from the given object
     */
    public static String stringify(Object object, String spacing) {
        if (object == null)
            return "null";
        if (object instanceof Float)
            return JSON.stringify((Float) object, spacing);
        if (object instanceof Long)
            return JSON.stringify((Long) object, spacing);
        if (object instanceof String)
            return JSON.stringify((String) object, spacing);
        if (object instanceof Character)
            return JSON.stringify((Character) object, spacing);
        if (object instanceof Object[])
            return JSON.stringify((Object[]) object, spacing);
        if (object instanceof List)
            return JSON.stringify((List) object, spacing);
        if (object instanceof Map)
            return JSON.stringify((Map) object, spacing);
        if (object instanceof ArrayStructure)
            return JSON.stringify(((ArrayStructure) object).list(), spacing);
        if (object instanceof Structure)
            return JSON.stringify((Structure) object, spacing);

        return String.valueOf(object);
    }

    /**
     * Transform the given {@link Object object} to a JSON text.
     *
     * @param object to transform
     * @return a JSON text from the given object
     */
    public static String stringify(Object object) {
        return JSON.stringify(object, "");
    }

    /**
     * Transform the given {@link Object object array} to a JSON text.
     *
     * @param array   to transform
     * @param spacing base
     * @return a JSON text from the given array
     */
    private static String stringify(Object[] array, String spacing) {
        StringBuilder text = new StringBuilder();
        text.append("[");

        for (Object object : array)
            text.append(text.length() == 1 ? "" : ",")
                    .append("\n\t")
                    .append(spacing)
                    .append(JSON.stringify(object, spacing + "\t"));

        text.append("\n")
                .append(spacing)
                .append("]");

        return text.toString();
    }

    /**
     * Transform the given {@link List list} to a JSON text.
     *
     * @param list    to transform
     * @param spacing base
     * @return a JSON text from the given list
     */
    private static String stringify(List list, String spacing) {
        return JSON.stringify(list.toArray(), spacing);
    }

    /**
     * Transform the given {@link Map map} to a JSON text.
     *
     * @param map     to transform
     * @param spacing base
     * @return a JSON text from the given map
     */
    private static String stringify(Map<?, ?> map, String spacing) {
        StringBuilder text = new StringBuilder();
        text.append("{");

        map.forEach((key, value) ->
                text.append(text.length() == 1 ? "" : ",")
                        .append("\n")
                        .append(spacing)
                        .append("\t")
                        .append(JSON.stringify(key, spacing + "\t"))
                        .append(":\t")
                        .append(JSON.stringify(value, spacing + "\t")));

        text.append("\n")
                .append(spacing)
                .append("}");

        return text.toString();
    }

    /**
     * Transform the given {@link String string} to a JSON text.
     *
     * @param string  to transform
     * @param spacing base
     * @return a JSON text from the given string
     */
    private static String stringify(String string, String spacing) {
        return '"' + string.replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + '"';
    }

    /**
     * Transform the given {@link Float float} to a JSON text.
     *
     * @param f       float to transform
     * @param spacing base
     * @return a JSON text from the given float
     */
    private static String stringify(Float f, String spacing) {
        return f + "F";
    }

    /**
     * Transform the given {@link Long long} to a JSON text.
     *
     * @param l       long to transform
     * @param spacing base
     * @return a JSON text from the given float
     */
    private static String stringify(Long l, String spacing) {
        return l + "L";
    }

    /**
     * Transform the given {@link Structure structure} to a JSON text.
     *
     * @param structure to transform
     * @param spacing   base
     * @return a JSON text from the given structure
     */
    private static String stringify(Structure structure, String spacing) {
        return JSON.stringify(structure.map(), spacing);
    }

    /**
     * Transform the given {@link Object object} to a JSON text.
     *
     * @param character to transform
     * @param spacing   base
     * @return a JSON text from the given object
     */
    private static String stringify(Character character, String spacing) {
        return "\'" + character + "\'";
    }
}
