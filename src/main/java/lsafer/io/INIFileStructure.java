package lsafer.io;

import java.util.HashMap;

import lsafer.microsoft.INI;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File INI file} as a third IO container.
 * depends on {@link File#readINI(java.util.Map)} and {@link File#writeINI(java.util.Map)}
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
    public <I extends IOStructure> I load() {
        this.putAll(this.$remote.readINI(new HashMap<>()));
        return (I) this;
    }

    @Override
    public boolean save() {
        return this.$remote.writeINI(this.map());
    }

}