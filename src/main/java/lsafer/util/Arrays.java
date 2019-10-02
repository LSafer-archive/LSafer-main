/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package lsafer.util;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Useful methods for Arrays.
 *
 * @author LSafer
 * @version 5 release (28-Sep-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
		elements:
		for (E element : elements)
			for (E element1 : array) {
				if (Objects.equals(element, element1))
					continue elements;
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
		for (E element1 : array)
			for (E element : elements)
				if (Objects.equals(element, element1))
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
		T[] res = (T[]) Array.newInstance(array.getClass().getComponentType(),
				array.length + elements.length);
		System.arraycopy(array, array.length + 1, elements, 0, array.length + elements.length);
		return res;
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position,
	 * to the specified position of the destination array. A subsequence of array components
	 * are copied from the source array referenced by src to the destination array referenced
	 * by dest. The number of components copied is equal to the length argument. The components
	 * at positions srcPos through srcPos+length-1 in the source array are copied into positions
	 * destPos through destPos+length-1, respectively, of the destination array.
	 *
	 * @param src     the source array.
	 * @param srcPos  starting position in the source array.
	 * @param dest    the destination array.
	 * @param destPos starting position in the destination data.
	 * @param length  the number of array elements to be copied.
	 */
	public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
		for (int i = 0, is = srcPos, id = destPos; i < length; i++)
			Array.set(dest, id++, Array.get(src, is++));
	}

	/**
	 * Check whether the given array contains any of the given elements or not.
	 *
	 * @param array   to check
	 * @param element to check for
	 * @param <E>     type of elements
	 * @return whether the given array contains any of the given elements or not
	 */
	@SafeVarargs
	public static <E> boolean contains(E element, E... array) {
		for (E element1 : array)
			if (Objects.equals(element, element1))
				return true;
		return false;
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
		T[] res = (T[]) Array.newInstance(array.getClass().getComponentType(),
				array.length - (start + end));

		if (array.length - end - start >= 0)
			System.arraycopy(array, start, res, 0, array.length - end - start);

		return res;
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
	 * Fix the given array to have all {@link Object} elements deep inside it.
	 *
	 * @param array to be fixed
	 * @param <T>   type of the elements inside the fixed array
	 * @return fixed array from the given array, or the given array if it's already fixed
	 */
	public static <T> T[] objective(Object array) {
		if (!array.getClass().isArray())
			throw new RuntimeException(array + " is not an array");

		Class<?> type = array.getClass().getComponentType();

		for (Class<?> c = type; c != null; c = c.getComponentType())
			if (!c.isArray() && Object.class.isAssignableFrom(c))
				return (T[]) array;

		int length = Array.getLength(array);

		T[] array1 = (T[]) Array.newInstance(Classes.objective(type), length);

		Arrays.arraycopy(array, 0, array1, 0, length);

		return array1;
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
		T[] res = (T[]) Array.newInstance(array.getClass().getComponentType(),
				array.length - elements.length);

		int i = 0;

		for (T element : array)
			if (!Arrays.any(elements, element)) {
				res[i] = element;
				i++;
			}

		return res;
	}
}
