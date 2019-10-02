/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header)
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package lsafer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Map maps} plugin interface. An Interface that adds all {@link List lists} base methods
 * such as {@link List#add(Object)} or {@link List#add(int, Object)}, etc...
 * Using the Integer keyed entries as the bound for specific index.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 4 release (28-Sep-19)
 * @since 15-Sep-19
 */
@SuppressWarnings("unused")
public interface HybridMap<K, V> extends Map<K, V>, Caster.User {
	/**
	 * Depending on current indexing position on this structure.
	 * Add an element at the index after the last index in this.
	 * <br><br><b>example:</b>
	 * <pre>
	 *     toString -> {-1:"-one", 0:"zero", 1:"one"}
	 *     add <- "two"
	 *     toString -> {-1:"-one", 0:"zero", 1:"one", 2:"two"}
	 * </pre>
	 *
	 * @param element to be added
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 */
	default V add(V element) {
		return this.put((K) (Object) (this.maxIndex() + 1), element);
	}

	/**
	 * Inserts the specified element at the specified position in this structure indexing system.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 *
	 * @param index   index at which the specified element is to be inserted
	 * @param element element to be inserted
	 */
	default void add(int index, V element) {
		this.shiftIndexes(index, 1);
		this.put((K) (Object) index, element);
	}

	/**
	 * Appends all of the elements in the specified collection to the end of this structure indexing system,
	 * in the order that they are returned by the specified collection's Iterator.
	 * The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
	 *
	 * @param collection containing elements to be added to this structure
	 * @return true if this structure changed as a result of the call
	 */
	default boolean addAll(Collection<V> collection) {
		int i = this.maxIndex();

		for (V element : collection)
			this.put((K) (Object) (++i), element);

		return collection.size() != 0;
	}

	/**
	 * Inserts all of the elements in the specified collection into this list, starting at the specified position.
	 * Shifts the element currently at that position (if any) and any subsequent elements to the right (increases their indices).
	 * The new elements will appear in the list in the order that they are returned by the specified collection's iterator.
	 *
	 * @param index      at which to insert the first element from the specified collection
	 * @param collection containing elements to be added to this list
	 * @return true if this structure changed as a result of the call
	 */
	default boolean addAll(int index, Collection<V> collection) {
		this.shiftIndexes(index, collection.size());

		int i = 0;
		for (V object : collection)
			this.put((K) (Object) (index + i++), object);

		return collection.size() != 0;
	}

	/**
	 * The maximum integer. That have been stored as a key in this.
	 *
	 * @return the maximum index of this
	 */
	default int maxIndex() {
		int i = -1;

		for (Map.Entry<?, ?> entry : this.entrySet()) {
			Object k = entry.getKey();
			if (k instanceof Integer && ((Integer) k) > i)
				i = (Integer) k;
		}

		return i;
	}

	/**
	 * The minimum integer. That have been stored as a key in this.
	 *
	 * @return the minimum index of this
	 */
	default int minIndex() {
		int i = 1;

		for (Map.Entry<?, ?> entry : this.entrySet()) {
			Object k = entry.getKey();
			if (k instanceof Integer && ((Integer) k) < i)
				i = (Integer) k;
		}

		return i;
	}

	/**
	 * Depending on current indexing position on this structure.
	 * Push an element at the index before the least index in this.
	 * <br><br><b>example:</b>
	 * <pre>
	 *     toString -> {-1:"-one", 0:"zero", 1:"one"}
	 *     push <- "-two"
	 *     toString -> {-2:"-two", -1:"-one", 0:"zero", 1:"one"}
	 * </pre>
	 *
	 * @param element to be pushed
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 */
	default V push(V element) {
		return this.put((K) (Object) (this.minIndex() - 1), element);
	}

	/**
	 * Inserts the specified element at the specified position in this structure indexing system.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the left (subtract one to their indices).
	 *
	 * @param index   index at which the specified element is to be inserted
	 * @param element element to be inserted
	 */
	default void push(int index, V element) {
		this.shiftIndexes(index, -1);
		this.put((K) (Object) index, element);
	}

	/**
	 * Appends all of the elements in the specified collection to the start of this structure indexing system,
	 * in the order that they are returned by the specified collection's Iterator.
	 * The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
	 *
	 * @param collection containing elements to be added to this structure
	 * @return true if this structure changed as a result of the call
	 */
	default boolean pushAll(Collection<V> collection) {
		int size = collection.size();
		int i = this.minIndex() - size;

		for (V element : collection)
			this.put((K) (Object) (++i), element);

		return size != 0;
	}

	/**
	 * Inserts all of the elements in the specified collection into this list, starting at the specified position.
	 * Shifts the element currently at that position (if any) and any subsequent elements to the left (subtract their indices).
	 * The new elements will appear in the list in the order that they are returned by the specified collection's iterator.
	 *
	 * @param index      at which to insert the first element from the specified collection
	 * @param collection containing elements to be added to this list
	 * @return true if this structure changed as a result of the call
	 */
	default boolean pushAll(int index, Collection<V> collection) {
		this.shiftIndexes(index, -collection.size());

		int i = 0;
		for (V object : collection)
			this.put((K) (Object) (index + i++), object);

		return collection.size() != 0;
	}

	/**
	 * Copies all of the elements from the specified collection to this structure.
	 * The effect of this call is equivalent to that of calling put(k, v) on this map
	 * once for each element from index i to element e in the specified collection.
	 * The behavior of this operation is undefined if the specified map is modified while the operation is in progress.
	 *
	 * @param collections elements to be stored in this map
	 */
	default void putAll(Collection<? extends V> collections) {
		int i = 0;
		for (V o : collections)
			this.put((K) (Object) i++, o);
	}

	/**
	 * Copies all of the elements from the specified array to this structure.
	 * The effect of this call is equivalent to that of calling put(k, v) on this map
	 * once for each element from index i to element e in the specified array.
	 * The behavior of this operation is undefined if the specified map is modified while the operation is in progress.
	 *
	 * @param array elements to be stored in this map
	 */
	default void putAll(V[] array) {
		for (int i = 0; i < array.length; i++)
			this.put((K) (Object) i, array[i]);
	}

	/**
	 * Shift indexes by the passed integer 'by',
	 * Starting from the passed index 'at'.
	 * <br><br><b>example:</b>
	 * <pre>
	 *     toString -> {-1:"-one", 0:"zero", 1:"one"}
	 *     shiftIndexes <- (0, 2)
	 *     toString -> {-1:"-one", 2:"zero", 3:"one"}
	 *     shiftIndexes <- (-1, -3)
	 *     toString -> {-4:"-one", 2:"zero", 3:"one"}
	 * </pre>
	 *
	 * @param at from where to start
	 * @param by how much to shift
	 */
	default void shiftIndexes(int at, int by) {
		HashMap<Integer, Object> newMap = new HashMap<>();

		this.entrySet().forEach(entry -> {
			Object key = entry.getKey();
			if (key instanceof Integer && (by > 0 ? ((Integer) key >= at) : ((Integer) key <= at)))
				newMap.put((Integer) key, entry.getValue());
		});

		newMap.forEach((k, v) -> {
			this.remove(k, v);
			((Map<Integer, Object>) this).put(k + by, v);
		});
	}
}
