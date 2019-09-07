package lsafer.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Useful methods for Arrays.
 *
 * @author LSafer
 * @version 4 release (06-Sep-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess"})
final public class Arrays {
    /**
     * This is a util class. And shall not be instanced as an object.
     */
    private Arrays() {

    }

    /**
     * Check whether the given array contains all the given elements or not.
     *
     * @param array    to check
     * @param elements to check for
     * @param <E>      type of elements
     * @return whether the given array contains all of the given elements or not
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
     * Check whether the given array contains any of the given elements or not.
     *
     * @param array    to check
     * @param elements to check for
     * @param <E>      type of elements
     * @return whether the given array contains any of the given elements or not
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
     * Append the given elements to the end of the given array.
     *
     * @param array    to be appended
     * @param elements to append
     * @param <T>      type of elements
     * @return a brand-new array with the given elements appended
     */
    @SafeVarargs
    public static <T> T[] append(T[] array, T... elements) {
        T[] res = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + elements.length);
        System.arraycopy(array, array.length + 1, elements, 0, array.length + elements.length);
        return res;
    }

    /**
     * Transform the given {@link List} into an {@link Object array}.
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
     * Transform the given {@link Object array} into a {@link List}.
     *
     * @param array to transform
     * @param <E>   type of the array
     * @return a new list including from given array
     */
    public static <E> List<E> asList(E[] array) {
        return new ArrayList<>(java.util.Arrays.asList(array));

    }

    /**
     * Remove the last and the first elements of the given {@link Object array}. Depending on the given values.
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
     * Fill the given {@link List}. Using the given supplier. Until it reaches the given size.
     *
     * @param list     to be filled
     * @param size     limit to fill until
     * @param supplier to use for filling
     * @param <E>      type of the list's elements
     */
    public static <E> void fill(List<E> list, int size, Supplier<E> supplier) {
        for (int i = list.size(); i < size; i++)
            list.add(supplier.get());
    }

    /**
     * Fill the given {@link Object array}. Using the given supplier. Until it reaches the given size.
     *
     * @param array    to be filled
     * @param size     limit to fill until
     * @param supplier to use for filling
     * @param <T>      type of the array's elements
     * @return the filled array (a new instance/array)
     */
    public static <T> T[] fill(T[] array, int size, Supplier<T> supplier) {
        T[] filled = (T[]) Array.newInstance(array.getClass().getComponentType(), size);
        System.arraycopy(array, 0, filled, 0, array.length);

        for (int i = array.length; i < size; i++)
            filled[i] = supplier.get();

        return filled;
    }

    /**
     * Get the index of the given element inside the given {@link Object array}.
     *
     * @param array   to get index from
     * @param element to get the index of
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
     * Check whether any/all of the given list's elements matches specific conditions with the given object or not.
     *
     * @param array   to check
     * @param element to apply conditions with
     * @param any     condition
     * @param filters (or conditions) to apply
     * @param <E>     element type
     * @return whether the given list matches the conditions with the given object or not
     */
    @SafeVarargs
    public static <E> boolean matches(E[] array, E element, boolean any, BiFunction<E, E, Boolean>... filters) {
        return Arrays.matches(Arrays.asList(array), element, any, filters);
    }

    /**
     * Check whether any/all of the given list's elements matches specific conditions with the given object or not.
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
     * Remove passed objects from passed array.
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
