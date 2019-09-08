package lsafer.io;

import java.io.Serializable;

/**
 * A structure linked with {@link java.util.Map} as a secondary container. And a {@link File Seril File} as a third IO container.
 * Depends on {@link File#readSerializable(Class, java.util.function.Supplier)} and {@link File#writeSerializable(Serializable)}.
 *
 * <ul>
 * <li>note: make sure your {@link SerialFileStructure serial-file-structure} matches all {@link FileStructure file-structures} rules.</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 3 release (06-Sep-2019)
 * @see java.io.Serializable
 * @since 13-Jul-19
 */
@SuppressWarnings("unused")
public class SerialFileStructure extends FileStructure {
    @Override
    public <F extends FileStructure> F load() {
        this.putAll(this.remote.readSerializable(SerialFileStructure.class, SerialFileStructure::new));
        return (F) this;
    }

    @Override
    public boolean save() {
        return this.remote.writeSerializable(this);
    }
}