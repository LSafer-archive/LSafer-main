package lsafer.io;

import java.util.HashMap;

import lsafer.json.JSON;

/**
 * structure linked with {@link java.util.Map} as a secondary container
 * and {@link File JSON file} as a third IO container.
 * depends on {@link File#readJSON(java.util.function.Supplier)} (Object)} and {@link File#writeJSON(java.util.Map)}
 * <p>
 * make sure your {@link JSONFileStructure json-file-structure} matches all {@link FileStructure file-structures} rules
 *
 * @author LSaferSE
 * @version 3 release (19-Jub-2019)
 * @see JSON
 * @since 11-Jul-19
 */
public class JSONFileStructure extends FileStructure {

    @Override
    public <I extends IOStructure> I load() {
        this.putAll(this.remote.readJSON(HashMap::new));
        return (I) this;
    }

    @Override
    public boolean save() {
        return this.remote.writeJSON(this.map());
    }

}