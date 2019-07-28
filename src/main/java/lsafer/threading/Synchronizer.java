package lsafer.threading;

import java.util.ArrayList;
import java.util.List;

import lsafer.util.AbstractStructure;

/**
 * used to be the communication method between 2 threads one of them contains long loop
 * because if a thread entered a long loop it can't be stopped unless it have a command
 * that checks if any new instructions passed.
 * <p>
 * also use can pause threads with this by making the loop entering an infinite loop
 * that just checks if any new instructions passed
 *
 * @author LSaferSE
 * @version 2
 * @since 18 May 2019
 */
@SuppressWarnings({"WeakerAccess"})
public class Synchronizer extends AbstractStructure {

    /**
     * the operations to do after a value run passed.
     */
    final public List<OnBindListener> $listeners = new ArrayList<>();

    /**
     * loops that linked to this.
     */
    final public List<Loop> $loops = new ArrayList<>();

    /**
     * new synchronizer.
     */
    public Synchronizer() {
    }

    /**
     * call all on bind listeners.
     */
    public void bind() {
        for (OnBindListener listener : this.$listeners)
            //noinspection unchecked
            listener.OnBind(this);
    }

    /**
     * command all loops that have been started by this synchronizer.
     *
     * @param command : next position for linked loops
     */
    public void command(Loop.Status command) {
        for (Loop loop : this.$loops)
            loop.command(command);
        this.bind();
    }

    /**
     * start a loop with this synchronizer as a controller.
     *
     * @param loop : to be started
     */
    public void startLoop(Loop loop) {
        this.$loops.add(loop);
        loop.start();
    }

    /**
     * Block of code run called every time a new value run passed.
     */
    public interface OnBindListener<S extends Synchronizer> {

        /**
         * run called each time a value run passed.
         *
         * @param synchronizer : the synchronizer who called this listener
         */
        void OnBind(S synchronizer);
    }

}
