package lsafer.util;

import java.util.Collection;
import java.util.function.Function;

/**
 * Utils for {@link Collection collections}.
 *
 * @author LSaferSE
 * @version 2 release (28-Sep-19)
 * @since 25-Sep-19
 */
@SuppressWarnings("WeakerAccess")
final public class Collections {
	/**
	 * Util classes shouldn't be instanced.
	 */
	private Collections() {
	}

	/**
	 * Fill the given {@link Collection}. Using the given supplier. Until it reaches the given size.
	 *
	 * @param collection to be filled
	 * @param size       limit to fill until
	 * @param supplier   to use for filling
	 * @param <E>        type of the list's elements
	 */
	public static <E> void fill(Collection<E> collection, int size, Function<Integer, E> supplier) {
		for (int i = collection.size(); i < size; i++)
			collection.add(supplier.apply(i));
	}
}
