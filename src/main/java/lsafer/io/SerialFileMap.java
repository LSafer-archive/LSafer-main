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

import java.io.Serializable;
import java.util.Map;

/**
 * A {@link Map} that is linked to {@link File Serial-File} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 7 release (28-Sep-2019)
 * @see java.io.Serializable
 * @since 13-Jul-19
 */
@SuppressWarnings("unused")
public interface SerialFileMap<K, V> extends FileMap<K, V>, Serializable {
	@Override
	default <F extends FileMap> F load() {
		SerialFileMap structure = this.remote().readSerializable(SerialFileMap.class, () -> null);

		if (structure != null)
			//noinspection unchecked
			this.putAll(structure);

		return (F) this;
	}

	@Override
	default boolean save() {
		return this.remote().writeSerializable(this);
	}
}
