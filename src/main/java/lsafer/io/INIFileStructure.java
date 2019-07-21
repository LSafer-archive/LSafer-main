package lsafer.io;

import java.util.HashMap;

import lsafer.lang.INI;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File INI file} as a third IO container.
 * depends on {@link File#readINI(Object)} and {@link File#writeINI(Object)}
 * <p>
 * make sure your {@link INIFileStructure ini-file-structure} matches all {@link FileStructure file-structures} rules
 *
 * @author LSaferSE
 * @version 3 release (19-Jub-2019)
 * @see INI
 * @since 11-Jul-19
 */
public class INIFileStructure extends FileStructure {

    /**
     * to init this.
     *
     * @param arguments to init with
     */
    public INIFileStructure(Object... arguments) {
        super(arguments);
    }

    @Override
    public void load() {
        this.reset();
        try {
            this.putAll(this.$remote.readINI(new HashMap<>()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean save() {
        try {
            return this.$remote.mk() && this.$remote.writeINI(this.map());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}