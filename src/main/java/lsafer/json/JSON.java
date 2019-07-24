package lsafer.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lsafer.util.Arrays;
import lsafer.util.Strings;
import lsafer.util.Structure;

/**
 * useful utils for using JSON.
 * <p>
 * Including :
 * <ul>
 * <li>transform {@link Object objects} into a JSON style text</li>
 * <li>cast object from JSON text into a Java {@link Object object}</li>
 * </ul>
 * <p>
 * TODO Support inString symbols ignoring
 *
 * @author LSaferSE
 * @version 2
 * @since 09-Jul-19
 */
@SuppressWarnings({"WeakerAccess"})
final public class JSON {

    /**
     * this is a util class and shall
     * not be instanced as an object.
     */
    private JSON(){

    }

    /**
     * check if the given JSON text is an {@link Object[] object array} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is an array or not
     */
    public static boolean is_array(String string) {
        return string.length() > 1 && (string.charAt(0) == '[' || string.charAt(1) == '[') &&
                (string.charAt(string.length() - 1) == ']' || string.charAt(string.length() - 2) == ']');
    }

    /**
     * check if the given JSON text is a {@link Boolean[] boolean} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a boolean or not
     */
    public static boolean is_boolean(String string) {
        return string.equals("true") || string.equals("false");
    }

    /**
     * check if the given JSON text is a {@link Character character} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a character or not
     */
    public static boolean is_char(String string) {
        return string.length() == 3 && string.charAt(0) == '\'' && string.charAt(2) == '\'';
    }

    /**
     * check if the given JSON text is a {@link Double double} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a double or not
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
     * check if the given JSON text is a {@link Float float} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a float or not
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
     * check if the given JSON text is an {@link Integer integer} or not.
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
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check if the given JSON text is a {@link Long long} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is an long or not
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
     * check if the given JSON text is a {@link Map map} or not.
     *
     * @param string JSON text to be checked
     * @return whether the passed JSON text is a map or not
     */
    public static boolean is_map(String string) {
        return string.length() > 1 && (string.charAt(0) == '{' || string.charAt(1) == '{') &&
                (string.charAt(string.length() - 1) == '}' || string.charAt(string.length() - 2) == '}');
    }

    /**
     * check if the given JSON text is a {@link String string} or not.
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
     * cast the given JSON text to {@link Object object}.
     *
     * @param string json text to be casted
     * @return an object that matches the given JSON text
     */
    public static Object parse(String string) {
        if (string.equals("null") || string.equals(""))
            return null;
        if (is_string(string))
            return parse_string(string);
        if (is_char(string))
            return parse_char(string);
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
        if (is_map(string))
            return parse_map(string);

        return string;
    }

    /**
     * cast the given JSON text to {@link Object[] object array}.
     *
     * @param string json text to be casted
     * @return an object array from the given JSON text
     */
    public static Object[] parse_array(String string) {
        string = Strings.replace(string, "", "\r", "\t", "\n", String.valueOf((char) 65533));//remove
        string = Strings.crop(string, 1, 1);

        ArrayList<Object> List = new ArrayList<>(); //result

        int dismiss = 0; //dismiss tracker
//        boolean stringing = false;
//        boolean skip = false;
        StringBuilder value = new StringBuilder(); //values temporary container

        for (String point : string.split("")) {
            if (point.equals(",") && dismiss == 0) {
                List.add(parse(value.toString()));
                value = new StringBuilder();

            } else {
                if (Strings.any(point, "{", "["))
                    dismiss++;
                else if (Strings.any(point, "}", "]"))
                    dismiss--;

                value.append(point); //writing value
            }
        }

        if (!value.toString().equals(""))
            List.add(parse(value.toString())); //leftovers

        return Arrays.asArray(List);
    }

    /**
     * cast the given JSON text to {@link Character character}.
     *
     * @param string json text to be casted
     * @return an object array from the given JSON text
     */
    public static char parse_char(String string) {
        return string.charAt(1);
    }

    /**
     * cast the given JSON text to {@link Map map}.
     *
     * @param string json text to be casted
     * @return a map object from the given JSON text
     */
    public static Map<Object, Object> parse_map(String string) {
        HashMap<Object, Object> map = new HashMap<>(); //result

        string = Strings.replace(string, "", "\r", "\t", "\n", String.valueOf((char) 65533));//remove
        string = Strings.crop(string, 1, 1);

        int dismiss = 0; //dismiss tracker
        boolean equating = false; //equating tracker
//        boolean skip = false;
//        boolean stringing = false;
        StringBuilder value = new StringBuilder(); //temporary val holder
        StringBuilder key = new StringBuilder(); //temporary key holder

        for (char point : string.toCharArray()) {
            if (point == ',' && dismiss == 0) {
                map.put(parse(key.toString()), parse(value.toString()));
                key = new StringBuilder();
                value = new StringBuilder();
                equating = false;

            } else if (point == ':' && !equating) {
                equating = true;

            } else {
                if (point == '{' || point == '[')
                    dismiss++;
                else if (point == '}' || point == ']')
                    dismiss--;

                (equating ? value : key).append(point);
            }
        }

        if (!key.toString().equals(""))
            map.put(parse(key.toString()), parse(value.toString())); //leftovers

        return map;
    }

    /**
     * cast the given JSON text to {@link String string}.
     *
     * @param string json text to be casted
     * @return a string object from the given JSON text
     */
    public static String parse_string(String string) {
        return Strings.crop(string, 1, 1);
    }

    /**
     * transform the given {@link Object object array} to a JSON text.
     *
     * @param array   to transform
     * @param spacing base
     * @return a JSON text from the given array
     */
    public static String stringify(Object[] array, String spacing) {
        StringBuilder text = new StringBuilder();
        text.append("[");

        for (Object object : array)
            text.append(text.length() == 1 ? "" : ",")
                    .append("\n")
                    .append(spacing)
                    .append(stringify(object, spacing + "\t"));

        text.append("\n")
                .append(spacing)
                .append("]");

        return text.toString();
    }

    /**
     * transform the given {@link List list} to a JSON text.
     *
     * @param list    to transform
     * @param spacing base
     * @return a JSON text from the given list
     */
    public static String stringify(List list, String spacing) {
        return stringify(list.toArray(), spacing);
    }

    /**
     * transform the given {@link Map map} to a JSON text.
     *
     * @param map     to transform
     * @param spacing base
     * @return a JSON text from the given map
     */
    public static String stringify(Map<?, ?> map, String spacing) {
        StringBuilder text = new StringBuilder();
        text.append("{");

        map.forEach((key, value) ->
                text.append(text.length() == 1 ? "" : ",")
                        .append("\n")
                        .append(spacing)
                        .append("\t")
                        .append(stringify(key, spacing + "\t"))
                        .append(":\t")
                        .append(stringify(value, spacing + "\t")));

        text.append("\n")
                .append(spacing)
                .append("}");

        return text.toString();
    }

    /**
     * transform the given {@link String string} to a JSON text.
     *
     * @param string  to transform
     * @param spacing base
     * @return a JSON text from the given string
     */
    public static String stringify(String string, String spacing) {
        return "\"" + string + "\"";
    }

    /**
     * transform the given {@link Float float} to a JSON text.
     *
     * @param f       float to transform
     * @param spacing base
     * @return a JSON text from the given float
     */
    public static String stringify(Float f, String spacing) {
        return f + "f";
    }

    /**
     * transform the given {@link Structure structure} to a JSON text.
     *
     * @param structure to transform
     * @param spacing   base
     * @return a JSON text from the given structure
     */
    public static String stringify(Structure structure, String spacing) {
        return stringify(structure.map(), spacing);
    }

    /**
     * transform the given {@link Object object} to a JSON text.
     *
     * @param object  to transform
     * @param spacing base
     * @return a JSON text from the given object
     */
    public static String stringify(Object object, String spacing) {
        if (object instanceof Object[])
            return stringify((Object[]) object, spacing);
        if (object instanceof List)
            return stringify((List) object, spacing);
        if (object instanceof Map)
            return stringify((Map) object, spacing);
        if (object instanceof String)
            return stringify((String) object, spacing);
        if (object instanceof Float)
            return stringify((Float) object, spacing);
        if (object instanceof Structure)
            return stringify((Structure) object, spacing);
        if (object instanceof Character)
            return stringify((Character) object, spacing);

        return String.valueOf(object);
    }

    /**
     * transform the given {@link Object object} to a JSON text.
     *
     * @param character to transform
     * @param spacing   base
     * @return a JSON text from the given object
     */
    public static String stringify(Character character, String spacing) {
        return "\'" + character + "\'";
    }

    /**
     * transform the given {@link Object object} to a JSON text.
     *
     * @param object to transform
     * @return a JSON text from the given object
     */
    public static String stringify(Object object) {
        return stringify(object, "");
    }

}
