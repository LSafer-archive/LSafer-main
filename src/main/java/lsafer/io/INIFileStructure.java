package lsafer.io;

import java.util.HashMap;
import java.util.Map;

import lsafer.microsoft.INI;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File INI file} as a third IO container.
 * depends on {@link File#readINI(java.util.function.Supplier)} and {@link File#writeINI(java.util.Map)}
 * <p>
 * make sure your {@link INIFileStructure ini-file-structure} matches all {@link FileStructure file-structures} rules
 *
 * @author LSaferSE
 * @version 3 release (19-Jub-2019)
 * @see INI
 * @since 11-Jul-19
 */
public class INIFileStructure extends FileStructure {

    @Override
    public <I extends IOStructure> I load() {
        //noinspection RedundantTypeArguments
        this.putAll(this.remote.<Object>readINI(HashMap::new));
        return (I) this;
    }

    @Override
    public boolean save() {
        return this.remote.writeINI(this.map());
    }

}