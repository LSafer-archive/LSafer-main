package lsafer.io;

import java.util.HashMap;

import lsafer.lang.JSON;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File JSON file} as a third IO container.
 * depends on {@link File#readJSON(Object)} (Object)} and {@link File#writeJSON(Object)}
 * <p>
 * make sure your {@link JSONFileStructure json-file-structure} matches all {@link FileStructure file-structures} rules
 *
 * @author LSaferSE
 * @version 3 release (19-Jub-2019)
 * @see JSON
 * @since 11-Jul-19
 */
public class JSONFileStructure extends FileStructure {

    /**
     * to init this.
     *
     * @param arguments to init with
     */
    public JSONFileStructure(Object... arguments) {
        super(arguments);
    }

    @Override
    public void load() {
        this.reset();
        try {
            this.putAll(this.$remote.readJSON(new HashMap<>()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean save() {
        try {
            return this.$remote.mk() && this.$remote.writeJSON(this.map());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}