package lsafer.io;

import lsafer.microsoft.INI;

import java.util.HashMap;

/**
 * A structure linked with {@link java.util.Map} as a secondary container. And an {@link File INI file} as a third IO container.
 * Depends on {@link File#readINI(java.util.function.Supplier)} and {@link File#writeINI(java.util.Map)}.
 *
 * <ul>
 *     <li>note: make sure your {@link INIFileStructure ini-file-structure} matches all {@link FileStructure file-structures} rules.</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 4 release (06-Sep-2019)
 * @see INI
 * @since 11-Jul-19
 */
public class INIFileStructure extends FileStructure {

    @Override
    public <F extends FileStructure> F load() {
        //noinspection RedundantTypeArguments
        this.putAll(this.remote.<Object>readINI(HashMap::new));
        return (F) this;
    }

    @Override
    public boolean save() {
        return this.remote.writeINI(this.map());
    }

}