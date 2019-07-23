package lsafer.microsoft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lsafer.util.Arrays;
import lsafer.util.Strings;
import lsafer.util.Structure;

/**
 * useful utils for using windows INI files.
 * Including
 * <ul>
 * <li>transform {@link Object objects} into an INI style text</li>
 * <li>cast object from INI text into a Java {@link Object object}</li>
 * </ul>
 * <p>
 *
 * @author LSaferSE
 * @version 1 alpha (21-Jul-19)
 * @since 21-Jul-19
 */
@SuppressWarnings({"WeakerAccess"})
final public class INI {

    /**
     * check if the given INI text is an {@link Object[] object array} or not.
     *
     * @param string INI text to be checked
     * @return whether the passed INI text is an array or not
     */
    public static boolean is_array(String string) {
        return string.contains(",");
    }

    /**
     * check if the given INI text is a {@link Boolean[] boolean} or not.
     *
     * @param string INI text to be checked
     * @return whether the passed INI text is a boolean or not
     */
    public static boolean is_boolean(String string) {
        return string.equals("true") || string.equals("false");
    }

    /**
     * check if the given INI text is a {@link Double double} or not.
     *
     * @param string INI text to be checked
     * @return whether the passed INI text is a double or not
     */
    public static boolean is_double(String string) {
        try {
            Double.valueOf(string);
            return !string.endsWith("f") &&
                    string.contains(".");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if the given INI text is a {@link Float float} or not.
     *
     * @param string INI text to be checked
     * @return whether the passed INI text is a float or not
     */
    public static boolean is_float(String string) {
        try {
            Float.valueOf(string);
            return string.endsWith("f");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if the given INI text is an {@link Integer integer} or not.
     *
     * @param string INI text to be checked
     * @return whether the passed INI text is an integer or not
     */
    public static boolean is_integer(String string) {
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
     * check if the given INI text is an {@link Long long} or not.
     *
     * @param string INI text to be checked
     * @return whether the passed INI text is an long or not
     */
    public static boolean is_long(String string) {
        try {
            Long.valueOf(string);
            return string.endsWith("L") &&
                    !string.contains(".");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * parse the given INI text into a java {@link Map}.
     * <p>
     * note : if you want to parse just an element use {@link #parse_object(String)}
     *
     * @param string source
     * @return a java map from the given string
     */
    public static Map<String, Object> parse(String string) {
        string = Strings.replace(string, "", "\r", "\t", "\u0000", String.valueOf((char) 65533));
        Map<String, Object> main = new HashMap<>();
        Map<String, Object> section = null;

        for (String line : string.split("\n"))
            if (line.length() > 2)
                if (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']') {
                    main.put(Strings.crop(line, 1, 1), section = new HashMap<>());

                } else if (line.charAt(0) != ';') {
                    String[] node = line.split("=");
                    if (node.length == 2)
                        (section == null ? main : section).put(node[0], parse_object(node[1]));
                }

        return main;
    }

    /**
     * cast the given INI text to {@link Object[] object}.
     *
     * @param string INI text to be casted
     * @return an object that matches the given INI text
     */
    public static Object[] parse_array(String string) {
        List<Object> list = new ArrayList<>();

        for (String element : string.split(","))
            list.add(parse_object(element));

        return Arrays.asArray(list);
    }

    /**
     * cast the given INI text to {@link Object object}.
     *
     * @param string INI text to be casted
     * @return an object that matches the given INI text
     */
    public static Object parse_object(String string) {
        if (string.equals("null"))
            return null;
        if (is_boolean(string))
            return Boolean.valueOf(string);
        if (is_float(string))
            return Float.valueOf(string);
        if (is_double(string))
            return Double.valueOf(string);
        if (is_long(string))
            return Long.valueOf(string);
        if (is_integer(string))
            return Integer.valueOf(string);
        if (is_array(string))
            return parse_array(string);

        return string;
    }

    /**
     * transform the given {@link Map map} to an INI text.
     *
     * @param map to transform
     * @return a INI text from the given map
     */
    public static String stringify(Map<?, ?> map) {
        StringBuilder string = new StringBuilder();
        StringBuilder last = new StringBuilder();

        int[] index = {0};
        map.forEach((key, value) -> {
            if (value instanceof Map || value instanceof Structure) {
                last.append(index[0] == 0 ? "" : "\n\n").append("[").append(key).append("]").append("\n").append(stringify(value));

            } else {
                string.append(index[0] == 0 ? "" : "\n").append(key).append("=").append(stringify(value));
            }
            index[0]++;
        });

        return string.append(last).toString();
    }

    /**
     * transform the given {@link Object object} to an INI text.
     *
     * @param object to transform
     * @return a INI text from the given map
     */
    public static String stringify(Object object) {
        if (object instanceof Object[])
            return stringify((Object[]) object);
        if (object instanceof List)
            return stringify((List) object);
        if (object instanceof Map)
            return stringify((Map) object);
        if (object instanceof Float)
            return stringify((Float) object);
        if (object instanceof Structure)
            return stringify((Structure) object);

        return String.valueOf(object);
    }

    /**
     * transform the given {@link Object[] array} to an INI text.
     *
     * @param array to transform
     * @return a INI text from the given map
     */
    public static String stringify(Object[] array) {
        StringBuilder string = new StringBuilder();

        for (Object object : array)
            string.append(string.length() == 0 ? "" : ",").append(stringify(object));

        return string.toString();
    }

    /**
     * transform the given {@link List list} to an INI text.
     *
     * @param list to transform
     * @return a INI text from the given map
     */
    public static String stringify(List list) {
        return stringify(list.toArray());
    }

    /**
     * transform the given {@link Float float} to an INI text.
     *
     * @param f to transform
     * @return a INI text from the given map
     */
    public static String stringify(Float f) {
        return f + "f";
    }

    /**
     * transform the given {@link Structure structure} to an INI text.
     *
     * @param structure to transform
     * @return a INI text from the given map
     */
    public static String stringify(Structure structure) {
        return stringify(structure.map());
    }

}
