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
package lsafer.util;

import java.lang.annotation.Annotation;

/**
 * Defines that the implement class have a configurations.
 *
 * @author LSaferSE
 * @version 1 release (28-Sep-19)
 * @since 28-Sep-19
 */
public interface Configurable {
	/**
	 * Get the configurations annotation annotated to this structure.
	 * Or the default_class's default configurations.
	 *
	 * @param type     the type of the annotation targeted
	 * @param defaults the class to get the default annotation in case it not found in this
	 * @param <A>      targeted annotation's type
	 * @return the targeted configurations of this structure
	 * @throws NullPointerException if the given (annotation class/the defaults object) is null
	 */
	default <A extends Annotation> A configurations(Class<A> type, Class<? /*annotated by A*/> defaults) {
		A annotation = this.getClass().getAnnotation(type);
		return annotation == null ? defaults.getAnnotation(type) : annotation;
	}
}
