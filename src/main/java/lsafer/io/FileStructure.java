package lsafer.io;

import lsafer.util.Structure;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File} as a third IO container
 * <p>
 * make sure your {@link FileStructure file-structure} matches all {@link IOStructure io-structures} rules
 *
 * @author LSaferSE
 * @version 7 release (19-Jul-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess"})
public abstract class FileStructure extends IOStructure<File> {

    /**
     * init this.
     *
     * @param arguments to init this
     */
    public FileStructure(Object... arguments) {
        super(arguments);
    }

    /**
     * get new instance with specific remote and load it.
     *
     * @param klass     of structure to run instance of
     * @param remote    IO container remote
     * @param arguments to pass to the constructor
     * @param <F>       type of the structure
     * @return new instance with pre-set remote
     */
    public static <F extends FileStructure> F load(Class<? extends F> klass, java.io.File remote, Object... arguments) {
        F structure = Structure.newInstance(klass, arguments);
        structure.$remote = remote == null ? structure.$remote : new File(remote);
        structure.load();
        return structure;
    }

    /**
     * get new instance with specific remote and load it.
     *
     * @param klass     of structure to run instance of
     * @param remote    IO container remote
     * @param arguments to pass to the constructor
     * @param <F>       type of the structure
     * @return new instance with pre-set remote
     */
    public static <F extends FileStructure> F load(Class<? extends F> klass, String remote, Object... arguments) {
        F structure = Structure.newInstance(klass, arguments);
        structure.$remote = remote == null ? structure.$remote : new File(remote);
        structure.load();
        return structure;
    }

    @Override
    public boolean check() {
        return this.$remote.exists() && !this.$remote.isDirectory();
    }

    @Override
    public boolean delete() {
        return this.$remote.delete();
    }

    /**
     * set the targeted File.
     *
     * @param remote targeted file
     */
    /*final*/
    public void remote(java.io.File remote) {
        super.remote(new File(remote));
    }

    /**
     * set the targeted File.
     *
     * @param remote targeted file
     */
    /*final*/
    public void remote(String remote) {
        super.remote(new File(remote));
    }

    @Override
    public abstract void load();

    @Override
    public abstract boolean save();

    /**
     * move {@link #$remote targeted file} to the given file.
     *
     * @param parent to move to
     * @return success of moving
     */
    public boolean move(File parent) {
        boolean w = this.$remote.move(parent);
        this.$remote = this.$remote.self;
        return w;
    }

    /**
     * move {@link #$remote targeted file} to the given file.
     *
     * @param parent to move to
     * @return success of moving
     */
    /*final*/
    final public boolean move(java.io.File parent){
        return this.move(new File(parent));
    }

    /**
     * move {@link #$remote targeted file} to the given file.
     *
     * @param parent to move to
     * @return success of moving
     */
    /*final*/
    public boolean move(String parent){
        return this.move(new File(parent));
    }

    /**
     * rename {@link #$remote targeted file} to the given name.
     *
     * @param name to rename to
     * @return success of the renaming
     */
    public boolean rename(String name) {
        boolean w = this.$remote.rename(name);
        this.$remote = this.$remote.self;
        return w;
    }

}
