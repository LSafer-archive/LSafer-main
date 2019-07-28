package lsafer.threading;

import java.util.List;

/**
 * controllable loop.
 * <p>
 * the concept is to do a block,
 * check shall continue or not,
 * then do the next block and so on.
 *
 * @param <I> the type of the items'll be passed while looping
 * @author LSaferSE
 * @version 3
 * @since 18 May 2019
 */
@SuppressWarnings({"WeakerAccess"})
public abstract class Loop<I> {

    /**
     * The code to loop.
     */
    private Block<I> block;

    /**
     * linking var.
     */
    private volatile boolean check = true;

    /**
     * the position of this loop.
     */
    private Status position = Status.resume;

    /**
     * init this.
     *
     * @param block : the code to loop
     */
    public Loop(Block<I> block) {
        this.block = block;
    }

    /**
     * continue the next step of the loop.
     *
     * @param item : the item to pass it to the next step
     * @return : if continue the loop or not
     */
    final protected boolean next(I item) {
        return this.check() && this.block.next(item);
    }

    /**
     * update the status of loop.
     *
     * @param command new status
     */
    public void command(Status command) {
        this.check = true;
        this.position = command;
    }

    /**
     * made for loop original class
     * to tell the loop what it should do.
     * <p>
     * if the position of the loop is "pause" then it'll enter a loop until any new commands
     *
     * @return : if true the the loop shall continue else shall break
     * @see Loop#next(Object) : it shall call this to decides it's operations
     */
    private boolean check() {
        if (!this.check) return true; //no updates
        this.check = false; //done reading it :)

        switch (this.position) {
            case resume:
                return true;
            case pause:
                //noinspection ALL do nothing until next command
                while (!this.check) ;
                return this.check();  //to read the next command
            case stop:
                return false; //break
            default:
                return false;
        }
    }

    /**
     * the looping cod.
     * call {@link #next(Object)} inside the loop to do the loop
     * if it's returns false , break the loop
     *
     * @see Foreach#start() foreach
     * @see Limited#start() limited
     * @see Forever#start() forever
     */
    protected abstract void start();

    /**
     * the positions of the loops that have been linked to a synchronizer.
     */
    public enum Status {
        /**
         * loops shall continue looping.
         */
        resume,

        /**
         * loops shall pause.
         */
        pause,

        /**
         * loops shall break.
         */
        stop
    }

    /**
     * Block of the code to loop.
     *
     * @param <I> type of the item to handle while looping
     */
    public interface Block<I> {

        /**
         * do next step of the loop.
         *
         * @param item : the item of loop position
         * @return : if the loop shall continue or not
         */
        boolean next(I item);
    }

    /**
     * loop for each item of a list.
     *
     * @param <I> : Items Type
     */
    public static class Foreach<I> extends Loop<I> {

        /**
         * list of items to loop.
         */
        private List<I> list;

        /**
         * init this.
         *
         * @param list  : the items to loop
         * @param block : code to loop
         */
        public Foreach(List<I> list, Block<I> block) {
            super(block);
            this.list = list;
        }

        /**
         * looping foreach item.
         */
        @Override
        protected void start() {
            for (I t : this.list)
                if (!this.next(t))
                    break;
        }
    }

    /**
     * looping until getNext broken manually.
     */
    public static class Forever extends Loop<Integer> {

        /**
         * init this.
         *
         * @param block : the code to loop
         */
        public Forever(Block<Integer> block) {
            super(block);
        }

        /**
         * looping until run broken.
         */
        @Override
        public void start() {
            for (int i = 0; ; i++)
                if (!this.next(i))
                    break;
        }

    }

    /**
     * loop from specific int to specific other int.
     */
    public static class Limited extends Loop<Integer> {

        /**
         * the int to stop before.
         */
        private int before;

        /**
         * the int to start from.
         */
        private int from;

        /**
         * init this.
         * <p>
         * example for lists:
         * mFrom last to first ( size-1 , -1 , ...)
         * mFrom first to last ( 0 , size , ...)
         *
         * @param from   : number to start mFrom
         * @param before : number to stop mBefore
         * @param block  : code to loop
         */
        public Limited(int from, int before, Block<Integer> block) {
            super(block);
            this.from = from;
            this.before = before;
        }

        /**
         * looping from {@link #from} all the way before {@link #before}.
         */
        @Override
        public void start() {
            if (this.from > this.before) {
                for (int i = this.from; i > this.before; i--) //reversed
                    if (!this.next(i))
                        break;
            } else {
                for (int i = this.from; i < this.before; i++) //strait
                    if (!this.next(i))
                        break;
            }
        }
    }

}
