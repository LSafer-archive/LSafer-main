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
 * An abstract for string parsers. The purpose of string-parsers is
 * to parse strings into objects. Or stringify objects into strings.
 * To whether store it. Or show it as a text.
 * <br>
 * As a string-parser that extends this class. You just have to navigate
 * this class to where your parsing/stringing methods is. By using the following
 * annotations.
 *
 * <ul>
 * <li>{@link QueryMethod} for methods that tells this class what type a string should be parsed to</li>
 * <li>{@link ParsingMethod} for methods that parse strings into objects</li>
 * <li>{@link StringingMethod} for methods that stringify objects</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 2 release (28-Sep-19)
 * @since 28-Sep-19
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class StringParser {
	/**
	 * A map that stores previously solved parser-methods to improve performance.
	 */
	final protected Map<String, Method> parsers = new HashMap<>();

	/**
	 * A map that stores previously solved stringifier-methods to improve performance.
	 */
	final protected Map<String, Method> stringifiers = new HashMap<>();

	/**
	 * Parse the given string to an object that matches it.
	 *
	 * @param parser the class to be used (MUST HAVE PUBLIC INSTANCE)
	 * @param string to be parsed
	 * @return an object result from parsing the given string
	 */
	public static Object Parse(Class<? extends StringParser> parser, String string) {
		try {
			return ((StringParser) parser.getField("instance").get(null)).parse(string);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stringify the given object. Depending on the stringing methods in this parser.
	 *
	 * @param parser the class to be used (MUST HAVE PUBLIC INSTANCE)
	 * @param object to be stringed
	 * @return a string representation of the object.
	 */
	public static String Stringify(Class<? extends StringParser> parser, Object object) {
		try {
			return ((StringParser) parser.getField("instance").get(null)).stringify(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stringify the given object. Depending on the stringing methods in this parser.
	 *
	 * @param parser the class to be used (MUST HAVE PUBLIC INSTANCE)
	 * @param object to be stringed
	 * @param shift  the shift that the string should have
	 * @return a string representation of the object.
	 */
	public static String Stringify(Class<? extends StringParser> parser, Object object, String shift) {
		try {
			return ((StringParser) parser.getField("instance").get(null)).stringify(object, shift);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse the given string to an object that matches it.
	 *
	 * @param string to be parsed
	 * @return an object result from parsing the given string
	 */
	public Object parse(String string) {
		Class<?> klass = this.queryc(string);

		if (klass != null) {
			Method parser = this.queryp(klass);

			if (parser != null)
				try {
					return parser.invoke(this, string);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
		}

		return string;
	}

	/**
	 * Query what's the suitable class for the given string.
	 *
	 * @param string to query a suitable class for
	 * @return the suitable class for the given string
	 */
	public Class<?> queryc(String string) {
		for (Method method : this.getClass().getMethods())
			if (method.isAnnotationPresent(QueryMethod.class))
				try {
					if ((boolean) method.invoke(this, string))
						return method.getAnnotation(QueryMethod.class).value();
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}

		return Object.class;
	}

	/**
	 * Query what's the method to parse the given type.
	 *
	 * @param type to query a method for
	 * @return the method to parse the given type. Or null if this class don't have one
	 */
	protected Method queryp(Class<?> type) {
		String key = type.getName();

		if (this.parsers.containsKey(key))
			return this.parsers.get(key);

		for (Method method : this.getClass().getMethods())
			if (method.isAnnotationPresent(ParsingMethod.class) && method.getReturnType() == type) {
				this.parsers.put(key, method);
				return method;
			}

		return null;
	}

	/**
	 * Query what's the method to stringify the given type.
	 *
	 * @param type to query a method for
	 * @return the method to stringify the given type. Or null if this class don't have one
	 */
	protected Method querys(Class<?> type) {
		String key = type.getName();

		if (this.stringifiers.containsKey(key))
			return this.stringifiers.get(key);

		for (Method method : this.getClass().getMethods())
			if (method.isAnnotationPresent(StringingMethod.class) && method.getParameterTypes()[0].isAssignableFrom(type)) {
				this.stringifiers.put(key, method);
				return method;
			}

		return null;
	}

	/**
	 * Stringify the given object. Depending on the stringing methods in this parser.
	 *
	 * @param object to be stringed
	 * @return a string representation of the object.
	 */
	public String stringify(Object object) {
		return this.stringify(object, "");
	}

	/**
	 * Stringify the given object. Depending on the stringing methods in this parser.
	 *
	 * @param object to be stringed
	 * @param shift  the shift that the string should have
	 * @return a string representation of the object.
	 */
	public String stringify(Object object, String shift) {
		if (object.getClass().isArray())
			Arrays.objective(object);

		Method method = this.querys(object.getClass());

		if (method != null)
			try {
				switch (method.getParameters().length) {
					case 1:
						return (String) method.invoke(this, object);
					case 2:
						return (String) method.invoke(this, object, shift);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}

		return String.valueOf(object);
	}

	/**
	 * Navigate the {@link StringParser} class that the annotated method is a parsing method.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface ParsingMethod {
	}

	/**
	 * Navigate the {@link StringParser} class that the annotated method is a string-type-detecting method.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface QueryMethod {
		/**
		 * Tells what class the annotated method is looking for.
		 *
		 * @return the class the annotated method is looking for
		 */
		Class<?> value();
	}

	/**
	 * Navigate the {@link StringParser} class that the annotated method is a stringing method.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected @interface StringingMethod {
	}
}
