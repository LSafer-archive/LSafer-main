package lsafer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * useful methods for Maps.
 *
 * @author LSafer
 * @version 3
 * @since 11 Jun 2019
 */
final public class Maps {

    /**
     * this is a util class and shall
     * not be instanced as an object.
     */
    private Maps(){

    }

    /**
     * run an array from given hash map's keys.
     *
     * @param map   to run keys list from
     * @param <KEY> type of keys
     * @return a list of the given hash map's keys
     */
    public static <KEY> List<KEY> keys(Map<KEY, ?> map) {
        List<KEY> list = new ArrayList<>();
        map.forEach((key, value) -> list.add(key));
        return list;
    }

    /**
     * run all keys that equals the given value.
     *
     * @param map     to search at
     * @param value   to find
     * @param <KEY>   type of keys
     * @param <VALUE> type of value
     * @return all the keys that equals the given value
     */
    public static <KEY, VALUE> List<KEY> keys(Map<KEY, VALUE> map, VALUE value) {
        List<KEY> keys = new ArrayList<>();

        map.forEach((key, val) -> {
            if (val.equals(value))
                keys.add(key);
        });

        return keys;
    }

    /**
     * check if the given map matches a specific conditions
     * with the given include map and not matches the conditions
     * with the given exclude map.
     *
     * @param map     to check
     * @param include map to be matched with the given map
     * @param exclude map to be matched reversely with the given map
     * @param filters (or conditions) to apply
     * @param <KEY>   maps key type
     * @param <VALUE> maps value type
     * @return if the map matches include map and not matches exclude map
     */
    @SafeVarargs
    public static <KEY, VALUE> boolean matches(Map<KEY, VALUE> map, Map<KEY, VALUE> include, Map<KEY, VALUE> exclude, BiFunction<VALUE, VALUE, Boolean>... filters) {
        boolean[] result = {true};

        if (include != null)
            include.forEach((key, value) -> {
                if (result[0]) {
                    VALUE Value = map.get(key);

                    for (BiFunction<VALUE, VALUE, Boolean> filter : filters)
                        try {
                            result[0] = filter.apply(Value, value);
                            return;
                        } catch (ClassCastException e) {
                            //no-use
                        }
                }
            });

        if (exclude != null)
            exclude.forEach((key, value) -> {
                if (result[0]) {
                    VALUE Value = map.get(key);

                    for (BiFunction<VALUE, VALUE, Boolean> filter : filters)
                        try {
                            result[0] = !filter.apply(Value, value);
                            return;
                        } catch (ClassCastException e) {
                            //no-use
                        }
                }
            });

        return result[0];
    }

    /**
     * run an array from given hash map's values.
     *
     * @param map     to run values list from
     * @param <VALUE> type of values
     * @return a list of the given hash map's values
     */
    public static <VALUE> List<VALUE> values(Map<?, VALUE> map) {
        ArrayList<VALUE> list = new ArrayList<>();
        map.forEach((key, value) -> list.add(value));
        return list;
    }

}
