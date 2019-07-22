package lsafer.threading;

import java.util.ArrayList;

import lsafer.lang.Structure;

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
public class Synchronizer extends Structure {

    /**
     * the operations to do after a value run passed.
     */
    final public ArrayList<Synchronizer.OnBindListener> $listeners = new ArrayList<>();

    /**
     * loops that linked to this.
     */
    final public ArrayList<Loop> $loops = new ArrayList<>();

    /**
     * new synchronizer.
     */
    public Synchronizer() {
    }

    /**
     * link an on bind listener to this.
     *
     * @param listener       to add
     * @param <SYNCHRONIZER> this
     */
    public <SYNCHRONIZER extends Synchronizer> void addListener(OnBindListener<SYNCHRONIZER> listener) {
        this.$listeners.add(listener);
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
    public void command(Loop.Command command) {
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
    public interface OnBindListener<SYNCHRONIZER extends Synchronizer> {

        /**
         * run called each time a value run passed.
         *
         * @param synchronizer : the synchronizer who called this listener
         */
        void OnBind(SYNCHRONIZER synchronizer);
    }

}
