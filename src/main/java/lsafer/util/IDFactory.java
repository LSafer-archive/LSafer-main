package lsafer.util;

import java.util.function.BiFunction;

/**
 * A factory that creates an Unused IDs (instance wise).
 *
 * @param <I> id type
 * @param <F> flavor type
 * @author LSaferSE
 * @version 1 alpha (07-Sep-19)
 * @since 07-Sep-19
 */
final public class IDFactory<I, F> {
    /**
     * The function to use to create new IDs.
     */
    private BiFunction<I, F, I> creator;

    /**
     * The last unused ID.
     *
     * <ul>
     * <li>note: this field should be always refer to unused value</li>
     * </ul>
     */
    private volatile I free;

    /**
     * Initialize this.
     *
     * @param first   ID to start from
     * @param creator the function to create new IDs; (old -> new)
     */
    public IDFactory(I first, BiFunction<I, F, I> creator) {
        this.free = first;
        this.creator = creator;
    }

    /**
     * Get an unused ID by any user of this factory.
     *
     * @param flavor flavor type
     * @return a new unused ID
     */
    public I newId(F flavor) {
        I id = this.free;

        synchronized (this) {
            this.free = this.creator.apply(this.free, flavor);
        }

        return id;
    }
}