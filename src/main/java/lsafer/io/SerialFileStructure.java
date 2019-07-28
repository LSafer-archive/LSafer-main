package lsafer.io;

import java.io.Serializable;
import java.util.HashMap;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File Serilizable file} as a third IO container.
 * depends on {@link File#readSerial(Class, Serializable)} and {@link File#writeSerial(Serializable)}
 * <p>
 * make sure your {@link SerialFileStructure serial-file-structure} matches all {@link FileStructure file-structures} rules
 *
 * @author LSaferSE
 * @version 2 release (19-Jul-2019)
 * @see java.io.Serializable
 * @since 13-Jul-19
 */
public class SerialFileStructure extends FileStructure {

    /**
     * init this.
     *
     * @param arguments to init with
     */
    public SerialFileStructure(Object... arguments) {
        super(arguments);
    }

    @Override
    public void load() {
        this.reset();
        this.putAll(this.$remote.readSerial(HashMap.class, new HashMap<>()));
    }

    @Override
    public boolean save() {
        return this.$remote.writeSerial((HashMap) this.map());
    }

}