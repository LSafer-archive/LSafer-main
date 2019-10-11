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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import lsafer.util.Configurable;
import lsafer.util.impl.FolderHashMap;
import lsafer.util.impl.JSONFileHashMap;

/**
 * A {@link Map} that is linked to {@link File Folder} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 7 release (28-Sep-19)
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
	default <F extends FileMap> F load() {
		for (File file : this.remote().children()) {
			V value = this.computeIfAbsent((K) file.getName(), key -> {
				try {
					return file.isDirectory() ?
						   (V) this.configurations(Configurations.class, FolderMap.class).folder().newInstance() :
						   (V) this.configurations(Configurations.class, FolderMap.class).file().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			});

			if (value instanceof FileMap) {
				((FileMap<?, ?>) value).remote(file);
				((FileMap) value).load();
			}
		}

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
