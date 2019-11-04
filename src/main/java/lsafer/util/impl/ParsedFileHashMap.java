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
package lsafer.util.impl;

import lsafer.io.ParsedFileMap;
import lsafer.util.HybridMap;
import lsafer.util.JetMap;
import lsafer.util.StringParser;

import java.util.Map;

/**
 * An implement of {@link ParsedFileMap} and {@link JetMap} and {@link HybridMap} to {@link java.util.HashMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 3 release (02-Nov-2019)
 * @since 25-Sep-2019
 * @see lsafer.json.JSON
 */
public class ParsedFileHashMap<K, V> extends AbstractFileHashMap<K, V> implements ParsedFileMap<K, V>, JetMap<K, V>, HybridMap<K, V> {
	/**
	 * The parser to be used to parse/stringify the content from/to the file targeted by this.
	 */
	public StringParser parser;

	/**
	 * Default constructor.
	 */
	public ParsedFileHashMap() {
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75).
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
	public ParsedFileHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is negative or the load factor is nonpositive
	 */
	public ParsedFileHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new HashMap with the same mappings as the specified Map. The HashMap is created with default load factor (0.75) and an initial
	 * capacity sufficient to hold the mappings in the specified Map.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 * @throws NullPointerException if the specified map is null
	 */
	public ParsedFileHashMap(Map<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Initialize this with a specific string-parser.
	 *
	 * @param parser to be used to parse the targeted file
	 */
	public ParsedFileHashMap(StringParser parser) {
		this.parser = parser;
	}

	@Override
	public StringParser parser() {
		return this.parser == null ? ParsedFileMap.super.parser() : this.parser;
	}
}
