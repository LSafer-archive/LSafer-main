package lsafer.util.impl;

import java.io.Serializable;

import lsafer.io.IOMap;
import lsafer.util.Structure;

/**
 * An abstract for structures with the needed methods for the interfaces {@link Structure}, {@link Serializable} and {@link IOMap}.
 *
 * @param <R> type of the remote of the third IO-port container.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 1 release (28-Sep-19)
 * @since 28-Sep-19
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractIOStructure<R, K, V> extends AbstractStructure<K, V> implements IOMap<R, K, V> {
	/**
	 * The 3rd IO-container's remote.
	 */
	protected transient R remote;

	@Override
	public R remote() {
		return this.remote;
	}

	@Override
	public R remote(R remote) {
		R old = this.remote();
		this.remote = remote;
		return old;
	}
}
