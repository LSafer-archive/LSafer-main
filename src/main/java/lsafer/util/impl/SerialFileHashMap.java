package lsafer.util.impl;

import lsafer.io.SerialFileMap;
import lsafer.util.HybridMap;
import lsafer.util.JetMap;

import java.util.Map;

/**
 * An implement of {@link SerialFileMap} and {@link JetMap} and {@link HybridMap} to {@link java.util.HashMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 1 release (03-Nov-2019)
 * @since 03-Nov-2019
 * @see java.io.Serializable
 */
public class SerialFileHashMap<K, V> extends AbstractFileHashMap<K, V> implements SerialFileMap<K, V> {
	/**
	 * Default constructor.
	 */
	public SerialFileHashMap() {
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75).
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
	public SerialFileHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is negative or the load factor is nonpositive
	 */
	public SerialFileHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new HashMap with the same mappings as the specified Map. The HashMap is created with default load factor (0.75) and an initial
	 * capacity sufficient to hold the mappings in the specified Map.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 * @throws NullPointerException if the specified map is null
	 */
	public SerialFileHashMap(Map<? extends K, ? extends V> map) {
		super(map);
	}
}
