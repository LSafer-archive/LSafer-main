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

import java.util.HashMap;

import lsafer.util.HybridMap;
import lsafer.util.JetMap;

/**
 * An implementation of {@link JetMap} and {@link HybridMap} to {@link HashMap}.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (28-Sep-19)
 * @since 18-Sep-19
 */
@SuppressWarnings({"unused"})
public class JetHashMap<K, V> extends HashMap<K, V> implements JetMap<K, V>, HybridMap<K, V> {
}
