package lsafer.io;

import lsafer.util.Structure;

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
 * @version 2 release (19-Jul-19)
 * @since 19-Jul-19
 */
@SuppressWarnings("WeakerAccess")
public class FolderStructure extends FileStructure {

    @Override
    public boolean exist() {
        return this.remote.exists() && this.remote.isDirectory();
    }

    @Override
    public boolean move(java.io.File parent) {
        boolean[] w = {super.move(parent)};

        if (w[0]) this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                w[0] &= ((FileStructure) value).move(this.remote);
        });

        return w[0];
    }

    @Override
    public <I extends FileStructure> I remote(java.io.File file) {
        super.remote(file);
        this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                ((FileStructure) value).remote(this.remote.child((String) key));
        });
        return (I) this;
    }

    @Override
    public boolean rename(String name) {
        boolean[] w = {super.rename(name)};

        if (w[0]) this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                w[0] &= ((FileStructure) value).move(this.remote);
        });

        return w[0];
    }

    @Override
    public <I extends IOStructure> I load() {
        for (File file : this.remote.children())
            this.putIfAbsent(FileStructure.class, file.getName(), () -> {
                try {
                    return file.isDirectory() ?
                            this.folder_structure().newInstance() :
                            this.file_structure().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                return null;
            }).remote(file).load();

        return (I) this;
    }

    @Override
    public boolean save() {
        boolean[] w = {this.remote.mkdirs()};

        if (w[0]) this.map().forEach((key, value) -> {
            if (key instanceof String && value instanceof FileStructure)
                w[0] &= ((FileStructure) value).save();
        });

        return w[0];
    }

    @Override
    public <T> T get(Object key) {
        T value = super.get(key);

        if (key instanceof String && value instanceof FileStructure && ((FileStructure) value).remote() == null)
            ((FileStructure) value).remote(this.remote.child((String) key));

        return value;
    }

    @Override
    public <V> V put(Object key, V value) {
        value = super.put(key, value);

        if (key instanceof String && value instanceof FileStructure && ((FileStructure) value).remote() == null)
            ((FileStructure) value).remote(this.remote.child((String) key));

        return value;
    }

    @Override
    public <S extends Structure> S reset() {
        super.reset();
        this.remote(this.remote());
        return (S) this;
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
