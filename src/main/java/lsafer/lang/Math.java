package lsafer.lang;

/**
 * useful methods for numbers.
 *
 * @author LSafer
 * @version 2
 * @since 11 Jun 2019
 */
final public class Math {

    /**
     * this is a util class and shall
     * not be instanced as an object.
     */
    private Math() {

    }

    /**
     * simple ratio formula :).
     * <p>
     * f(value, all) = value / all
     *
     * @param value of ratio
     * @param all   parts
     * @return : the ratio between value and all values
     */
    public static int ratioOf(Number value, Number all) {
        return (int) ((((double) value) / ((double) all)) * 100.0);
    }

}
