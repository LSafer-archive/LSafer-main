package lsafer.io;

import lsafer.structure.Structurable;

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
        for (File file : this.$remote.children())
            if (!this.containsKey(file.getName()))
                if (file.isDirectory())
                    this.put(file.getName(), Structurable.newInstance(this.folder_structure()));
                else
                    this.put(file.getName(), Structurable.newInstance(this.file_structure()));

        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure) {
                ((FileStructure) value).remote(this.$remote.child((String) key));
                ((FileStructure) value).load();
            }
        });
    }

    @Override
    public boolean save() {
        boolean[] w = {this.$remote.mkdirs()};

        if (w[0])
            this.map().forEach((key, value) -> {
                if (key instanceof String && value instanceof FileStructure)
                    w[0] &= ((FileStructure) value).save();
            });

        return w[0];
    }

    @Override
    public boolean move(File parent) {
        boolean[] w = {super.move(parent)};

        if (w[0])
            this.map().forEach((key, value) -> {
                if (key instanceof String && value instanceof FileStructure)
                    w[0] &= ((FileStructure) value).move(this.$remote);
            });

        return w[0];
    }

    @Override
    public boolean rename(String name) {
        boolean[] w = {super.rename(name)};

        if (w[0])
            this.map().forEach((key, value) -> {
                if (key instanceof String && value instanceof FileStructure)
                    w[0] &= ((FileStructure) value).move(this.$remote);
            });

        return w[0];
    }

    @Override
    public void remote(File file) {
        super.remote(file);
        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                ((FileStructure) value).remote(this.$remote.child((String) key));
        });
    }

    @Override
    public void reset() {
        super.reset();
        this.remote(this.$remote);
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
