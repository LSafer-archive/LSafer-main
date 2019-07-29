package lsafer.io;

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

    @Override
    public boolean check() {
        return this.$remote.exists() && !this.$remote.isDirectory();
    }

    @Override
    public boolean delete() {
        return this.$remote.delete();
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
