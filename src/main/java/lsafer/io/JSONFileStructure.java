package lsafer.io;

import lsafer.json.JSON;

import java.util.HashMap;

/**
 * A structure linked with {@link java.util.Map} as a secondary container. And a {@link File JSON file} as a third IO container.
 * Depends on {@link File#readJSON(java.util.function.Supplier)} (Object)} and {@link File#writeJSON(java.util.Map)}.
 *
 * <ul>
 * <li>note: make sure your {@link JSONFileStructure json-file-structure} matches all {@link FileStructure file-structures} rules.</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 4 release (06-Sep-2019)
 * @see JSON
 * @since 11-Jul-19
 */
public class JSONFileStructure extends FileStructure {
    @Override
    public <F extends FileStructure> F load() {
        this.putAll(this.remote.readJSON(HashMap::new));
        return (F) this;
    }

    @Override
    public boolean save() {
        return this.remote.writeJSON(this.map());
    }
}
