package lsafer.threading;

import lsafer.util.HashStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Used to be the communication method between 2 threads and one of them contains a long loop.
 * Because if a thread entered a long loop it can't be stopped. Unless it have a command
 * that checks if any new instructions have been passed.
 * Also it can pause threads. By making the loop entering an infinite loop
 * that just checks if any new instructions have passed.
 *
 * @author LSaferSE
 * @version 3 release (06-Sep-2019)
 * @since 18 May 2019
 */
@SuppressWarnings({"WeakerAccess"})
public class Synchronizer extends HashStructure {
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
     * @param command next position for linked loops
     */
    public void command(Loop.Status command) {
        for (Loop loop : this.loops)
            loop.command(command);

        this.bind();
    }

    /**
     * Start a loop with this synchronizer as a controller.
     *
     * @param loop to be started
     */
    public void startLoop(Loop loop) {
        this.loops.add(loop);
        loop.start();
    }
}
