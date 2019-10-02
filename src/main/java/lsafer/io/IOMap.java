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
import java.util.function.Function;

import lsafer.util.Caster;
import lsafer.util.Configurable;

/**
 * A {@link Map} that is linked to some sort of IO-Container.
 *
 * @param <R> type of the remote of the third IO-port container.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 9 release (28-Sep-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@IOMap.Configurations
public interface IOMap<R, K, V> extends Map<K, V>, Configurable, Caster.User {
	@Override
	default <T> T clone(Class<T> klass) {
		T clone = Caster.User.super.clone(klass);

		if (clone instanceof IOMap) {
			R remote = this.remote();

			if (((Configurable) clone).configurations(Configurations.class, IOMap.class).remote().isInstance(remote))
				((IOMap<R, ?, ?>) clone).remote(remote);
		}

		return clone;
	}

	/**
	 * Replace this remote with a new remote.
	 *
	 * @param remote new remote function
	 * @param <I>    this
	 * @return this
	 */
	default <I extends IOMap> I remote(Function<R, R> remote) {
		this.remote(remote.apply(this.remote()));
		return (I) this;
	}

	/**
	 * Try to set the 3rd IO-container's remote to the passed remote.
	 *
	 * @param remote to be set
	 * @return the previous remote
	 */
	default R xremote(Object remote) {
		R old = this.remote();
		this.remote((R) remote);
		return old;
	}

	/**
	 * Get the IO container's remote.
	 *
	 * @return IO container's remote
	 */
	R remote();

	/**
	 * Set the IO container's remote to a new one.
	 *
	 * @param remote new remote
	 * @return previous remote
	 */
	R remote(R remote);

	/**
	 * The configuration of the annotated {@link IOMap}.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.TYPE_USE})
	@Inherited
	@interface Configurations {
		/**
		 * The type of the remote of the annotated {@link IOMap}.
		 *
		 * @return the remote type of the annotated map
		 */
		Class<?> remote() default Object.class;
	}
}
