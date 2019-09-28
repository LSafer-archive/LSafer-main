package lsafer.io;

import java.util.Map;

import lsafer.util.Caster;

/**
 * A {@link Map} that is linked to {@link File} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 12 release (28-Sep-2019)
 * @since 11 Jun 2019
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@IOMap.Configurations(remote = File.class)
public interface FileMap<K, V> extends IOMap<File, K, V>, Caster.User {
	@Override
	default File xremote(Object remote) {
		File old = this.remote();
		this.remote(this.caster().cast(File.class, remote));
		return old;
	}

	/**
	 * Delete the linked {@link File}.
	 *
	 * @return success of deleting
	 * @see File#delete()
	 */
	default boolean delete() {
		return this.remote().delete();
	}

	/**
	 * Check if the linked {@link File} is available or not.
	 *
	 * @return whether the linked file is available or not
	 */
	default boolean exist() {
		File remote = this.remote();
		return remote.exists() && !remote.isDirectory();
	}

	/**
	 * Move the linked {@link File} to the given file.
	 *
	 * @param parent to move to
	 * @return success of moving
	 */
	default boolean move(java.io.File parent) {
		File remote = this.remote();
		boolean w = remote.move(parent);
		this.remote(remote.self);
		return w;
	}

	/**
	 * Move the linked {@link File} to the given file.
	 *
	 * @param parent to move to
	 * @return success of moving
	 */
	default boolean move(String parent) {
		return this.move(new File(parent));
	}

	/**
	 * Rename the linked {@link File} to the given name.
	 *
	 * @param name to rename to
	 * @return success of the renaming
	 */
	default boolean rename(String name) {
		File remote = this.remote();
		boolean w = remote.rename(name);
		this.remote(remote.self);
		return w;
	}

	/**
	 * Load this from the linked {@link File}.
	 *
	 * @param <F> this
	 * @return this
	 */
	<F extends FileMap> F load();

	/**
	 * Save this to the linked {@link File}.
	 *
	 * @return success of saving
	 */
	boolean save();
}
