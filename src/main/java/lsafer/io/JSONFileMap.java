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

import java.util.HashMap;
import java.util.Map;

import lsafer.json.JSON;

/**
 * A {@link Map} that is linked to {@link File JSON-File} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 8 release (28-Sep-2019)
 * @see JSON
 * @since 11-Jul-19
 */
public interface JSONFileMap<K, V> extends FileMap<K, V> {
	@Override
	default <F extends FileMap> F load() {
		//noinspection unchecked
		this.putAll(this.remote().read(JSON.class, Map.class, HashMap::new));
		return (F) this;
	}

	@Override
	default boolean save() {
		return this.remote().write(JSON.class, this);
	}
}
