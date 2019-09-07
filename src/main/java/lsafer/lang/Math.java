package lsafer.lang;

/**
 * Useful methods for numbers.
 *
 * @author LSafer
 * @version 3 release (06-Sep-2019)
 * @since 11 Jun 2019
 */
final public class Math {
    /**
     * This is a util class. And shall not be instanced as an object.
     */
    private Math() {
    }

    /**
     * Get the ratio of the given 'value' number compared to the 'all' number.
     * <br><br><b>example:</b>
     * <pre>
     * f(value, all) = value / all
     * </pre>
     *
     * @param value of ratio
     * @param all   parts
     * @return the ratio between value and all values
     */
    public static int ratioOf(Number value, Number all) {
        float fv = Float.valueOf(value.toString());
        float fa = Float.valueOf(all.toString());

        return (int) ((fv / fa) * 100.0f);
    }
}
