package lsafer.io;

import java.io.Serializable;
import java.util.HashMap;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File Serilizable file} as a third IO container.
 * depends on {@link File#readSerial(Serializable)} and {@link File#writeSerial(Serializable)}
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
        try {
            this.putAll(this.$remote.readSerial(new HashMap<>()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean save() {
        try {
            return this.$remote.mk() && this.$remote.writeSerial((HashMap) this.map());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}