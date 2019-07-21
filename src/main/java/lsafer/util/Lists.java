package lsafer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import lsafer.lang.Reflect;

/**
 * useful utils for lists.
 *
 * @author LSaferSE
 * @version 1 alpha (19-Jul-19)
 * @since 19-Jul-19
 */
@SuppressWarnings({"WeakerAccess"})
public class Lists {

    /**
     * run the type of the given list.
     *
     * @param list      to run type of
     * @param <ELEMENT> type of elements
     * @return the type of the given list
     */
    public static <ELEMENT> Class<? extends ELEMENT> getComponentType(List<ELEMENT> list) {
        return (Class<? extends ELEMENT>) Reflect.getSuperOf(list.toArray());

    }

    /**
     * check if any/all of the given list's elements
     * matches specific conditions with the given object.
     *
     * @param list      to check
     * @param object    to apply conditions with
     * @param any       condition
     * @param filters   (or conditions) to apply
     * @param <ELEMENT> type of elements
     * @return if the given list matches the conditions with the given object
     */
    @SafeVarargs
    public static <ELEMENT> boolean matches(List<ELEMENT> list, ELEMENT object, boolean any, BiFunction<ELEMENT, ELEMENT, Boolean>... filters) {
        boolean w = true;

        for (ELEMENT element : list)
            for (BiFunction<ELEMENT, ELEMENT, Boolean> filter : filters)
                try {
                    if (filter.apply(element, object)) {
                        if (any)
                            return true;
                    } else if (!any) {
                        return false;
                    }
                } catch (Exception e) {
                    //
                }

        return !any;
    }

    /**
     * transform the given array into
     * an {@link ArrayList}.
     *
     * @param array     to transform
     * @param <ELEMENT> type of the array
     * @return new array list including the given array
     */
    public static <ELEMENT> List<ELEMENT> valueOf(ELEMENT[] array) {
        return new ArrayList<>(java.util.Arrays.asList(array));

    }

}
