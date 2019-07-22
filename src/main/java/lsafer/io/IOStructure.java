package lsafer.io;

import lsafer.lang.Structurable;
import lsafer.util.Structure;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and IO port as a third container
 * <p>
 * make sure your {@link IOStructure io-structure} matches all {@link Structure structures} rules
 *
 * @param <REMOTE> type of the targeted data solid container.
 * @author LSaferSE
 * @version 3 release (19-Jul-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings("WeakerAccess")
public abstract class IOStructure<REMOTE> extends Structure {

    /**
     * IO container's remote.
     */
    protected REMOTE $remote;

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
     * @param klass      of structure to run instance of
     * @param remote     IO container remote
     * @param arguments  to pass to the constructor
     * @param <INSTANCE> type of the structure
     * @param <REMOTE>   type of the remote
     * @return new instance with pre-set remote
     */
    public static <REMOTE, INSTANCE extends IOStructure<REMOTE>> INSTANCE load(Class<INSTANCE> klass, REMOTE remote, Object... arguments) {
        INSTANCE structure = Structurable.newInstance(klass, arguments);
        structure.$remote = remote == null ? structure.$remote : remote;
        structure.load();
        return structure;
    }

    /**
     * copy this structure as other class
     * and copy the IO container remote if possible.
     *
     * @param klass   to copy to
     * @param <CLONE> type of the class
     * @return new instance with same values of this but different class
     */
    @Override
    public <CLONE extends Structurable> CLONE clone(Class<CLONE> klass) {
        CLONE structure = super.clone(klass);

        if (structure instanceof IOStructure)
            try {
                ((IOStructure<REMOTE>) structure).$remote = this.$remote;
            } catch (ClassCastException e) {
                //coping this to a different target type data structure
            }

        return structure;
    }

    /**
     * set the IO container's remote.
     *
     * @param remote IO container remote
     */
    public void remote(REMOTE remote) {
        this.$remote = remote;
    }

    /**
     * get the IO container's remote.
     *
     * @return IO container's remote
     */
    public REMOTE remote() {
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
