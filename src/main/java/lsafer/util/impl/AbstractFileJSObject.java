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
import lsafer.util.JSObject;

import java.io.Serializable;

/**
 * An abstract to implement needed methods in the interfaces {@link JSObject} and {@link Serializable} and {@link FileMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (02-Nov-19)
 * @since 28-Sep-19
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractFileJSObject<K, V> extends AbstractJSObject<K, V> implements FileMap<K, V> {
	/**
	 * The file targeted by this.
	 */
	protected File file;

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public File setFile(File file) {
		File old = this.file;
		this.file = file;
		return old;
	}
}
