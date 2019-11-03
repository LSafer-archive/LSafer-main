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

import lsafer.io.File;
import lsafer.io.FileMap;
import lsafer.io.FolderMap;
import lsafer.util.HybridMap;
import lsafer.util.JetMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Function;

/**
 * An implement of {@link FolderMap} and {@link JetMap} and {@link HybridMap} to {@link java.util.HashMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (02-Nov-2019)
 * @since 28-Sep-2019
 */
@SuppressWarnings("unused")
public class FolderHashMap<K, V> extends AbstractFileHashMap<K, V> implements FolderMap<K, V>, JetMap<K, V>, HybridMap<K, V> {
	/**
	 * The default file-map to initialize. for files found but no matching entries for them.
	 */
	public Class<? extends FileMap> file;
	/**
	 * The default folder-map to initialize. for files found but no matching entries for them.
	 */
	public Class<? extends FolderMap> folder;

	/**
	 * Default constructor.
	 */
	public FolderHashMap() {
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75).
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
	public FolderHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty HashMap with the specified initial capacity and load factor.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 * @throws IllegalArgumentException if the initial capacity is negative or the load factor is nonpositive
	 */
	public FolderHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new HashMap with the same mappings as the specified Map. The HashMap is created with default load factor (0.75) and an initial
	 * capacity sufficient to hold the mappings in the specified Map.
	 *
	 * @param map the map whose mappings are to be placed in this map
	 * @throws NullPointerException if the specified map is null
	 */
	public FolderHashMap(Map<? extends K, ? extends V> map) {
		super(map);
	}

	/**
	 * Initialize and override {@link FolderMap.Configurations#file()} and {@link FolderMap.Configurations#folder()}.
	 *
	 * @param folder default folder-map class to override
	 * @param file   default file-map class to override
	 */
	public FolderHashMap(Class<? extends FolderMap> folder, Class<? extends FileMap> file) {
		this.folder = folder;
		this.file = file;
	}

	@Override
	public FileMap newInstanceFor(File file) {
		try {
			Function<File, File> FILE = f -> file;

			if (file.isDirectory() && this.folder != null)
				try {
					return this.folder.getConstructor(Class.class, Class.class)
							.newInstance(this.folder, this.file).setFile(FILE);
				} catch (NoSuchMethodException ignored) {
					return this.folder.getConstructor().newInstance().setFile(FILE);
				}
			if (this.file != null)
				return this.file.getConstructor().newInstance().setFile(FILE);

			return FolderMap.super.newInstanceFor(file);
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
