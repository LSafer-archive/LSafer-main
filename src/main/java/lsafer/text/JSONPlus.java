package lsafer.text;

import lsafer.annotation.Underdevelopment;

/**
 * A parser/stringifier for JSONPlus. JSONPlus is just like JSON but before each value. There is an annotation for the type of the value.
 *
 * <br><br><b>example:</b>
 * <pre>
 *     (java.util.Map){
 *         (java.lang.String) "name": (java.lang.String) "sulaiman",
 *         (java.lang.String) "children": (java.lang.List) [
 *              (java.lang.String) "alex",
 *              (java.lang.String) "john",
 *              (java.lang.String) "frank",
 *         ],
 *         (int) "age": 18,
 *     }
 * </pre>
 *
 * @author LSaferSE
 * @version 1 alpha (07-Sep-19)
 * @since 07-Sep-19
 */
@Underdevelopment(value = "not working", state = "scratch")
@SuppressWarnings("ALL")
final public class JSONPlus {
    /**
     *
     */
    public static String stringify(Object object) {
        StringBuilder builder = new StringBuilder();

        builder.append("(")
                .append(object.getClass().getName())
                .append(")");

        return builder.toString();
    }
}
