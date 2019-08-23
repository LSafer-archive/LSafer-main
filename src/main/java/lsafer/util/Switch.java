package lsafer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LSaferSE
 * @version 1 alpha (07-Aug-19)
 * @since 07-Aug-19
 */
@SuppressWarnings("WeakerAccess")
@Deprecated
final public class Switch<T, R> {

    /**
     *
     */
    final public static Object DEFAULT = new Object();

    /**
     *
     */
    private T object;

    /**
     *
     */
    private List<Consumer<Switch<T, R>>> pointer;

    /**
     *
     */
    private R returns;

    /**
     *
     */
    private Map<T, List<Consumer<Switch<T, R>>>> table = new HashMap<>();

    /**
     * @param object
     */
    public Switch(T object) {
        this.object = object;
    }

    /**
     *
     */
    public Switch<T, R> _break() {
        this.pointer = null;
        return this;
    }

    /**
     *
     */
    public Switch<T, R> _case(T object) {
        this.table.put(object, this.pointer = this.pointer == null ? new ArrayList<>() : this.pointer);
        return this;
    }

    /**
     *
     */
    public Switch<T, R> _default() {
        this.table.put((T) Switch.DEFAULT, this.pointer = this.pointer == null ? new ArrayList<>() : this.pointer);
        return this;
    }

    /**
     * @param consumer
     */
    public Switch<T, R> _do(Consumer<Switch<T, R>> consumer) {
        this.pointer.add(consumer);
        return this;
    }

    /**
     *
     */
    public R _done() {
        List<Consumer<Switch<T, R>>> list = this.table.get(this.object);
        list = list == null ? this.table.getOrDefault((T) DEFAULT, new ArrayList<>()) : list;

        for (Consumer<Switch<T, R>> consumer : list)
            if (this.returns != null)
                break;
            else
                consumer.accept(this);

        return this.returns;
    }

    /**
     *
     */
    public void _return(R object) {
        this.returns = object;
    }


//    public static void main(String...args){
//        System.out.println(
//                new Switch<>("bye")
//                        ._case("asd")
//                        ._do(s -> s._return(1))
//                        ._break()
//                        ._case("bye")
//                        ._do(s -> s._return(0))
//                        ._break()
//                        ._default()
//                        ._do(s -> s._return(231))
//                        ._done()
//        );
//    }

}
