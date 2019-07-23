package lsafer.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import lsafer.lang.Reflect;

/**
 * useful methods for Arrays.
 *
 * @author LSafer
 * @version 3
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess"})
final public class Arrays {

    /**
     * check if the give array contains all
     * of the given elements.
     *
     * @param array     to check
     * @param elements  to check for
     * @param <ELEMENT> type of elements
     * @return if the given array contains all of the given elements
     */
    @SafeVarargs
    public static <ELEMENT> boolean all(ELEMENT[] array, ELEMENT... elements) {
        Main:
        for (ELEMENT element : elements) {
            for (ELEMENT a : elements)
                if (element == a)
                    continue Main;
            return false;
        }
        return true;
    }

    /**
     * check if the given array contains any
     * of the given elements.
     *
     * @param array     to check
     * @param elements  to check for
     * @param <ELEMENT> type of elements
     * @return if the given array contains any of the given elements
     */
    @SafeVarargs
    public static <ELEMENT> boolean any(ELEMENT[] array, ELEMENT... elements) {
        for (ELEMENT a : array)
            for (ELEMENT element : elements)
                if (a == element)
                    return true;
        return false;
    }

    /**
     * transform the given array list into
     * a java simple array.
     *
     * @param list      to transform
     * @param type      of array's components
     * @param <ELEMENT> type of elements
     * @return java simple array from given array list
     */
    public static <ELEMENT> ELEMENT[] asArray(List<ELEMENT> list, Class<ELEMENT> type) {
        return (ELEMENT[]) list.toArray((ELEMENT[]) Array.newInstance(type, list.size()));
    }

    /**
     * transform the given array list into
     * a java simple array.
     *
     * @param list      to transform
     * @param <ELEMENT> type of elements
     * @return java simple array from given array list
     * @see #asArray(List, Class)  more lighter wieght :)
     */
    public static <ELEMENT> ELEMENT[] asArray(List<ELEMENT> list) {
        return generify(list.toArray());
    }

    /**
     * transform the given array into
     * an {@link ArrayList}.
     *
     * @param array     to transform
     * @param <ELEMENT> type of the array
     * @return new array list including the given array
     */
    public static <ELEMENT> List<ELEMENT> asList(ELEMENT[] array) {
        return new ArrayList<>(java.util.Arrays.asList(array));

    }

    /**
     * remove the last and the first elements
     * of the given array depending on the
     * given values.
     *
     * @param array     to crop
     * @param start     range to remove
     * @param end       range to remove
     * @param <ELEMENT> type of elements
     * @return cropped edge version of the given array
     */
    public static <ELEMENT> ELEMENT[] crop(ELEMENT[] array, int start, int end) {
        ELEMENT[] res = (ELEMENT[]) Array.newInstance(array.getClass().getComponentType(), array.length - (start + end));

        if (array.length - end - start >= 0)
            System.arraycopy(array, start, res, 0, array.length - end - start);

        return res;
    }

    /**
     * change the type of the given array
     * to the best type depending on it's
     * elements type.
     *
     * @param array         to generify
     * @param <NEW_ELEMENT> type of generified array's elements.
     * @param <OLD_ELEMENT> type of old array's elements
     * @return the array with the perfect type for it
     */
    public static <OLD_ELEMENT, NEW_ELEMENT extends OLD_ELEMENT> NEW_ELEMENT[] generify(OLD_ELEMENT[] array) {
        NEW_ELEMENT[] generified = (NEW_ELEMENT[]) Array.newInstance(getComponentType(array), array.length);
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(array, 0, generified, 0, array.length);
        return generified;
    }

    /**
     * get the type of the given array
     * the best type depending on it's
     * elements type.
     *
     * @param array     to get type of
     * @param <ELEMENT> type of elements
     * @return the perfect type for the given array
     */
    public static <ELEMENT> Class<? extends ELEMENT> getComponentType(ELEMENT[] array) {
        return Reflect.getSuperOf(array);
    }

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
     * run the index of the given element
     * inside the given array.
     *
     * @param array     to run index from
     * @param element   to run the index of
     * @param <ELEMENT> type of elements
     * @return the index of the given element inside the given array
     */
    @SafeVarargs
    public static <ELEMENT> int indexOf(ELEMENT element, ELEMENT... array) {
        for (int i = 0; i < array.length; i++)
            if (array[i] != null)
                if (array[i].equals(element))
                    return i;
        return -1;
    }

    /**
     * check if any/all of the given list's elements
     * matches specific conditions with the given object.
     *
     * @param array     to check
     * @param object    to apply conditions with
     * @param any       condition
     * @param filters   (or conditions) to apply
     * @param <ELEMENT> element type
     * @return if the given list matches the conditions with the given object
     */
    @SafeVarargs
    public static <ELEMENT> boolean matches(ELEMENT[] array, ELEMENT object, boolean any, BiFunction<ELEMENT, ELEMENT, Boolean>... filters) {
        return matches(asList(array), object, any, filters);
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
     * remove passed objects from passed array.
     *
     * @param array     to remove from
     * @param elements  to remove
     * @param <ELEMENT> type of elements
     * @return passed array excluded from passed elements
     */
    @SafeVarargs
    public static <ELEMENT> ELEMENT[] remove(ELEMENT[] array, ELEMENT... elements) {
        ELEMENT[] res = (ELEMENT[]) Array.newInstance(array.getClass().getComponentType(), array.length - elements.length);

        int i = 0;

        for (ELEMENT element : array)
            if (!any(elements, element)) {
                res[i] = element;
                i++;
            }

        return res;
    }

}
