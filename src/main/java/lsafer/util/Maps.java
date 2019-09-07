package lsafer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Useful methods for Maps.
 *
 * @author LSafer
 * @version 4 release (06-Sep-2019)
 * @since 11 Jun 2019
 */
final public class Maps {
    /**
     * This is a util class. and shall not be instanced as an object.
     */
    private Maps() {

    }

    /**
     * Get all keys that equals the given value.
     *
     * @param map   to search at
     * @param value to find
     * @param <E>   type of keys
     * @param <V>   type of value
     * @return all the keys that equals the given value
     */
    public static <E, V> List<E> keys(Map<E, V> map, V value) {
        List<E> keys = new ArrayList<>();

        map.forEach((key, val) -> {
            if (val.equals(value))
                keys.add(key);
        });

        return keys;
    }

    /**
     * Check if the given map matches a specific conditions.
     * With the given include map and not matches the conditions.
     * With the given exclude map.
     *
     * @param map     to check
     * @param include map to be matched with the given map
     * @param exclude map to be matched reversely with the given map
     * @param filters (or conditions) to apply
     * @param <K>     maps key type
     * @param <V>     maps value type
     * @return if the map matches include map and not matches exclude map
     */
    @SafeVarargs
    public static <K, V> boolean matches(Map<K, V> map, Map<K, V> include, Map<K, V> exclude, BiFunction<V, V, Boolean>... filters) {
        boolean[] result = {true};

        if (include != null)
            include.forEach((key, value) -> {
                if (result[0]) {
                    V Value = map.get(key);

                    for (BiFunction<V, V, Boolean> filter : filters)
                        try {
                            result[0] = filter.apply(Value, value);
                            return;
                        } catch (ClassCastException ignored) {
                        }
                }
            });

        if (exclude != null)
            exclude.forEach((key, value) -> {
                if (result[0]) {
                    V Value = map.get(key);

                    for (BiFunction<V, V, Boolean> filter : filters)
                        try {
                            result[0] = !filter.apply(Value, value);
                            return;
                        } catch (ClassCastException ignored) {
                        }
                }
            });

        return result[0];
    }
}
