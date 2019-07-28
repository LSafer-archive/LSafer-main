package lsafer.io;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File Folder} as a third IO container
 * <p>
 * make sure your {@link FolderStructure folder-structure} matches all {@link FileStructure file-structures} rules
 *
 * <p>
 * field rules:
 * <ul>
 * <li>no null default values</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 1 release (19-Jul-19)
 * @since 19-Jul-19
 */
@SuppressWarnings("WeakerAccess")
public class FolderStructure extends FileStructure {

    /**
     * init this.
     *
     * @param arguments to init with
     */
    public FolderStructure(Object... arguments) {
        super(arguments);
    }

    @Override
    public boolean check() {
        return this.$remote.exists() && this.$remote.isDirectory();
    }

    @Override
    public void load() {
        this.reset();
        for (File file : this.$remote.children()) {
            Class type = this.typeOf(file.getName());

            if (type == null) //that means there isn't ANY value (or field) mapped to the key
                this.put(file.getName(), IOStructure.load(file.isDirectory() ? this.folder_structure() : this.file_structure(), file));
            else if (FileStructure.class.isAssignableFrom(type))
                this.get(FileStructure.class, file.getName(), null).load();
        }
    }

    @Override
    public boolean save() {
        boolean[] w = {this.$remote.mkdirs()};

        if (w[0]) this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                w[0] &= ((FileStructure) value).save();
        });

        return w[0];
    }

    @Override
    public boolean move(File parent) {
        boolean[] w = {super.move(parent)};

        if (w[0]) this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                w[0] &= ((FileStructure) value).move(this.$remote);
        });

        return w[0];
    }

    @Override
    public boolean rename(String name) {
        boolean[] w = {super.rename(name)};

        if (w[0]) this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                w[0] &= ((FileStructure) value).move(this.$remote);
        });

        return w[0];
    }

    @Override
    public void remote(File file) {
        if (file != null)
            super.remote(file);
        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                ((FileStructure) value).remote(this.$remote.child((String) key));
        });
    }

    @Override
    public <T> T get(Object key) {
        if (!this.isIgnored(key)) {
            T value = super.get(key);

            if (key instanceof String && value instanceof FileStructure && ((FileStructure) value).remote() == null)
                ((FileStructure) value).remote(this.$remote.child((String) key));
        }

        return null;
    }

    @Override
    public Object put(Object key, Object value) {
        if (!this.isIgnored(key)) {
            value = super.put(key, value);

            if (key instanceof String && value instanceof FileStructure && ((FileStructure) value).remote() == null)
                ((FileStructure) value).remote(this.$remote.child((String) key));
        }

        return value;
    }

    @Override
    public void reset() {
        super.reset();
        this.remote(null);
    }

    /**
     * default structure for files.
     *
     * @return the default files structure's class
     */
    public Class<? extends FileStructure> file_structure() {
        return JSONFileStructure.class;
    }

    /**
     * default structure for folders.
     *
     * @return the default folders structure's class
     */
    public Class<? extends FolderStructure> folder_structure() {
        return FolderStructure.class;
    }

}
