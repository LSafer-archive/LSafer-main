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
package lsafer.util.impl;

import java.lang.annotation.Annotation;

import lsafer.io.File;
import lsafer.io.FileMap;
import lsafer.io.FolderMap;
import lsafer.util.HybridMap;
import lsafer.util.JetMap;

/**
 * An implement of 3 interfaces. To be a map that have a 3rd folder container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 1 release (28-Sep-19)
 * @since 28-Sep-19
 */
@SuppressWarnings("unused")
public class FolderHashMap<K, V> extends IOHashMap<File, K, V> implements FolderMap<K, V>, JetMap<K, V>, HybridMap<K, V> {
	/**
	 * The default file-map to initialize. for files found but no matching entries for them.
	 */
	private Class<? extends FileMap> file;

	/**
	 * The default folder-map to initialize. for files found but no matching entries for them.
	 */
	private Class<? extends FolderMap> folder;

	/**
	 * Default constructor.
	 */
	public FolderHashMap() {
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
	public <A extends Annotation> A configurations(Class<A> type, Class<?> defaults) {
		A configurations = FolderMap.super.configurations(type, defaults);

		if (type == FolderMap.Configurations.class && configurations == FolderMap.class.getAnnotation(FolderMap.Configurations.class))
			return (A) new FolderMap.Configurations() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return FolderMap.Configurations.class;
				}

				@Override
				public Class<? extends FileMap> file() {
					return FolderHashMap.this.file == null ? ((FolderMap.Configurations) configurations).file() : FolderHashMap.this.file;
				}

				@Override
				public Class<? extends FolderMap> folder() {
					return FolderHashMap.this.folder == null ? ((FolderMap.Configurations) configurations).folder() : FolderHashMap.this.folder;
				}
			};

		return configurations;
	}
}
