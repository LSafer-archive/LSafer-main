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
	default <A extends Annotation> A configurations(Class<A> type, Class<?> defaults) {
		A annotation = this.getClass().getAnnotation(type);
		return annotation == null ? defaults.getAnnotation(type) : annotation;
	}
}
