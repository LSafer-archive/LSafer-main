package lsafer.io;

import lsafer.util.AbstractStructure;
import lsafer.util.Structure;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and IO port as a third container
 * <p>
 * make sure your {@link IOStructure io-structure} matches all {@link AbstractStructure structures} rules
 *
 * @param <R> type of the targeted data solid container.
 * @author LSaferSE
 * @version 4 release (19-Jul-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings("WeakerAccess")
public abstract class IOStructure<R> extends AbstractStructure {

    /**
     * IO container's remote.
     */
    protected R $remote;

    /**
     * init this.
     *
     * @param arguments to init with
     */
    public IOStructure(Object... arguments) {
        super(arguments);
    }

    /**
     * get new instance with specific remote and load it.
     *
     * @param klass     of structure to run instance of
     * @param remote    IO container remote
     * @param arguments to pass to the constructor
     * @param <I>       type of the structure
     * @param <R>       type of the remote
     * @return new instance with pre-set remote
     */
    public static <R, I extends IOStructure<R>> I load(Class<? extends I> klass, R remote, Object... arguments) {
        I structure = Structure.newInstance(klass, arguments);
        structure.$remote = remote == null ? structure.$remote : remote;
        structure.load();
        return structure;
    }

    /**
     * copy this structure as other class
     * and copy the IO container remote if possible.
     *
     * @param klass to copy to
     * @param <S>   type of the class
     * @return new instance with same values of this but different class
     */
    @Override
    public <S extends Structure> S clone(Class<S> klass) {
        S structure = super.clone(klass);

        if (structure instanceof IOStructure)
            try {
                ((IOStructure<R>) structure).$remote = this.$remote;
            } catch (ClassCastException ignored) {
                //coping this to a different target type data structure
            }

        return structure;
    }

    /**
     * set the IO container's remote.
     *
     * @param remote IO container remote
     */
    public void remote(R remote) {
        this.$remote = remote;
    }

    /**
     * get the IO container's remote.
     *
     * @return IO container's remote
     */
    public R remote() {
        return this.$remote;
    }

    /**
     * check if the IO container is available or not.
     *
     * @return whether the IO container is available or not
     */
    abstract public boolean check();

    /**
     * delete the IO container.
     *
     * @return success of deleting
     */
    abstract public boolean delete();

    /**
     * load this from the IO container.
     */
    abstract public void load();

    /**
     * save this to the IO container.
     *
     * @return success of saving
     */
    abstract public boolean save();

}
