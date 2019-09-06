package lsafer.io;

import java.lang.annotation.*;

import static lsafer.io.FolderStructure.Defaults;

/**
 * A structure linked with {@link java.util.Map} as a secondary container. And a {@link File Folder} as a third IO container.
 *
 * <ul>
 * <li>rule: for file-structure fields. no null default values.</li>
 * <li>note: make sure your {@link FolderStructure folder-structure} matches all {@link FileStructure file-structures} rules.</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 3 release (06-Sep-19)
 * @since 19-Jul-19
 */
@Defaults
public class FolderStructure extends FileStructure {

    @Override
    public boolean exist() {
        return this.remote.exists() && this.remote.isDirectory();
    }

    @Override
    public <T> T get(Object key) {
        T value = super.get(key);

        if (key instanceof String && value instanceof FileStructure && ((FileStructure) value).remote() == null)
            ((FileStructure) value).remote(this.remote.child((String) key));

        return value;
    }

    @Override
    public <F extends FileStructure> F load() {
        for (File file : this.remote.children()) {
            FileStructure structure = this.putIfAbsent(FileStructure.class, file.getName(), () -> {
                try {
                    return file.isDirectory() ?
                           this.getClass().getAnnotation(Defaults.class).folder().newInstance() :
                           this.getClass().getAnnotation(Defaults.class).file().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                return null;
            });

            if (structure != null)
                structure.remote((java.io.File) file).load();
        }

        return (F) this;
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
    public <V> V put(Object key, V value) {
        value = super.put(key, value);

        if (key instanceof String && value instanceof FileStructure && ((FileStructure) value).remote() == null)
            ((FileStructure) value).remote(this.remote.child((String) key));

        return value;
    }

    @Override
    public <I extends IOStructure> I remote(File file) {
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
    public <S extends lsafer.util.Structure> S reset() {
        super.reset();
        this.remote(this.remote());
        return (S) this;
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

    /**
     * Set the default values for the targeted folder-structure.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface Defaults {
        /**
         * The default file-structure to initialize. for files found but no matching fields for them.
         *
         * @return default file-structure class
         */
        Class<? extends FileStructure> file() default JSONFileStructure.class;

        /**
         * The default folder-structure to initialize. for folders found but no matching fields for them.
         *
         * @return default folder-structure class
         */
        Class<? extends FolderStructure> folder() default FolderStructure.class;
    }

}
