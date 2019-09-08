package lsafer.io;

/**
 * A structure linked with {@link java.util.Map} as a secondary container. And a {@link File} as a third IO container.
 *
 * <ul>
 * <li>note: make sure your {@link FileStructure file-structure} matches all {@link IOStructure io-structures} rules.</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 8 release (06-Sep-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "unused"})
@IOStructure.Defaults(remote = File.class)
public abstract class FileStructure extends IOStructure<File> {
    /**
     * Delete the linked {@link File}.
     *
     * @return success of deleting
     * @see File#delete()
     */
    public boolean delete() {
        return this.remote.delete();
    }

    /**
     * Check if the linked {@link File} is available or not.
     *
     * @return whether the linked file is available or not
     */
    public boolean exist() {
        return this.remote.exists() && !this.remote.isDirectory();
    }

    /**
     * Move the linked {@link File} to the given file.
     *
     * @param parent to move to
     * @return success of moving
     */
    public boolean move(java.io.File parent) {
        boolean w = this.remote.move(parent);
        this.remote = this.remote.self;
        return w;
    }

    /**
     * Move the linked {@link File} to the given file.
     *
     * @param parent to move to
     * @return success of moving
     */
    public boolean move(String parent) {
        return this.move(new File(parent));
    }

    /**
     * Link this with a new {@link File}.
     *
     * @param remote to be linked
     * @param <F>    this
     * @return this
     */
    public <F extends FileStructure> F remote(java.io.File remote) {
        return super.remote(new File(remote));
    }

    /**
     * Link this with a new {@link File} from the given file-path.
     *
     * @param remote to be linked
     * @param <F>    this
     * @return this
     */
    public <F extends FileStructure> F remote(String remote) {
        return (F) this.remote(new File(remote));
    }

    /**
     * Rename the linked {@link File} to the given name.
     *
     * @param name to rename to
     * @return success of the renaming
     */
    public boolean rename(String name) {
        boolean w = this.remote.rename(name);
        this.remote = this.remote.self;
        return w;
    }

    /**
     * Load this from the linked {@link File}.
     *
     * @param <F> this
     * @return this
     */
    public abstract <F extends FileStructure> F load();

    /**
     * Save this to the linked {@link File}.
     *
     * @return success of saving
     */
    abstract public boolean save();
}
