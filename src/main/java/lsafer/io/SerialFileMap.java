package lsafer.io;

import java.io.Serializable;
import java.util.Map;

/**
 * A {@link Map} that is linked to {@link File Serial-File} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 7 release (28-Sep-2019)
 * @see java.io.Serializable
 * @since 13-Jul-19
 */
@SuppressWarnings("unused")
public interface SerialFileMap<K, V> extends FileMap<K, V>, Serializable {
	@Override
	default <F extends FileMap> F load() {
		SerialFileMap structure = this.remote().readSerializable(SerialFileMap.class, () -> null);

		if (structure != null)
			//noinspection unchecked
			this.putAll(structure);

		return (F) this;
	}

	@Override
	default boolean save() {
		return this.remote().writeSerializable(this);
	}
}
