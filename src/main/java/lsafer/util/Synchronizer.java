package lsafer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lsafer.util.impl.AbstractStructure;

/**
 * Used to be the communication method between 2 threads and one of them contains a long loop.
 * Because if a thread entered a long loop it can't be stopped. Unless it have a command
 * that checks if any new instructions have been passed.
 * Also it can pause threads. By making the loop entering an infinite loop
 * that just checks if any new instructions have passed.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 5 release (28-Sep-2019)
 * @since 18 May 2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Synchronizer<K, V> extends AbstractStructure<K, V> implements JetMap<K, V>, HybridMap<K, V> {
	/**
	 * The operations to do after a value get passed.
	 */
	final public transient List<Consumer<? extends Synchronizer>> listeners = new ArrayList<>();

	/**
	 * Loops that linked to this.
	 */
	final public transient List<Loop> loops = new ArrayList<>();

	/**
	 * Call all listeners.
	 */
	public void bind() {
		for (Consumer<? extends Synchronizer> listener : this.listeners)
			((Consumer<Synchronizer>) listener).accept(this);
	}

	/**
	 * Command all loops that have been started by this synchronizer.
	 *
	 * @param position next position for linked loops
	 */
	public void cps(String position) {
		for (Loop loop : this.loops)
			loop.cp(position);

		this.bind();
	}

	/**
	 * Start a loop with this synchronizer as a controller.
	 *
	 * @param loop to be started
	 */
	public void loop(Loop loop) {
		this.loops.add(loop);
		loop.start();
	}
}
