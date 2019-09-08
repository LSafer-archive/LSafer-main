package lsafer.util;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An interface defines that the targeted class can be used as a structure.
 * Basically structure means that the class can be working like a {@link Map}.
 * And every field of it (that matches the conditions of {@link #structured(Field)})
 * can be used as an {@link Map.Entry entry}.
 * <br><br>
 * <b>Important Info:</b>
 * <ul>
 * <li>
 * Every field that matches the conditions of {@link #structured(Field)} will be used as an {@link Map.Entry}.
 * </li>
 * <li>
 * You can {@link #put(Object, Object) put} some objects with a type different than the targeted field's type.
 * Depending on {@link #cast(Class, Object) the casting method} of this structure.
 * </li>
 * <li>
 * Declaring fields in the constructor of a super class will not work.
 * Because it'll be set to the value on the subclass after super class's constructor ends.
 * </li>
 * <li>
 * Use a secondary container like a {@link Map} with your structure.
 * So you can store keys even if the class don't have a {@link Field field} matches it.
 * </li>
 * </ul>
 *
 * <br>
 * <b>Method Classing:</b>
 * <ul>
 * <li>[BASE]: A base editing method.</li>
 * <li>[SUPER BASE]: A base editing method. That required to be called on all supers.</li>
 * <li>[UTIL]: A method used as a util.</li>
 * <li>[OVERLOAD]: A method uses base methods. But not a base method it self.</li>
 * <li>[FIELD CONTROL]: A method that deals with fields directly.</li>
 * </ul>
 *
 * <br>
 * <b>Field Control Methods:</b>
 * <ul>
 * <li>{@link #clear()} Clears ALL entries. and set ALL {@link #structured(Field) structed} fields to null.</li>
 * <li>{@link #containsField(String, Class)} Checks if there is a field with the given name and type.</li>
 * <li>{@link #containsKey(Object)} Checks if there is a key linked to any value (except null) in this.</li>
 * <li>{@link #get(Object)} Gets a value linked to the given key.</li>
 * <li>{@link #keySet()} Gets all keys that linked to a value (except null) in this.</li>
 * <li>{@link #map()} Gets all entries in this (except entries with null value).</li>
 * <li>{@link #overrideFromSuper(Class)} Overrides ALL fields in this that matches a name of the given super class of this.</li>
 * <li>{@link #overrideSuper(Class)} Overrides ALL fields in a super class of this with the fields in this.</li>
 * <li>{@link #put(Object, Object)} Links the given key to the given value.</li>
 * <li>{@link #remove(Object)} Unlink the given key from it's value. and set the field of the key to null.</li>
 * <li>{@link #size()} Gets the count of entries mapped in this.</li>
 * <li>{@link #values()} Gets all values that have been linked to a key in this (except null).</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 8 release (6-Sep-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings({"UnusedReturnValue", "DocLint", "unused"})
public interface Structure extends Serializable {
    /**
     * <b>UTIL</b><br>
     * Cast the given object to the targeted class.
     *
     * @param klass  targeted class to cast the object to
     * @param object to be casted
     * @param <T>    type of the targeted class (the return type)
     * @return the passed object casted to the given class (instance may change)
     * @see CastingEngine
     */
    default <T> T cast(Class<T> klass, Object object) {
        return CastingEngine.Default.instance.cast(klass, object);
    }

    /**
     * <b>OVERLOAD</b><br>
     * Cast every entry in this to the given class.
     *
     * <ul>
     * <li>uses: {@link #map()}</li>
     * <li>uses: heavy {@link Map#forEach(BiConsumer)}</li>
     * <li>uses: repetitive {@link #put(Object, Object)}</li>
     * <li>uses: repetitive {@link #cast(Class, Object)}</li>
     * </ul>
     *
     * @param klass to cast entries to
     * @param <S>   this
     * @return this
     */
    default <S extends Structure> S castAll(Class<?> klass) {
        this.map().forEach((key, value) -> this.put(key, this.cast(klass, value)));
        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <br>
     * Remove any key that this dose not have a field with the same name of it.
     *
     * @param <S> this
     * @return this
     */
    default <S extends Structure> S clean() {
        return (S) this;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Remove all entries from this and set all fields to null.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * <li>uses: repetitive {@link Field#set(Object, Object)}.</li>
     * </ul>
     *
     * @param <S> this
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
     * <br>
     * Copy this structure as other structure class.
     *
     * <ul>
     * <li>uses: {@link #putAll(Structure)}.</li>
     * </ul>
     *
     * @param klass to copy to
     * @param <S>   type of the class
     * @return new instance with same values of this but different class
     */
    default <S extends Structure> S clone(Class<S> klass) {
        try {
            return klass.newInstance().putAll(this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Map the given key to the given value. But only if the given key isn't mapped or mapped to null.
     *
     * @param key   to map
     * @param value to generate value if the given key is already have been mapped
     * @param <V>   type of the value
     * @return the actual value that have been added or the mapped value if the key have already mapped
     */
    default <V> V computeIfAbsent(Object key, Supplier<V> value) {
        V mapped = this.get(key);
        return mapped == null ? this.put(key, value.get()) : mapped;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Map the given key to the given value. But only if the given key isn't mapped or mapped to null.
     *
     * <ul>
     * <li>note: this will cast the mapped value (if present) and return it but it'll not map the casted instance.</li>
     * </ul>
     *
     * @param klass the class of the value
     * @param key   to map
     * @param value to generate value if the given key is already have been mapped
     * @param <V>   type of the value
     * @return the actual value that have been added or the mapped value if the key have already mapped
     */
    default <V> V computeIfAbsent(Class<? extends V> klass, Object key, Supplier<V> value) {
        V mapped = this.get(klass, key);
        return mapped == null ? this.put(key, value.get()) : mapped;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Checks if this class contains a field matches the given name.
     * And it's type is assignable from the given type.
     *
     * @param name of the field to check
     * @param type of the field to check
     * @return whether this class contains a field with the given name and matches the given value or not
     */
    default boolean containsField(String name, Class<?> type) {
        try {
            Field field = this.getClass().getField(name);

            if (this.structured(field)) {
                field.setAccessible(true);
                return type.isAssignableFrom(field.getType());
            }
        } catch (NoSuchFieldException ignored) {
        }

        return false;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Check if this contains the given key.
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
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Check if any key in this is mapped to the given value.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * <li>uses: repetitive {@link Field#get(Object)}.</li>
     * </ul>
     *
     * @param value to find
     * @return whether the given value is mapped with any key in this
     */
    default boolean containsValue(Object value) {
        Set<String> duplicated = new HashSet<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field) && !duplicated.contains(field.getName()))
                try {
                    duplicated.add(field.getName());

                    field.setAccessible(true);
                    return Objects.equals(field.get(this), value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return false;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Get the value mapped to the given key.
     *
     * @param key to get it's mapped value
     * @param <T> type of value
     * @return the mapped value to the given key
     */
    default <T> T get(Object key) {
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
     * <b>OVERLOAD</b>
     * <br>
     * Get the value mapped to the given key. Or returns
     * the given default value case the key didn't
     * exist or it's mapped to null.
     *
     * <ul>
     * <li>note: this will cast the mapped value. And return it. But it'll not map the casted instance.</li>
     * </ul>
     *
     * @param klass to make sure the mapped value is instance of the needed class
     * @param key   to get it's mapped value
     * @param <T>   type of value
     * @return the mapped value to the given key
     */
    default <T> T get(Class<? extends T> klass, Object key) {
        return this.cast(klass, this.get(key));
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Get a value mapped to the given key. Or returns
     * the given default value case the key didn't
     * exist or it's mapped to null.
     *
     * <ul>
     * <li>note: the "defaultValue" return will not be mapped (even if applied).</li>
     * <li>note: the "defaultValue" return will not be casted (if applied).</li>
     * </ul>
     *
     * @param key          to get it's mapped value
     * @param defaultValue case key not found
     * @param <T>          type of value
     * @return the mapped value to the given key
     */
    default <T> T get(Object key, Supplier<T> defaultValue) {
        T value = this.get(key);
        return value == null ? defaultValue.get() : value;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Get a value mapped to the given key. Or returns
     * the given default value case the key didn't
     * exist or it's mapped to null.
     *
     * <ul>
     * <li>note: this will cast the mapped value and return it but it'll not map the casted instance.</li>
     * <li>note: the "defaultValue" return will not be mapped (even if applied).</li>
     * <li>note: the "defaultValue" return will not be casted (if applied).</li>
     * </ul>
     *
     * @param klass        to make sure the mapped value is instance of the needed class
     * @param key          to get it's mapped value
     * @param defaultValue case key not found
     * @param <T>          type of value
     * @return the mapped value to the given key
     */
    default <T> T get(Class<? super T> klass, Object key, Supplier<T> defaultValue) {
        T value = (T) this.cast(klass, this.get(key));
        return value == null ? defaultValue.get() : value;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Returns a set of keys of this structure.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach.</li>
     * </ul>
     * <ul>
     * <li>note: the set is mostly not linked to this structure.</li>
     * </ul>
     *
     * @return a set view of the keys contained in this map
     */
    default Set<Object> keySet() {
        Set<Object> set = new HashSet<>();
        Set<String> duplicated = new HashSet<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field) && !duplicated.contains(field.getName())) {
                duplicated.add(field.getName());
                set.add(field.getName());
            }

        return set;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Get a map image of this structure.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link Field#get(Object)}.</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * </ul>
     *
     * @param <K> expected keys type
     * @param <V> expected values type
     * @return a map from this structure's entries
     */
    default <K, V> Map<K, V> map() {
        Map<K, V> map = new HashMap<>();
        Set<String> duplicated = new HashSet<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field) && !duplicated.contains(field.getName()))
                try {
                    duplicated.add(field.getName());

                    field.setAccessible(true);
                    map.put((K) field.getName(), (V) field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return map;
    }

    /**
     * <b>OVERLOAD</b>
     * Get a map image of this structure. But exclude ANY key/value not matches the given types.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: heavy foreach.</li>
     * </ul>
     *
     * @param keyClass   keys type filter
     * @param valueClass values type filter
     * @param <K>        keys type
     * @param <V>        values type
     * @return a filtered map of this structure's entries
     */
    default <K, V> Map<K, V> map(Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> map = new HashMap<>();

        this.map().forEach((key, value) -> {
            if (keyClass.isInstance(key) && valueClass.isInstance(value))
                map.put((K) key, (V) value);
        });

        return map;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Override the fields inside this from fields inside a super class of this.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * <li>uses: repetitive {@link Field#get(Object)}.</li>
     * <li>uses: repetitive {@link #put(Object, Object)}.</li>
     * </ul>
     *
     * @param klass to get fields from
     * @param <S>   this
     * @return this
     */
    default <S extends Structure> S overrideFromSuper(Class<? extends Structure> klass) {
        Set<String> duplicated = new HashSet<>();

        for (Field field : klass.getFields())
            if (this.structured(field) && !duplicated.contains(field.getName()))
                try {
                    duplicated.add(field.getName());

                    field.setAccessible(true);
                    this.put(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return (S) this;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Override fields in a super class of this from the fields inside this.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * <li>uses: repetitive {@link Field#set(Object, Object)}.</li>
     * <li>uses: repetitive {@link #cast(Class, Object)}.</li>
     * </ul>
     *
     * @param klass to override fields inside
     * @param <S>   this
     * @return this
     */
    default <S extends Structure> S overrideSuper(Class klass) {
        this.map().forEach((key, value) -> {
            if (key instanceof String)
                try {
                    Field field = klass.getField((String) key);

                    if (this.structured(field)) {
                        field.setAccessible(true);
                        field.set(this, this.cast(field.getType(), value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException ignored) {
                }
        });
        return (S) this;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Map the given value to the given key.
     *
     * <ul>
     * <li>
     * note: this method will try to cast the given value.
     * To match the field of the given key.
     * And will return the casted value.
     * </li>
     * </ul>
     * <br>
     *
     * @param key   to map to
     * @param value to map
     * @param <V>   type of the value
     * @return the actual value that have been added or null if the passed value is null
     */
    default <V> V put(Object key, V value) {
        if (key instanceof String)
            try {
                Field field = this.getClass().getField((String) key);

                if (this.structured(field)) {
                    field.setAccessible(true);
                    Object casted = this.cast(field.getType(), value);
                    field.set(this, casted);
                    return (V) casted;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return value;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Copy all (key-value) links from the given structure to this.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: {@link #putAll(Map)}.</li>
     * <li>uses: heavy foreach.</li>
     * </ul>
     *
     * @param structure to copy from
     * @param <S>       this
     * @return this
     */
    default <S extends Structure> S putAll(Structure structure) {
        this.putAll(structure.map());
        return (S) this;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Copy all (key-value) links from the given map to this.
     *
     * <ul>
     * <li>uses: {@link Map#forEach(BiConsumer)}.</li>
     * <li>uses: repetitive {@link #put(Object, Object)}.</li>
     * </ul>
     *
     * @param map to copy from
     * @param <S> type of this
     * @return this
     */
    default <S extends Structure> S putAll(Map<?, ?> map) {
        map.forEach(this::put);
        return (S) this;
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Unmap the given key from it's value.
     *
     * @param key to unmap
     * @return whether a thing have been remove or not
     */
    default boolean remove(Object key) {
        if (key instanceof String)
            try {
                Field field = this.getClass().getField((String) key);

                if (this.structured(field)) {
                    field.setAccessible(true);
                    field.set(this, null);
                    return true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return false;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Unmap the given key from it's value. But only if the mapped value {@link Object#equals(Object) equals} the given value.
     *
     * @param key   to unmap
     * @param value to remove if it's equals the value mapped to the given key
     * @return whether this contains the given key and it have get removed successfully, or not
     */
    default boolean remove(Object key, Object value) {
        return Objects.equals(this.get(key), value) && this.remove(key);
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Apply the given function foreach Key-Value in this.
     * And if the function reruns true then the applied key will be removed.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: heavy {@link Map#forEach(BiConsumer)}.</li>
     * <li>uses: repetitive {@link #remove(Object)}.</li>
     * </ul>
     *
     * @param function to use
     * @param <S>      this
     * @return this
     */
    default <S extends Structure> S removeIf(BiFunction<?, ?, Boolean> function) {
        this.map().forEach((k, v) -> {
            if (((BiFunction<Object, Object, Boolean>) function).apply(k, v))
                this.remove(k);
        });

        return (S) this;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Remove an entry that it's value {@link Objects#equals(Object, Object) equals} to the passed value from this.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link Objects#equals(Object, Object)}.</li>
     * <li>note: this method will remove one value only.</li>
     * </ul>
     *
     * @param value to be removed
     * @return if a value have been removed or not
     */
    default boolean removeValue(Object value) {
        for (Map.Entry entry : this.map().entrySet())
            if (Objects.equals(value, entry.getValue()))
                return this.remove(entry.getKey());

        return false;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Replace the value mapped to the given key with another value. Then return the old value.
     *
     * @param key      to replace it's value
     * @param function replace function
     * @param <O>      old value type
     * @param <N>      new value type
     * @return old value mapped to the given key
     */
    default <O, N> O replace(Object key, Function<O, N> function) {
        O old = this.get(key);
        this.put(key, function.apply(old));
        return old;
    }

    /**
     * <b>OVERLOAD</b>
     * <br>
     * Replace each element in this by the object returned by applying the given function on it.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: heavy {@link Map#forEach(BiConsumer)}.</li>
     * <li>uses: repetitive {@link #put(Object, Object)}.</li>
     * </ul>
     *
     * @param function to apply (replace) foreach entry in this (&lt;Key, Value, Return&gt;)
     * @param <S>      this
     * @return this
     */
    default <S extends Structure> S replaceAll(BiFunction<?, ?, ?> function) {
        this.map().forEach((key, value) -> this.put(key, ((BiFunction<Object, Object, Object>) function).apply(key, value)));
        return (S) this;
    }

    /**
     * <b>SUPER BASE</b>
     * <br>
     * Reset all values to default.
     *
     * <ul>
     * <li>uses: {@link #clear()}.</li>
     * <li>uses: {@link #putAll(Structure)}.</li>
     * </ul>
     *
     * @param <S> this
     * @return this
     */
    default <S extends Structure> S reset() {
        Structure.this.clear();

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
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Get the number of entries contained in this structure.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * <li>uses: repetitive {@link Field#get(Object)}.</li>
     * </ul>
     *
     * @return the count of elements inside this
     */
    default int size() {
        Set<String> duplicated = new HashSet<>();
        int i = 0;

        for (Field field : this.getClass().getFields())
            if (this.structured(field) && !duplicated.contains(field.getName())) {
                duplicated.add(field.getName());
                i++;
            }

        return i;
    }

    /**
     * <b>UTIL</b>
     * <br>
     * Check if the given field can be structured.
     *
     * @param field to check if it's structured in this Structure or not
     * @return whether the given field'll be structured in this structure or well be ignored
     */
    default boolean structured(Field field) {
        int modifier = field.getModifiers();

        return !Modifier.isPrivate(modifier) &&
               !Modifier.isProtected(modifier) &&
               !Modifier.isTransient(modifier) &&
               !field.isAnnotationPresent(Destructed.class) &&
               !Strings.any(field.getName(), "serialVersionUID", "$assertionsDisabled");
    }

    /**
     * <b>SUPER BASE | FIELD CONTROL</b>
     * <br>
     * Get a list of the values contained in this.
     *
     * <ul>
     * <li>uses: {@link Class#getFields()}.</li>
     * <li>uses: heavy foreach</li>
     * <li>uses: repetitive {@link #structured(Field)}.</li>
     * <li>uses: repetitive {@link Field#get(Object)}.</li>
     * </ul>
     *
     * @return a list of the values contained in this
     */
    default Collection<Object> values() {
        Collection<Object> values = new ArrayList<>();
        Set<String> duplicated = new HashSet<>();

        for (Field field : this.getClass().getFields())
            if (this.structured(field) && !duplicated.contains(field.getName()))
                try {
                    duplicated.add(field.getName());

                    field.setAccessible(true);
                    Object value = field.get(this);

                    values.add(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return values;
    }

    /**
     * A way to annotate fields that you don't want to be structured.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Destructed {
    }
}
