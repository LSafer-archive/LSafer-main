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

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * A {@link Map maps} plugin interface. An interface that adds useful methods to the implement map.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (28-Sep-19)
 * @since 18-Sep-19
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface JetMap<K, V> extends Map<K, V>, Caster.User {
	/**
	 * Apply the passed function. ONLY if the stored value Not equals to null,
	 * and is instance of the given klass. Then return the value.
	 *
	 * @param klass    to make sure the mapped value is instance of the needed class
	 * @param key      to get it's mapped value
	 * @param function to be applied
	 * @param <T>      type of value
	 * @return the mapped value to the given key
	 */
	default <T> T doIfPresent(Class<T> klass, Object key, Consumer<T> function) {
		T value = this.get(klass, key);

		if (value != null)
			function.accept(value);

		return value;
	}

	/**
	 * Get the value mapped to the given key. Or returns
	 * the given default value case the key didn't
	 * exist or it's mapped to null.
	 *
	 * <ul>
	 * <li>
	 * note: this will cast the mapped value. And return it. But it'll not map the casted instance.
	 * </li>
	 * </ul>
	 *
	 * @param klass to make sure the mapped value is instance of the needed class
	 * @param key   to get it's mapped value
	 * @param <T>   type of value
	 * @return the mapped value to the given key
	 */
	default <T> T get(Class<? extends T> klass, Object key) {
		return this.caster().cast(klass, this.get(key));
	}

	/**
	 * Removes all of the entries of this that satisfy the given predicate.
	 * Errors or runtime exceptions thrown during iteration or by the predicate are relayed to the caller.
	 *
	 * @param filter a predicate which returns true for entries to be removed
	 * @return true if any entries were removed
	 */
	default boolean removeIf(BiPredicate<K, V> filter) {
		Set<K> keySet = new HashSet<>();

		this.forEach(((k, v) -> {
			if (filter.test(k, v))
				keySet.add(k);
		}));

		if (keySet.isEmpty()) {
			return false;
		} else {
			keySet.forEach(this::remove);
			return true;
		}
	}

	/**
	 * Remove a value {@link Objects#equals(Object, Object) equals} to the given value.
	 * This method removes the first value equals to the given value. Then return the
	 * key it associated to. Or null if ether the key is null. Or there is no such
	 * value equals to the given value in this map.
	 *
	 * @param value to be removed
	 * @return the key the value is associated to
	 */
	default K removeValue(Object value) {
		for (Map.Entry<K, V> entry : this.entrySet())
			if (Objects.equals(entry.getValue(), value)) {
				K key = entry.getKey();
				this.remove(key);
				return key;
			}
		return null;
	}
}
