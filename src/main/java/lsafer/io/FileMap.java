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
package lsafer.io;

import lsafer.util.Caster;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A {@link Map} that is linked to {@link File} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 13 release (16-Oct-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@IOMap.Configurations(remote = File.class)
public interface FileMap<K, V> extends IOMap<File, K, V>, Caster.User {
	@Override
	default File xremote(Object remote) {
		File old = this.remote();
		this.remote(this.caster().cast(File.class, remote));
		return old;
	}

	/**
	 * Delete the linked {@link File}.
	 *
	 * @return success of deleting
	 * @see File#delete()
	 */
	default boolean delete() {
		return this.remote().delete();
	}

	/**
	 * Check if the linked {@link File} is available or not.
	 *
	 * @return whether the linked file is available or not
	 */
	default boolean exist() {
		File remote = this.remote();
		return remote.exists() && !remote.isDirectory();
	}

	/**
	 * Move the linked {@link File} to the given file.
	 *
	 * @param parent to move to
	 * @return success of moving
	 */
	default boolean move(java.io.File parent) {
		File remote = this.remote();
		boolean w = remote.move(parent);
		this.remote(remote.self);
		return w;
	}

	/**
	 * Move the linked {@link File} to the given file.
	 *
	 * @param parent to move to
	 * @return success of moving
	 */
	default boolean move(String parent) {
		return this.move(new File(parent));
	}

	/**
	 * Rename the linked {@link File} to the given name.
	 *
	 * @param name to rename to
	 * @return success of the renaming
	 */
	default boolean rename(String name) {
		File remote = this.remote();
		boolean w = remote.rename(name);
		this.remote(remote.self);
		return w;
	}

	/**
	 * Read the contents of this file as a map and return it.
	 *
	 * @return a map of contents of this file
	 */
	Map<K, V> read();

	/**
	 * Load this from the linked {@link File}.
	 *
	 * @param <F> this
	 * @return this
	 */
	default <F extends FileMap> F load() {
		return this.load((k, v) -> {
		}, (k, v) -> {
		});
	}

	/**
	 * Load this from the linked {@link File}.
	 *
	 * @param removed to do with the removed entries
	 * @param added   to do with added entries
	 * @param <F>     this
	 * @return this
	 */
	default <F extends FileMap> F load(BiConsumer<K, V> removed, BiConsumer<K, V> added) {
		Map<K, V> map = this.read();
		Set<K> keys = map.keySet();
		Set<K> remove = new HashSet<>();

		this.keySet().forEach(k -> {
			if (keys.contains(k)) {
				this.put(k, map.get(k));
				keys.remove(k);
			} else {
				remove.add(k);
			}
		});

		remove.forEach(key -> {
			V value = this.remove(key);
			removed.accept(key, value);
		});
		keys.forEach(key -> {
			V value = map.get(key);
			this.put(key, value);
			added.accept(key, value);
		});
		return (F) this;
	}

	/**
	 * Save this to the linked {@link File}.
	 *
	 * @return success of saving
	 */
	boolean save();
}
