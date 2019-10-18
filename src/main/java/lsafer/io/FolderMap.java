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

import lsafer.util.Configurable;
import lsafer.util.impl.FolderHashMap;
import lsafer.util.impl.JSONFileHashMap;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A {@link Map} that is linked to {@link File Folder} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 8 release (17-Oct-19)
 * @since 19-Jul-19
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@FolderMap.Configurations
public interface FolderMap<K, V> extends FileMap<K, V>, Configurable {
	@Override
	default boolean exist() {
		File remote = this.remote();
		return remote.exists() && remote.isDirectory();
	}

	@Override
	default Map<K, V> read() {
		Configurations configurations = this.configurations(Configurations.class, FolderMap.class);
		Map<K, V> map = new HashMap<>();

		this.remote().children().forEach(file -> {
			try {
				String key = file.getName();
				FileMap<?, ?> value = file.isDirectory() ?
									  configurations.folder().getDeclaredConstructor(new Class[0]).newInstance() :
									  configurations.file().getDeclaredConstructor(new Class[0]).newInstance();

				value.remote(file);
				value.load();
				map.put((K) key, (V) value);
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		});

		return map;
	}

	@Override
	default <F extends FileMap> F load(BiConsumer<K, V> removed, BiConsumer<K, V> added) {
		Configurations configurations = this.configurations(Configurations.class, FolderMap.class);
		Set<String> children = this.remote().children0();
		Set<K> remove = new HashSet<>();

		this.entrySet().forEach(entry -> {
			String key = String.valueOf(entry.getKey());

			if (children.contains(key)) {
				V value = entry.getValue();
				if (value instanceof FileMap) {
					((FileMap<?, ?>) value).remote(this.remote().child(key));
					((FileMap<?, ?>) value).load();
				}
				children.remove(key);
			} else {
				remove.add((K) key);
			}
		});

		remove.forEach((key) -> {
			V value = this.remove(key);
			removed.accept(key, value);
		});
		children.forEach(key -> {
			try {
				File file = new File(key);
				FileMap<?, ?> value = (file.isDirectory() ?
									   configurations.folder().getDeclaredConstructor(new Class[0]).newInstance() :
									   configurations.file().getDeclaredConstructor(new Class[0]).newInstance());
				value.remote(file);
				value.load();
				this.put((K) key, (V) value);
				added.accept((K) key, (V) value);
			} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
		return (F) this;
	}

	@Override
	default boolean move(java.io.File parent) {
		boolean w = FileMap.super.move(parent);
		File remote = this.remote();

		if (w)
			for (Map.Entry<K, V> entry : this.entrySet()) {
				V value = entry.getValue();

				if (value instanceof FileMap)
					w &= ((FileMap) value).move(this.remote());
			}

		return w;
	}

	@Override
	default boolean rename(String name) {
		boolean w = FileMap.super.rename(name);
		File remote = this.remote();

		if (w)
			for (Map.Entry<K, V> entry : this.entrySet()) {
				V value = entry.getValue();

				if (value instanceof FileMap)
					w &= ((FileMap) value).move(remote);
			}

		return w;
	}

	@Override
	default boolean save() {
		boolean w = this.remote().mkdirs();

		if (w)
			for (Map.Entry<K, V> entry : this.entrySet()) {
				V value = entry.getValue();
				if (value instanceof FileMap)
					w &= ((FileMap) value).save();
			}

		return w;
	}

	/**
	 * Apply the remote of this. To every {@link FileMap} contained in this.
	 *
	 * @param <F> this
	 * @return this
	 */
	default <F extends FolderMap> F applyRemote() {
		File remote = this.remote();
		this.forEach(((key, value) -> {
			if (key instanceof String && value instanceof FileMap) {
				((FileMap<?, ?>) value).remote(remote.child(((String) key)));

				if (value instanceof FolderMap)
					((FolderMap<?, ?>) value).applyRemote();
			}
		}));
		return (F) this;
	}

	/**
	 * Set the default values for the targeted folder-map.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.TYPE_USE})
	@Inherited
	@interface Configurations {
		/**
		 * The default file-map to initialize. for files found but no matching fields for them.
		 *
		 * @return default file-map class
		 */
		Class<? extends FileMap> file() default JSONFileHashMap.class;

		/**
		 * The default folder-map to initialize. for folders found but no matching fields for them.
		 *
		 * @return default folder-map class
		 */
		Class<? extends FolderMap> folder() default FolderHashMap.class;
	}
}
