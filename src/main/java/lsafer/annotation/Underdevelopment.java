package lsafer.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotate that the targeted element is still uncompleted.
 * And it's not safe to depend on it or use it.
 *
 * @author LSaferSE
 * @version 2 beta (28-Sep-19)
 * @since 06-Sep-19
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
public @interface Underdevelopment {
	/**
	 * The current development state of the target.
	 *
	 * @return the current development state.
	 */
	String state() default "";

	/**
	 * The message why the target is uncompleted.
	 *
	 * @return why the target is not completed yet
	 */
	String value() default "";
}
