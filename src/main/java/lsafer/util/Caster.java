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
package lsafer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that contains a casting methods that designed to cast for each casting situation.
 * <br>
 * Each casting method is (suppose to be) designed to be invoked by {@link #cast(Class, Object, boolean)} ) the main dynamic casting method}.
 *
 * @author LSaferSE
 * @version 5 release (11-Oct-19)
 * @since 31-Aug-19
 */
public abstract class Caster {
	/**
	 * To avoid repetitive method searching.
	 */
	final protected Map<String, Method> casters = new HashMap<>();

	/**
	 * Cast the given Object to the targeted class. By searching for a matching method then invoke it then return the results of it.
	 *
	 * <ul>
	 * <li>
	 * note: after finding a matching method. It'll be stored for next time casts so
	 * the next casts will be faster. (using {@link #casters Methods Map}.
	 * </li>
	 * </ul>
	 *
	 * @param klass  to cast the object to
	 * @param object to be casted
	 * @param clone  whether you want the object be cloned if it's instance of the given class or you want the same instance
	 * @param <T>    type of the targeted class
	 * @return the given object casted to the given class, or null case casting failure
	 */
	public <T> T cast(Class<T> klass, Object object, boolean clone) {
		if (object == null || (!clone && klass.isInstance(object)))
			return (T) object;
		if (object.getClass().isArray() && !(object instanceof Object[]))
			object = Arrays.objective(object);

		if (object instanceof User) {
			T casted = ((User) object).castTo(klass);
			if (casted != null)
				return casted;
		}

		klass = (Class<T>) Classes.objective(klass);
		Method method = this.queryCastingMethod(object.getClass(), klass);
		if (method != null)
			try {
				switch (method.getParameters().length) {
					case 1:
						return klass.cast(method.invoke(this, object));
					case 2:
						return klass.cast(method.invoke(this, klass, object));
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}

		return null;
	}

	/**
	 * Cast the given Object to the targeted class. By searching for a matching method then invoke it then return the results of it.
	 *
	 * <ul>
	 * <li>
	 * note: after finding a matching method. It'll be stored for next time casts so
	 * the next casts will be faster. (using {@link #casters Methods Map}.
	 * </li>
	 * </ul>
	 *
	 * @param klass  to cast the object to
	 * @param object to be casted
	 * @param <T>    type of the targeted class
	 * @return the given object casted to the given class, or null case casting failure
	 */
	public <T> T cast(Class<T> klass, Object object) {
		return this.cast(klass, object, false);
	}

	/**
	 * Find a method that casts any of the given 'input' class. to the given 'output' class.
	 *
	 * @param input  type that the targeted method can cast
	 * @param output type that the targeted method can return
	 * @return a method that casts the given input class to the given output class
	 */
	public Method queryCastingMethod(Class<?> input, Class<?> output) {
		output = Classes.objective(output);
		input = Classes.objective(input);

		String key = input.getName() + output.getName();

		if (this.casters.containsKey(key))
			return this.casters.get(key);

		query:
		for (Method method : this.getClass().getMethods()) {
			CastingMethod annotation = method.getAnnotation(CastingMethod.class);

			if (annotation == null)
				continue;

			Class<?>[] params = method.getParameterTypes();
			Class<?> param;

			switch (params.length) {
				case 1:
					param = params[0];
					break;
				case 2:
					param = params[1];
					break;
				default:
					continue;
			}

			if (!param.isAssignableFrom(input))
				continue;

			Class<?> type = method.getReturnType();

			for (Class<?> excluded : annotation.exclude())
				if (excluded.isAssignableFrom(output))
					continue query;

			if (type != output && !(annotation.subs() && type.isAssignableFrom(output)) && !(annotation.supers() && output.isAssignableFrom(type)))
				continue;

			this.casters.put(key, method);
			return method;
		}

		return null;
	}

	/**
	 * The annotation to declare what casting methods.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface CastingMethod {
		/**
		 * Output classes that the annotated method dose not support as a return value-type.
		 *
		 * @return excluded supported output-classes
		 */
		Class<?>[] exclude() default {};

		/**
		 * Allow the case "the targeted class is instance of the return class".
		 * <br><br><b>example:</b>
		 * <pre>
		 *     return Target cast(Class&lt;? instanceOf Target&gt; klass, Object object)
		 * </pre>
		 *
		 * @return if the output is a sub output
		 */
		boolean subs() default false;

		/**
		 * Allow the case "the targeted class is a super class of the return class".
		 * <br><br><b>example</b>
		 * <pre>
		 *     return Target cast(Class&lt;? super Target&gt; klass, Object object)
		 * </pre>
		 *
		 * @return if the output is a super output
		 */
		boolean supers() default false;
	}

	/**
	 * Defines that the implement class is a {@link Caster} user.
	 */
	public interface User {
		/**
		 * Cast this object to the given class. <br/> Note: this is a class based casting. And it don't use the caster of this object. you can use
		 * {@link #clone(Class)} instead. <br/> Implementation Note: this method will override caster methods. Return null to skip to the caster
		 * casting methods.
		 *
		 * @param klass to cast this object to
		 * @param <T>   the targeted type
		 * @return this object casted to the given class. Or null if this fails to cast
		 */
		default <T> T castTo(Class<T> klass) {
			return null;
		}

		/**
		 * Get the caster used by this.
		 *
		 * @return the caster used by this
		 */
		default Caster caster() {
			return DefaultCaster.global;
		}

		/**
		 * Clone this class into a new instance of the given class.
		 *
		 * @param klass to get a new instance of
		 * @param <T>   type of the new clone
		 * @return a new clone casted from this to the given class
		 */
		default <T> T clone(Class<T> klass) {
			return this.caster().cast(klass, this, true);
		}
	}
}
