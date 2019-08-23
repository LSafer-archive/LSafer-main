package lsafer.util;

import java.io.File;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * defines that the targeted class can be used as a structure,
 * basically structure means that the class can be a map
 * and every public field of it is a map node,
 * the field name is the node key, and it's value is the node value.
 *
 * <p>
 * field rules:
 * <ul>
 * <li>all public fields will be a node (even final and static fields)[all but fields excluded in this list]</li>
 * <li>private field well not be a node</li>
 * <li>protected fields sometimes well not be a node (but sometimes well be by mistake , so please add '$' char in their name to make sure it's excluded)</li>
 * <li>java non-object fields (like int, float, etc...) may cause casting problems</li>
 * <li>declaring fields in the constructor is an unprotect way</li>
 * <li>if the field is transient then it'll be ignored by the structure</li>
 * <li>you can {@link #put(Object, Object) put} some objects with a type different than the targeted field's type, see {@link #cast(Class, Object)} for more info</li>
 * <li>the only way to declare a default value is to declare it directly to the field (or on it's constructor) , other ways may not work , so be careful while using nested structures</li>
 * </ul>
 *
 * <p>
 * to use any of these methods with your structure :
 * <ul>
 * <li>{@link #cast(Class, Object) castObject(YourClass, Object)}</li>
 * <li>{@link #clone(Class) clone(YourClass)}</li>
 * <li>{@link #reset() YourClass.reset()}</li>
 * </ul>
 * your structure should match this rules :
 * <ul>
 * <li>your Structure must be public static.</li>
 * <li>
 * your Structure must have at least one of :
 * <ul>
 * <li>constructor public ()</li>
 * <li>constructor public (Object...)</li>
 * </ul>
 * </ul>
 *
 * <p>
 * tips :
 * <ul>
 * <li>add a secondary container like a {@link Map} to your structure so you can store keys even if the class don't have a {@link Field field} matches it.</li>
 * </ul>
 *
 * <p>
 * methods to Override :
 * <ul>
 * <li>{@link #clean()} clear all secondary containers only (do super first)</li>
 * <li>{@link #clear()} clear all containers (do super first)</li>
 * <li>{@link #clone(Class)} to clone hidden fields (do super first)</li>
 * <li>{@link #containsKey(Object)} to check secondary containers (do super last)</li>
 * <li>{@link #containsValue(Object)} to check secondary containers (do super last)</li>
 * <li>{@link #get(Object)} to get the value from the secondary containers if not found in a field (do super first)</li>
 * <li>{@link #map()} to get a map from this and the secondary containers combined (do super last)</li>
 * <li>{@link #put(Object, Object)} to put the value in the secondary container (do super first)</li>
 * <li>{@link #remove(Object)} to remove the value from the secondary container (do super first)</li>
 * <li>{@link #reset()} to reset the secondary container too (do super last)</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 7 release (7-Aug-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings({"UnusedReturnValue"})
//TODO
//  -methods weight improvements
//  -class-structure2sub-class-structure duel synchronization
public interface Structure extends Serializable {

    /**
     * <b>UTIL</b>
     * <p>
     * cast the given object to the given klass
     * used to cast objects that we have to cast it manually.
     *
     * <p>----------</p>
     * <p>
     * supported casts (else well return null):
     * <ul>
     * <li>(value equals null)</li>
     * <li>(value instanceOf klass)</li>
     * <li>(klass subClassOf {@link Structure}) and (value instanceOf {@link Structure})</li>
     * <li>(klass subClassOf {@link Structure}) and (value instanceOf {@link Map})</li>
     * <li>(klass equals {@link Map}) and (value instanceOf {@link Structure})</li>
     * <li>(klass.component subClassOf {@link Structure}) and (value instanceOf {@link Map[]})</li>
     * <li>(klass.component equals {@link Map}) and (value instanceOf {@link Structure[]})</li>
     * <li>(klass subClassOf {@link Number}) and (value instanceOf {@link Number} or {@link String})</li>
     * <li>(klass subClassOf {@link List}) and (value instanceOf {@link Object[]})</li>
     * <li>(klass subClassOf {@link Object[]}) and (value instanceOf {@link List})</li>
     * </ul>
     *
     * @param klass to cast to
     * @param value to cast
     * @param <T>   type of the class to cast to
     * @return value casted to the given class
     * @see #putAll(Map) used to fill the structable (case the structuable is assignable from the klass and the value is a map)
     */
    default <T> T cast(Class<T> klass, Object value) {
        if (value == null || klass.isInstance(value)) {
            return (T) value;
        }

        if (String.class == klass) {
            return (T) String.valueOf(value);
        }
        if (Number.class.isAssignableFrom(klass) && (value instanceof Number || value instanceof String)) {
            try {
                return (T) klass.getMethod("valueOf", String.class)
                        .invoke(null, klass == Integer.class || klass == Long.class ?
                                String.valueOf(value).split("[.]")[0] : String.valueOf(value));
            } catch (Exception ignored) {
                return null;
            }
        }
        if (File.class.isAssignableFrom(klass) && value instanceof String) {
            return (T) new File(String.valueOf(value));
        }

        if (ArrayStructure.class.isAssignableFrom(klass) && value instanceof Object[]) {
            try {
                ArrayStructure structure = (ArrayStructure) klass.newInstance();
                structure.putAll((Object[]) value);
                return (T) structure;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ArrayStructure.class.isAssignableFrom(klass) && value instanceof List) {
            try {
                ArrayStructure structure = (ArrayStructure) klass.newInstance();
                structure.putAll((List) value);
                return (T) structure;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ArrayStructure[].class.isAssignableFrom(klass) && value instanceof Object[][]) {
            Object[][] arrays = (Object[][]) value;
            ArrayStructure[] structures = (ArrayStructure[]) Array.newInstance(klass.getComponentType(), arrays.length);

            for (int i = 0; i < arrays.length; i++) {
                try {
                    structures[i] = (ArrayStructure) klass.newInstance();
                    structures[i].putAll(arrays[i]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }

            return (T) structures;
        }
        if (ArrayStructure[].class.isAssignableFrom(klass) && value instanceof List[]) {
            List[] lists = (List[]) value;
            ArrayStructure[] structures = (ArrayStructure[]) Array.newInstance(klass.getComponentType(), lists.length);

            for (int i = 0; i < lists.length; i++) {
                try {
                    structures[i] = (ArrayStructure) klass.newInstance();
                    structures[i].putAll(lists[i]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }

            return (T) structures;
        }

        if (Structure.class.isAssignableFrom(klass) && value instanceof Structure) {
            return (T) ((Structure) value).clone((Class<? extends Structure>) klass);
        }
        if (Structure.class.isAssignableFrom(klass) && value instanceof Map) {
            try {
                Structure structure = (Structure) klass.newInstance();
                structure.putAll((Map<String, Object>) value);
                return (T) structure;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Structure[].class.isAssignableFrom(klass) && value instanceof Map[]) {
            Map[] maps = (Map[]) value;
            Structure[] structures = (Structure[]) Array.newInstance(klass.getComponentType(), maps.length);

            for (int i = 0; i < maps.length; i++) {
                try {
                    structures[i] = (Structure) klass.newInstance();
                    structures[i].putAll(maps[i]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }

            return (T) structures;
        }

        if (Map.class == klass && value instanceof Structure) {
            return (T) ((Structure) value).map();
        }
        if (Map[].class.isAssignableFrom(klass) && value instanceof Structure[]) {
            Structure[] structures = (Structure[]) value;
            Map[] maps = (Map[]) Array.newInstance(klass.getComponentType(), structures.length);

            for (int i = 0; i < structures.length; i++) {
                maps[i] = structures[i].map();
            }

            return (T) maps;
        }

        if (List.class == klass && value instanceof Object[]) {
            return (T) Arrays.asList((Object[]) value);
        }
        if (List.class == klass && value instanceof ArrayStructure) {
            return (T) ((ArrayStructure) value).list();
        }
        if (List[].class.isAssignableFrom(klass) && value instanceof ArrayStructure[]) {
            ArrayStructure[] structures = (ArrayStructure[]) value;
            List[] lists = (List[]) Array.newInstance(klass.getComponentType(), structures.length);

            for (int i = 0; i < structures.length; i++) {
                lists[i] = structures[i].list();
            }

            return (T) lists;
        }
        if (Object[][].class.isAssignableFrom(klass) && value instanceof ArrayStructure[]) {
            ArrayStructure[] structures = (ArrayStructure[]) value;
            Object[][] arrays = (Object[][]) Array.newInstance(klass.getComponentType(), structures.length);

            for (int i = 0; i < structures.length; i++) {
                arrays[i] = Arrays.generify(structures[i].array());
            }

            return (T) arrays;
        }

        return null;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * remove any key that this dose not
     * have a field with the same name
     * of it.
     *
     * @param <S> type of this
     * @return this
     */
    default <S extends Structure> S clean() {
        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * remove all nodes from this
     * and set all fields to null.
     *
     * @param <S> type of this
     * @return this
     */
    default <S extends Structure> S clear() {
        for (Field field : this.getClass().getFields())
            if (this.structured(field))
                try {
                    field.setAccessible(true);
                    field.set(this, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        return (S) this;
    }

    /**
     * <b>UTIL</b>
     * <p>
     * copy this structure as other structure class.
     *
     * @param klass to copy to
     * @param <S>   type of the class
     * @return new instance with same values of this but different class
     * @see #putAll(Structure) used to put all of this into the new instance
     */
    default <S extends Structure> S clone(Class<S> klass) {
        try {
            S structure = (S) (klass == null ? this.getClass() : klass).newInstance();
            structure.putAll(this);
            return structure;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * checks if this class contains a field
     * matches the given name and it's type
     * is assignable from the given type.
     *
     * @param name of the field to check
     * @param type of the field to check
     * @return whether this class contains a field with the given name and matches the given value or not
     */
    default boolean containsField(String name, Class<? /*super FIELD_TYPE*/> type) {
        try {
            Field field = this.getClass().getField(name);

            return this.structured(field) && type.isAssignableFrom(field.getType());
        } catch (NoSuchFieldException ignored) {
        }

        return false;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * check if this contains the given key and it didn't
     * mapped to null.
     *
     * @param key to find
     * @return whether this contains the given key and not mapped to null
     */
    default boolean containsKey(Object key) {
        if (key instanceof String)
            try {
                return this.structured(this.getClass().getField((String) key));
            } catch (NoSuchFieldException ignored) {
            }

        return false;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * check if any key in this is mapped to the given value.
     *
     * @param value to find
     * @return whether the given value is mapped with any key in this
     */
    default boolean containsValue(Object value) {
        for (Field field : this.getClass().getFields())
            if (this.structured(field))
                try {
                    field.setAccessible(true);
                    if (field.get(this).equals(value))
                        return true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return false;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * do a function foreach element in this.
     *
     * @param consumer function to apply
     * @param <S>      this
     * @return this
     * @see Map#forEach(BiConsumer)
     */
    //TODO heavy weight, uses this.map()
    default <S extends Structure> S forEach(BiConsumer<?, ?> consumer) {
        this.map().forEach((BiConsumer<? super Object, ? super Object>) consumer);
        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * run a value mapped to the given key.
     * <p>
     * fyi : if the passed key == null then the return value well be this
     *
     * @param key to run it's mapped value
     * @param <T> type of value
     * @return the mapped value to the given key
     */
    default <T> T get(Object key) {
        if (key == null)
            return (T) this;

        if (key instanceof String)
            try {
                Field field = this.getClass().getField((String) key);

                if (this.structured(field)) {
                    field.setAccessible(true);
                    return (T) field.get(this);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return null;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * run a value mapped to the given key
     * and returns the given default value
     * case the key didn't exist or mapped
     * to null.
     *
     * @param klass to make sure the mapped value is instance of the needed class
     * @param key   to run it's mapped value
     * @param <T>   type of value
     * @return the mapped value to the given key
     * @see #get(Object) to run the value
     *
     */
    default <T> T get(Class<? super T> klass, Object key) {
        return (T) this.cast(klass, this.get(key));
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * run a value mapped to the given key
     * and returns the given default value
     * case the key didn't exist or mapped
     * to null.
     *
     * @param key          to run it's mapped value
     * @param defaultValue case key not found
     * @param <T>          type of value
     * @return the mapped value to the given key
     * @see #get(Object) to run the value
     */
    default <T> T get(Object key, Supplier<T> defaultValue) {
        T value = this.get(key);
        return value == null ? defaultValue.get() : value;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * run a value mapped to the given key
     * and returns the given default value
     * case the key didn't exist or mapped
     * to null.
     *
     * @param klass        to make sure the mapped value is instance of the needed class
     * @param key          to run it's mapped value
     * @param defaultValue case key not found
     * @param <T>          type of value
     * @return the mapped value to the given key
     * @see #get(Object) to run the value
     */
    default <T> T get(Class<? super T> klass, Object key, Supplier<T> defaultValue) {
        T value = (T) this.cast(klass, this.get(key));
        return value == null ? defaultValue.get() : value;
    }

    /**
     * <b>BASE</b>
     * <p>
     * Returns a Set view of the keys contained in this map.
     * The set is backed by the map,
     * so changes to the map are reflected in the set,
     * and vice-versa.
     * If the map is modified while an iteration over the set is
     * in progress (except through the iterator's own remove operation),
     * the results of the iteration are undefined.
     * The set supports element removal,
     * which removes the corresponding mapping from the map,
     * via the Iterator.remove,
     * Set.remove,
     * removeAll,
     * retainAll,
     * and clear operations.
     * It does not support the add or addAll operations.
     *
     * @return a set view of the keys contained in this map
     */
    default Set<Object> keySet() {
        Set<Object> set = new HashSet<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field))
                set.add(field.getName());

        return set;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * run this as a map.
     *
     * @return this as a map
     */
    default Map<Object, Object> map() {
        Map<Object, Object> map = new HashMap<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field) && !map.containsKey(field.getName()))
                try {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return map;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * override the fields inside this from fields inside a super class of this.
     *
     * @param klass to get fields from
     * @param <S>   this
     * @return this
     */
    default <S extends Structure> S overrideFromSuper(Class klass) {
        Class klass1 = klass == null ? this.getClass().getSuperclass() : klass;
        List<String> overridden = new ArrayList<>();

        for (Field field : klass1.getFields())
            if (this.structured(field) && !overridden.contains(field.getName()))
                try {
                    field.setAccessible(true);
                    this.put(field.getName(), field.get(this));
                    overridden.add(field.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * override fields in a super class of this from the fields inside this.
     *
     * @param klass to override fields inside
     * @param <S>   this
     * @return this
     */
    default <S extends Structure> S overrideSuper(Class klass) {
        Class klass1 = klass == null ? this.getClass().getSuperclass() : klass;

        if (klass1.isInstance(this))
            this.map().forEach((key, value) -> {
                if (key instanceof String)
                    try {
                        Field field = klass1.getField((String) key);

                        if (this.structured(field))

                            field.setAccessible(true);
                        field.set(this, this.cast(field.getType(), value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException ignored) {
                    }
            });
        else throw new IllegalStateException(this.getClass() + " isn't an instance of " + klass1);
        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * map the given value to the given key.
     * <p>
     * this method will try to cast the given
     * value to match the field of the given key
     * and will return the casted value
     * <p>
     *
     * @param key   to map to
     * @param value to map
     * @param <V>   type of the value
     * @return the actual value that have been added
     * and null if the passed value is null
     * @see #cast(Class, Object) used to cast values to field's type
     */
    default <V> V put(Object key, V value) {
        if (key instanceof String)
            try {
                Field field = this.getClass().getField((String) key);

                if (this.structured(field)) {
                    field.setAccessible(true);

                    Object value1 = this.cast(field.getType(), value);
                    field.set(this, value1);
                    return (V) value1;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return value;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * copy all (key-value) links from the given
     * structure to this.
     *
     * @param structure to copy from
     * @param <S>       type of this
     * @return this
     * @see #map() used to run structure's map
     * @see #putAll(Map) used to putAll the structure's ma
     */
    default <S extends Structure> S putAll(Structure structure) {
        this.putAll(structure.map());
        return (S) this;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * copy all (key-value) links from the given
     * map to this.
     *
     * @param map to copy from
     * @param <S> type of this
     * @return this
     * @see #put(Object, Object) used to put foreach value
     */
    default <S extends Structure> S putAll(Map<?, ?> map) {
        map.forEach(this::put);
        return (S) this;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * map the given key to the given value only if
     * the given key isn't mapped or mapped to null.
     *
     * @param key   to map
     * @param value to generate value if the given key is already have been mapped
     * @param <V>   type of the value
     * @return the actual value that have been added or the mapped value if the key have already mapped
     */
    default <V> V putIfAbsent(Object key, Supplier<V> value) {
        V mapped = this.get(key);
        return this.put(key, mapped == null ? value.get() : mapped);
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * map the given key to the given value only if
     * the given key isn't mapped or mapped to null.
     *
     * @param klass the class of the value
     * @param key   to map
     * @param value to generate value if the given key is already have been mapped
     * @param <V>   type of the value
     * @return the actual value that have been added or the mapped value if the key have already mapped
     */
    default <V> V putIfAbsent(Class<? super V> klass, Object key, Supplier<V> value) {
        V mapped = this.get(klass, key);
        return this.put(key, mapped == null ? value.get() : mapped);
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * unmap the given key from it's value.
     *
     * @param key to unmap
     */
    default void remove(Object key) {
        if (key instanceof String)
            try {
                Field field = this.getClass().getField((String) key);

                if (this.structured(field)) {
                    field.setAccessible(true);
                    field.set(this, null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * unmap the given key from it's value only if it's
     * value {@link Object#equals(Object) equals} the given key.
     *
     * @param key   to unmap
     * @param value to remove if it's equals the value mapped to the given key
     */
    default void remove(Object key, Object value) {
        if (this.get(key).equals(value))
            this.remove(key);
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * remove all nodes found in the given map from this.
     *
     * @param map to remove
     * @param <S> type of this
     * @return this
     */
    default <S extends Structure> S removeAll(Map<?, ?> map) {
        map.forEach(this::remove);
        return (S) this;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * remove all nodes found in the given structure from this.
     *
     * @param structure to remove
     * @param <S>       type of this
     * @return this
     */
    default <S extends Structure> S removeAll(Structure structure) {
        return this.removeAll(structure.map());
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * apply the given function foreach Key-Value in this
     * and if the function reruns true then it'll be removed.
     *
     * @param function to use
     * @param <S>      this
     * @return this
     */
    //TODO heavy weight, uses this.forEach() witch uses this.map()
    default <S extends Structure> S removeIf(BiFunction<?, ?, Boolean> function) {
        this.forEach((k, v) -> {
            if (((BiFunction<Object, Object, Boolean>) function).apply(k, v))
                this.remove(k);
        });

        return (S) this;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * replace a value in this.
     *
     * @param klass        of the value
     * @param key          were the value have been mapped at
     * @param defaultValue in case the mapped value is null
     * @param replacement  function to get the replacement from
     * @param <V>          value type
     * @return old value
     */
    //TODO too many arguments
    default <V> V replace(Class<V> klass, Object key, Supplier<V> defaultValue, Function<V, V> replacement) {
        V o = this.get(klass, key);
        this.put(key, replacement.apply(o == null ? defaultValue.get() : o));
        return o;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * replace a value in this.
     *
     * @param key          were the value have been mapped at
     * @param defaultValue in case the mapped value is null
     * @param replacement  function to get the replacement from
     * @param <V>          value type
     * @return old value
     */
    //TODO too many arguments
    default <V> V replace(Object key, Supplier<V> defaultValue, Function<V, V> replacement) {
        V o = this.get(key);
        this.put(key, replacement.apply(o == null ? defaultValue.get() : o));
        return o;
    }

    /**
     * <b>OVERLOAD UTIL</b>
     * <p>
     * replace foreach element in this.
     *
     * @param function to apply (replace) foreach node in this &lt;Key, Value, Return&gt;
     * @param <S>      this
     * @return this
     */
    //TODO heavy weight, uses this.map()
    default <S extends Structure> S replaceAll(BiFunction<?, ?, ?> function) {
        this.map().forEach((key, value) -> this.put(key, ((BiFunction<Object, Object, Object>) function).apply(key, value)));
        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <p>
     * reset all values to default.
     *
     * @param <S> this
     * @return this
     */
    default <S extends Structure> S reset() {
        try {
            this.putAll(this.getClass().newInstance());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return (S) this;
    }

    /**
     * <b>BASE</b>
     * <p>
     * get the number of nodes contained in this structure.
     *
     * @return the count of elements inside this
     */
    default int size() {
        int i = 0;
        for (Field field : this.getClass().getFields())
            if (this.structured(field))
                i++;
        return i;
    }

    /**
     * <b>UTIL</b>
     * <p>
     * check if the given field is
     * structured or just transient.
     * <p>
     * exp:
     * structured field well be a node of this structure
     * and the transient field well not
     *
     * @param field to check if it's structured in this Structure or not
     * @return whether the given field'll be structured in this structure or well be ignored
     */
    default boolean structured(Field field) {
        return !Modifier.isTransient(field.getModifiers()) &&
                !field.isAnnotationPresent(Destructed.class) &&
                !Strings.any(field.getName(), "serialVersionUID", "$assertionsDisabled");
    }

    /**
     * <b>BASE</b>
     * <p>
     * get a list of the values contained in this.
     *
     * @return a list of the values contained in this
     * @see Map#values() the same idea in Map
     */
    default Collection<Object> values() {
        Collection<Object> values = new ArrayList<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field))
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);

                    if (value != null)
                        values.add(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return values;
    }

    /**
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Destructed {

    }

//    /**
//     * get the type of the value mapped
//     * to the given key.
//     *
//     * @param key to get mapped value from
//     * @return the type of the value mapped in the given key
//     */
//    /*abstract*/
//    default Class typeOf(Object key) {
//        if (key instanceof String)
//            try {
//                Field field = this.getClass().getField((String) key);
//
//                if (this.structured(field)) {
//                    field.setAccessible(true);
//                    Object object = field.get(this);
//                    return object == null ? field.getType() : object.getClass();
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (NoSuchFieldException ignored) {
//            }
//
//        return null;
//    }
//    /**
//     * Returns a Set view of the mappings contained in this map.
//     * The set is backed by the map,
//     * so changes to the map are reflected in the set,
//     * and vice-versa.
//     * If the map is modified while an iteration over the set
//     * is in progress (except through the iterator's own remove
//     * operation,
//     * or through the setValue operation on a map entry returned by the iterator)
//     * the results of the iteration are undefined.
//     * The set supports element removal,
//     * which removes the corresponding mapping from the map,
//     * via the Iterator.remove,
//     * Set.remove,
//     * removeAll,
//     * retainAll and clear operations.
//     * It does not support the add or addAll operations.
//     *
//     * NOTE: heavy weight
//     *
//     * @return a set view of the mappings contained in this map
//     */
//    /*[overload]*/
//    //TODO remove heavy weight
//    default Set<Map.Entry<Object, Object>> entrySet() {
//        Set<Map.Entry<Object, Object>> set = new HashSet<>();
//
//        for (Field field : this.getClass().getFields())
//            if (this.structured(field))
//                try {
//                    field.setAccessible(true);
//                    Object value = field.get(this);
//
//                    if (value != null)
//
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//
//        return this.map().entrySet();
//    }
}
