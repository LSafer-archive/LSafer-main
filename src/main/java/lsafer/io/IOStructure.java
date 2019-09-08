package lsafer.io;

import lsafer.util.HashStructure;
import lsafer.util.Structure;

import java.lang.annotation.*;
import java.util.function.Function;

import static lsafer.io.IOStructure.Defaults;

/**
 * A structure linked with {@link java.util.Map} as a secondary container. And an IO-port as a third container.
 *
 * <ul>
 * <li>note: make sure your {@link IOStructure io-structure} matches all {@link HashStructure hash-structures} rules.</li>
 * </ul>
 *
 * @param <R> type of the remote of the third IO-port container.
 * @author LSaferSE
 * @version 5 release (06-Sep-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
@Defaults
public abstract class IOStructure<R> extends HashStructure {
    /**
     * The remote of the third IO-port container.
     */
    @Destructed
    protected R remote;

    @Override
    public <S extends Structure> S clone(Class<S> klass) {
        S structure = super.clone(klass);

        if (structure instanceof IOStructure &&
            structure.getClass().getAnnotation(Defaults.class).remote().isInstance(this.remote))
            ((IOStructure<R>) structure).remote = this.remote;

        return structure;
    }

    /**
     * Get the IO container's remote.
     *
     * @return IO container's remote
     * @see #remote
     */
    public R remote() {
        return this.remote;
    }

    /**
     * Set the IO container's remote to a new one.
     *
     * @param remote new remote
     * @param <I>    this
     * @return this
     */
    public <I extends IOStructure> I remote(R remote) {
        this.remote = remote;
        return (I) this;
    }

    /**
     * Replace this remote with a new remote.
     *
     * @param remote new remote function
     * @param <I>    this
     * @return this
     */
    public <I extends IOStructure> I remote(Function<R, R> remote) {
        return this.remote(remote.apply(this.remote()));
    }

    /**
     * Set the default values for the targeted io-structure.
     */
    @Inherited
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Defaults {
        /**
         * The type of the remote of third IO-port container of the target.
         *
         * @return the remote type
         */
        Class<?> remote() default Object.class;
    }
}
