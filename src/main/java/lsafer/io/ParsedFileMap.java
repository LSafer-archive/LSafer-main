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


import lsafer.json.JSON;
import lsafer.util.Configurable;
import lsafer.util.StringParser;

import java.util.Map;

/**
 * A map linked to a file is it's original source. And use {@link lsafer.util.StringParser} as a way to translate that source.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (02-Nov-2019)
 * @since 30-Oct-2019
 */
@ParsedFileMap.Configurations
public interface ParsedFileMap<K, V> extends FileMap<K, V>, Configurable {
	@Override
	default Map<K, V> read(File.Synchronizer<?, ?> synchronizer) {
		return this.getFile().read(synchronizer, this.parser(), Map.class);
	}

	@Override
	default void write(File.Synchronizer<?, ?> synchronizer, Map<K, V> map) {
		this.getFile().write(synchronizer, this.parser(), map);
	}

	/**
	 * Get the parser to be used by this.
	 *
	 * @return the parser of this
	 */
	default StringParser parser() {
		try {
			return (StringParser) this.configurations(Configurations.class, ParsedFileMap.class).parser().getField("global").get(null);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The configurations of {@link ParsedFileMap}s.
	 */
	@interface Configurations {
		/**
		 * The parser class to be used to parse file output from the linked remote of the annotated class.
		 *
		 * @return the parser class (should contain 'global' instance)
		 */
		Class<? extends StringParser> parser() default JSON.class;
	}
}
