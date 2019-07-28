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
     * this is a util class and shall
     * not be instanced as an object.
     */
    private Arrays() {

    }

    /**
     * check if the give array contains all
     * of the given elements.
     *
     * @param array    to check
     * @param elements to check for
     * @param <E>      type of elements
     * @return if the given array contains all of the given elements
     */
    @SafeVarargs
    public static <E> boolean all(E[] array, E... elements) {
        Main:
        for (E element : elements) {
            for (E a : elements)
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
     * @param array    to check
     * @param elements to check for
     * @param <E>      type of elements
     * @return if the given array contains any of the given elements
     */
    @SafeVarargs
    public static <E> boolean any(E[] array, E... elements) {
        for (E a : array)
            for (E element : elements)
                if (a == element)
                    return true;
        return false;
    }

    /**
     * transform the given array list into
     * a java simple array.
     *
     * @param list to transform
     * @param type of array's components
     * @param <E>  type of elements
     * @return java simple array from given array list
     */
    public static <E> E[] asArray(List<E> list, Class<E> type) {
        return (E[]) list.toArray((E[]) Array.newInstance(type, list.size()));
    }

    /**
     * transform the given array list into
     * a java simple array.
     *
     * @param list to transform
     * @param <E>  type of elements
     * @return java simple array from given array list
     * @see #asArray(List, Class)  more lighter wieght :)
     */
    public static <E> E[] asArray(List<E> list) {
        return Arrays.generify(list.toArray());
    }

    /**
     * transform the given array into
     * an {@link ArrayList}.
     *
     * @param array to transform
     * @param <E>   type of the array
     * @return new array list including the given array
     */
    public static <E> List<E> asList(E[] array) {
        return new ArrayList<>(java.util.Arrays.asList(array));

    }

    /**
     * remove the last and the first elements
     * of the given array depending on the
     * given values.
     *
     * @param array to crop
     * @param start range to remove
     * @param end   range to remove
     * @param <T>   type of elements
     * @return cropped edge version of the given array
     */
    public static <T> T[] crop(T[] array, int start, int end) {
        T[] res = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - (start + end));

        if (array.length - end - start >= 0)
            System.arraycopy(array, start, res, 0, array.length - end - start);

        return res;
    }

    /**
     * change the type of the given array
     * to the best type depending on it's
     * elements type.
     *
     * @param array to generify
     * @param <TT>  type of generified array's elements.
     * @param <T>   type of old array's elements
     * @return the array with the perfect type for it
     */
    public static <T, TT extends T> TT[] generify(T[] array) {
        TT[] generified = (TT[]) Array.newInstance(Arrays.getComponentType(array), array.length);
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(array, 0, generified, 0, array.length);
        return generified;
    }

    /**
     * get the type of the given array
     * the best type depending on it's
     * elements type.
     *
     * @param array to get type of
     * @param <T>   type of elements
     * @return the perfect type for the given array
     */
    public static <T> Class<? extends T> getComponentType(T[] array) {
        return Reflect.getSuperOf(array);
    }

    /**
     * run the type of the given list.
     *
     * @param list to run type of
     * @param <E>  type of elements
     * @return the type of the given list
     */
    public static <E> Class<? extends E> getComponentType(List<E> list) {
        return (Class<? extends E>) Reflect.getSuperOf(list.toArray());
    }

    /**
     * run the index of the given element
     * inside the given array.
     *
     * @param array   to run index from
     * @param element to run the index of
     * @param <E>     type of elements
     * @return the index of the given element inside the given array
     */
    @SafeVarargs
    public static <E> int indexOf(E element, E... array) {
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
     * @param array   to check
     * @param element to apply conditions with
     * @param any     condition
     * @param filters (or conditions) to apply
     * @param <E>     element type
     * @return if the given list matches the conditions with the given object
     */
    @SafeVarargs
    public static <E> boolean matches(E[] array, E element, boolean any, BiFunction<E, E, Boolean>... filters) {
        return Arrays.matches(Arrays.asList(array), element, any, filters);
    }

    /**
     * check if any/all of the given list's elements
     * matches specific conditions with the given object.
     *
     * @param list    to check
     * @param element to apply conditions with
     * @param any     condition
     * @param filters (or conditions) to apply
     * @param <E>     type of elements
     * @return if the given list matches the conditions with the given object
     */
    @SafeVarargs
    public static <E> boolean matches(List<E> list, E element, boolean any, BiFunction<E, E, Boolean>... filters) {
        boolean w = true;

        for (E element0 : list)
            for (BiFunction<E, E, Boolean> filter : filters)
                try {
                    if (filter.apply(element0, element)) {
                        if (any)
                            return true;
                    } else if (!any) {
                        return false;
                    }
                } catch (Exception ignored) {
                }

        return !any;
    }

    /**
     * remove passed objects from passed array.
     *
     * @param array    to remove from
     * @param elements to remove
     * @param <T>      type of elements
     * @return passed array excluded from passed elements
     */
    @SafeVarargs
    public static <T> T[] remove(T[] array, T... elements) {
        T[] res = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - elements.length);

        int i = 0;

        for (T element : array)
            if (!Arrays.any(elements, element)) {
                res[i] = element;
                i++;
            }

        return res;
    }

}
