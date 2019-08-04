package lsafer.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import lsafer.lang.Reflect;

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
 * <li>fields that contains any of {@link #isIgnored(Object)} destruct symbols} in their names well not be a node</li>
 * <li>java non-object fields (like int, float, etc...) may cause casting problems</li>
 * <li>declaring fields in the constructor is an unprotect way</li>
 * <li>you can {@link #put(Object, Object) put} some objects with a type different than the targeted field's type, see {@link #castObject(Class, Object)} for more info</li>
 * <li>the only way to declare a default value is to declare it directly to the field (or on it's constructor) , other ways may not work , so be careful while using nested structures</li>
 * </ul>
 *
 * <p>
 * to use any of these methods with your structure :
 * <ul>
 * <li>{@link #castObject(Class, Object) castObject(YourClass, Object)}</li>
 * <li>{@link #newInstance(Class, Object...) newInstance(YourClass, Object)}</li>
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
 * <li>
 * if you want an {@link ArrayList array list} and you want it's elements to be casted to a specific class,
 * please use {@link CastList cast list} and don't make it null, also
 * </li>
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
 * <li>{@link #typeOf(Object)} to check the secondary container too (do super first)</li>
 * <li>{@link #isIgnored(Object)} to declare what keys to ignore</li>
 * </ul>
 *
 * @author LSaferSE
 * @version 6 release (28-Jul-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
//TODO
//  -methods weight improvements
//  -structure 2 map duel synchronization
public interface Structure {

    /**
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
     * @see #newInstance(Class, Object...) used to run a new instance of the klass (case the structuable is assignable from it)
     * @see #putAll(Map) used to fill the structable (case the structuable is assignable from the klass and the value is a map)
     */
    static <T> T CastObject(Class<T> klass, Object value) {
        //? <- null
        //(value equals null)
        if (value == null) {
            return null;
        }
        //? <- ?
        //(value instanceOf klass)
        if (klass.isInstance(value)) {
            return (T) value;
        }
        //String <- ?
        //(klass instanceOf String) and (value instanceOf Object)
        if (klass == String.class) {
            return (T) String.valueOf(value);
        }
        //Number <- Float | Double | Integer | Long | String
        //(klass subClassOf Number) and (value instanceOf Number or String)
        if (Number.class.isAssignableFrom(klass) && (value instanceof Number || value instanceof String)) {
            try {
                return (T) klass.getMethod("valueOf", String.class)
                        .invoke(null, klass == Integer.class || klass == Long.class ?
                                String.valueOf(value).split("[.]")[0] : String.valueOf(value));
            } catch (Exception ignored) {
                return null;
            }
        }
        //List <- Object[]
        //(klass subClassOf List) and (value instanceOf Object[])
        if (klass == List.class && value instanceof Object[]) {
            return (T) Arrays.asList((Object[]) value);
        }
        //Map <- Structure
        //(klass equals Map) and (value instanceOf Structure)
        if (klass == Map.class && value instanceof Structure) {
            return (T) ((Structure) value).map();
        }
        //Structure <- Map
        //(klass subClassOf Structure) and (value instanceOf Map)
        if (Structure.class.isAssignableFrom(klass) && value instanceof Map) {
            Structure structure = Structure.newInstance((Class<? extends Structure>) klass);
            structure.putAll((Map<String, Object>) value);
            return (T) structure;
        }
        //Structure <- Structure
        //(klass subClassOf Structure) and (value instanceOf Structure)
        if (Structure.class.isAssignableFrom(klass) && value instanceof Structure) {
            return (T) ((Structure) value).clone((Class<? extends Structure>) klass);
        }
        //to avoid null pointer exception
        if (Object[].class.isAssignableFrom(klass))
            //Object[] <- List
            //(klass subClassOf Object[]) and (value instanceOf List)
            if (value instanceof List) {
                //noinspection unchecked
                return (T) Arrays.asArray((List) value, klass.getComponentType());
            }
            //Map[] <- Structure[]
            //(klass.component equals Map) and (value instanceOf Structure[])
            else if (klass.getComponentType() == Map.class && value instanceof Structure[]) {
                Structure[] structures = (Structure[]) value;
                Map[] maps = (Map[]) Array.newInstance(klass.getComponentType(), structures.length);

                for (int i = 0; i < structures.length; i++) {
                    maps[i] = structures[i].map();
                }

                return (T) maps;
            }
            //Structure[] <- Map[]
            //(klass.component subClassOf Structure) and (value instanceOf Map[])
            else if (Structure.class.isAssignableFrom(klass.getComponentType()) && value instanceof Map[]) {
                Map[] maps = (Map[]) value;
                Structure[] structures = (Structure[]) Array.newInstance(klass.getComponentType(), maps.length);

                for (int i = 0; i < maps.length; i++) {
                    structures[i] = Structure.newInstance((Class<? extends Structure>) klass);
                    structures[i].putAll(maps[i]);
                }

                return (T) structures;
            }

        //failed to cast
        return null;
    }

    /**
     * get new instance of the given class.
     * there is no problem init with the constructor directly :)
     *
     * @param klass     to run new instance of
     * @param arguments to pass to the constructor
     * @param <S>       type of the object
     * @return new Instance of the given class
     */
    static <S extends Structure> S newInstance(Class<? extends S> klass, Object... arguments) {
        if (Reflect.containsConstructor(klass, Reflect.CONSTRUCTOR_VARARG))
            return Reflect.getInstanceOf(klass, Reflect.CONSTRUCTOR_VARARG, arguments);
        else if (Reflect.containsConstructor(klass, Reflect.CONSTRUCTOR_DEFAULT))
            return Reflect.getInstanceOf(klass, Reflect.CONSTRUCTOR_DEFAULT);
        else
            throw new IllegalStateException("can't create new instance of " + klass);
    }

    /**
     * default method to cast objects.
     *
     * @param klass to cast object to
     * @param value to be cast
     * @param <T>   type of the cast value
     * @return a value casted to the given class
     * @see #CastObject(Class, Object)
     */
    default <T> T castObject(Class<T> klass, Object value) {
        return Structure.CastObject(klass, value);
    }

    /**
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
     * remove all nodes from this
     * and set all fields to null.
     *
     * @param <S> type of this
     * @return this
     */
    default <S extends Structure> S clear() {
        for (Field field : this.getClass().getFields())
            if (!this.isIgnored(field.getName()))
                try {
                    field.setAccessible(true);
                    if (field.getType() == CastList.class) {
                        ((CastList) field.get(this)).clear();
                    } else {
                        field.set(this, null);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        return (S) this;
    }

    /**
     * copy this structure as other structure class.
     *
     * @param klass to copy to
     * @param <S>   type of the class
     * @return new instance with same values of this but different class
     * @see #newInstance(Class, Object...) used to run a new instance of the given class
     * @see #putAll(Structure) used to put all of this into the new instance
     */
    default <S extends Structure> S clone(Class<S> klass) {
        S structure = Structure.newInstance(klass);
        structure.putAll(this);
        return structure;
    }

    /**
     * checks if this class contains a field
     * matches the given name and it's type
     * is assignable from the given type.
     *
     * @param name of the field to check
     * @param type of the field to check
     * @return whether this class contains a field with the given name and matches the given value or not
     */
    /*final*/
    default boolean containsField(String name, Class<? /*super FIELD_TYPE*/> type) {
        if (!this.isIgnored(name))
            try {
                return type.isAssignableFrom(this.getClass().getField(name).getType());
            } catch (NoSuchFieldException ignored) {
            }

        return false;
    }

    /**
     * check if this contains the given key and it didn't
     * mapped to null.
     *
     * @param key to find
     * @return whether this contains the given key and not mapped to null
     */
    default boolean containsKey(Object key) {
        if (key instanceof String && !this.isIgnored(key))
            try {
                Field field = this.getClass().getField((String) key);
                field.setAccessible(true);
                return field.get(this) != null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return false;
    }

    /**
     * check if any key in this is mapped to the given value.
     *
     * @param value to find
     * @return whether the given value is mapped with any key in this
     */
    default boolean containsValue(Object value) {
        for (Field field : this.getClass().getFields())
            if (!this.isIgnored(field.getName()))
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
     * do a function foreach element in this.
     *
     * @param consumer function to apply
     * @param <S>      this
     * @return this
     * @see Map#forEach(BiConsumer)
     */
    /*final*/
    default <S extends Structure> S foreach(BiConsumer<Object, Object> consumer) {
        this.map().forEach(consumer);
        return (S) this;
    }

    /**
     * run a value mapped to the given key.
     *
     * @param key to run it's mapped value
     * @param <T> type of value
     * @return the mapped value to the given key
     */
    default <T> T get(Object key) {
        if (key instanceof String && !this.isIgnored(key))
            try {
                Field field = this.getClass().getField((String) key);
                field.setAccessible(true);
                return (T) this.getClass().getField((String) key).get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return null;
    }

    /**
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
     */
    /*final*/
    default <T> T get(Class<? super T> klass, Object key) {
        return (T) this.castObject(klass, this.get(key));
    }

    /**
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
    /*final*/
    default <T> T get(Object key, Function<?, T> defaultValue) {
        T value = this.get(key);
        return value == null ? defaultValue.apply(null) : value;
    }

    /**
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
    /*final*/
    default <T> T get(Class<? super T> klass, Object key, Function<Class<? super T>, T> defaultValue) {
        T value = (T) this.castObject(klass, this.get(key));
        return value == null ? defaultValue.apply(klass) : value;
    }

    /**
     * check whether the given key should be ignored or not.
     *
     * @param key to check
     * @return whether the given key should be ignored or not
     */
    default boolean isIgnored(Object key) {
        return !(key instanceof String) || Strings.any((String) key, "$", "IncrementalChange", "serialVersionUID");
    }

    /**
     * run this as a map.
     *
     * @return this as a map
     */
    default Map<Object, Object> map() {
        Map<Object, Object> map = new HashMap<>();

        for (Field field : this.getClass().getFields())
            if (!map.containsKey(field.getName()) && !this.isIgnored(field.getName()))
                try {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return map;
    }

    /**
     * override the fields inside this from fields inside a super class of this.
     *
     * @param klass to get fields from
     * @param <S>   this
     * @return this
     */
    /*final*/
    default <S extends Structure> S overrideFromSuper(Class klass) {
        List<String> overridden = new ArrayList<>();

        if (klass.isInstance(this)) {
            for (Field field : klass.getFields())
                if (!overridden.contains(field.getName()))
                    try {
                        field.setAccessible(true);
                        this.put(field.getName(), field.get(this));
                        overridden.add(field.getName());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
        } else {
            throw new IllegalStateException(this.getClass() + " isn't an instance of " + klass);
        }

        return (S) this;
    }

    /**
     * override the fields inside this from fields inside the parent class of this.
     *
     * @param <S> this
     * @return this
     */
    /*final*/
    default <S extends Structure> S overrideFromSuper() {
        return this.overrideFromSuper(this.getClass().getSuperclass());
    }

    /**
     * override fields in a super class of this from the fields inside this.
     *
     * @param klass to override fields inside
     * @param <S>   this
     * @return this
     */
    /*final*/
    default <S extends Structure> S overrideSuper(Class klass) {
        if (klass.isInstance(this))
            this.map().forEach((key, value) -> {
                if (key instanceof String)
                    try {
                        Field field = klass.getField((String) key);
                        field.setAccessible(true);
                        field.set(this, this.castObject(field.getType(), value));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException ignored) {
                    }
            });
        else throw new IllegalStateException(this.getClass() + " isn't an instance of " + klass);
        return (S) this;
    }

    /**
     * override fields in the parent super class of this from the fields inside this.
     *
     * @param <S> this
     * @return this
     */
    /*final*/
    default <S extends Structure> S overrideSuper() {
        return this.overrideSuper(this.getClass().getSuperclass());
    }

    /**
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
     * @see #castObject(Class, Object) used to cast values to field's type
     */
    default <V> V put(Object key, V value) {
        if (key instanceof String && !this.isIgnored(key))
            try {
                Field field = this.getClass().getField((String) key);
                field.setAccessible(true);

                if (field.getType() == CastList.class) {
                    List<Object> value1 = (List<Object>) this.castObject(List.class, value);

                    if (value1 != null) {
                        CastList<Object> value2 = (CastList<Object>) field.get(this);
                        value2 = new CastList<>(value2 == null ? Object.class : value2.getComponentType());
                        value2.addAll(value1);
                        value2.castElements();
                        field.set(this, value2);
                        return (V) value2;
                    }
                } else {
                    Object value1 = this.castObject(field.getType(), value);

                    if (value1 != null) {
                        field.set(this, value1);
                        return (V) value1;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return value;
    }

    /**
     * copy all (key-value) links from the given
     * structure to this.
     *
     * @param structure to copy from
     * @param <S>       type of this
     * @return this
     * @see #map() used to run structure's map
     * @see #putAll(Map) used to putAll the structure's map
     */
    /*final*/
    default <S extends Structure> S putAll(Structure structure) {
        this.putAll(structure.map());
        return (S) this;
    }

    /**
     * copy all (key-value) links from the given
     * map to this.
     *
     * @param map to copy from
     * @param <S> type of this
     * @return this
     * @see #put(Object, Object) used to put foreach value
     */
    /*final*/
    default <S extends Structure> S putAll(Map<?, ?> map) {
        map.forEach(this::put);
        return (S) this;
    }

    /**
     * map the given key to the given value only if
     * the given key isn't mapped or mapped to null.
     *
     * @param key   to map
     * @param value to generate value if the given key is already have been mapped
     * @param <V>   type of the value
     * @return the actual value that have been added or the mapped value if the key have already mapped
     */
    /*final*/
    default <V> V putIfAbsent(Object key, Function<?, V> value) {
        V mapped = this.get(key);
        return mapped == null ? this.put(key, value.apply(null)) : mapped;
    }

    /**
     * map the given key to the given value only if
     * the given key isn't mapped or mapped to null.
     *
     * @param klass the class of the value
     * @param key   to map
     * @param value to generate value if the given key is already have been mapped
     * @param <V>   type of the value
     * @return the actual value that have been added or the mapped value if the key have already mapped
     */
    /*final*/
    default <V> V putIfAbsent(Class<? super V> klass, Object key, Function<Class<? super V>, V> value) {
        V mapped = this.get(klass, key);
        return mapped == null ? this.put(key, value.apply(klass)) : mapped;
    }

    /**
     * unmap the given key from it's value.
     *
     * @param key to unmap
     */
    default void remove(Object key) {
        if (key instanceof String && !this.isIgnored(key))
            try {
                Field field = this.getClass().getField((String) key);
                field.setAccessible(true);
                field.set(this, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }
    }

    /**
     * unmap the given key from it's value only if it's
     * value {@link Object#equals(Object) equals} the given key.
     *
     * @param key   to unmap
     * @param value to remove if it's equals the value mapped to the given key
     */
    /*final*/
    default void remove(Object key, Object value) {
        if (this.get(key).equals(value))
            this.remove(value);
    }

    /**
     * remove all nodes found in the given map from this.
     *
     * @param map to remove
     * @param <S> type of this
     * @return this
     */
    /*final*/
    default <S extends Structure> S removeAll(Map<?, ?> map) {
        map.forEach(this::remove);
        return (S) this;
    }

    /**
     * remove all nodes found in the given structure from this.
     *
     * @param structure to remove
     * @param <S>       type of this
     * @return this
     */
    /*final*/
    default <S extends Structure> S removeAll(Structure structure) {
        return this.removeAll(structure.map());
    }

    /**
     * replace a value in this.
     *
     * @param klass        of the value
     * @param key          were the value have been mapped at
     * @param defaultValue in case the mapped value is null
     * @param replacement  function to get the replacement from
     * @param <V>          value type
     * @return old value
     */
    /*final*/
    default <V> V replace(Class<V> klass, Object key, Function<Class<? super V>, V> defaultValue, Function<V, V> replacement) {
        if (!this.isIgnored(key)) {
            V o = this.get(klass, key);
            if (o == null) o = defaultValue.apply(klass);
            V n = replacement.apply(o);
            this.put(key, n);
            return o;
        }

        return null;
    }

    /**
     * replace a value in this.
     *
     * @param key          were the value have been mapped at
     * @param defaultValue in case the mapped value is null
     * @param replacement  function to get the replacement from
     * @param <V>          value type
     * @return old value
     */
    /*final*/
    default <V> V replace(Object key, Function<?, V> defaultValue, Function<V, V> replacement) {
        if (!this.isIgnored(key)) {
            V o = this.get(key);
            if (o == null) o = defaultValue.apply(null);
            V n = replacement.apply(o);
            this.put(key, n);
            return o;
        }

        return null;
    }

    /**
     * replace foreach element in this.
     *
     * @param function to apply (replace) foreach node in this &lt;Key, Value, Return&gt;
     * @param <S>      this
     * @return this
     */
    /*final*/
    default <S extends Structure> S replaceAll(BiFunction<Object, Object, Object> function) {
        this.map().forEach((key, value) -> this.put(key, function.apply(key, value)));
        return (S) this;
    }

    /**
     * reset all values to default.
     *
     * @param <S> type of this
     * @return this
     * @see #newInstance(Class, Object...) used to run defaults from the new Instance
     * @see #putAll(Structure) used to put defaults
     */
    default <S extends Structure> S reset() {
        this.putAll(Structure.newInstance(this.getClass()));
        return (S) this;
    }

    /**
     * get the size of this.
     *
     * @return the count of elements inside this
     */
    /*final*/
    default int size() {
        return this.map().size();
    }

    /**
     * get the type of the value mapped
     * to the given key.
     *
     * @param key to get mapped value from
     * @return the type of the value mapped in the given key
     */
    default Class typeOf(Object key) {
        if (key instanceof String && !this.isIgnored(key))
            try {
                Field field = this.getClass().getField((String) key);
                field.setAccessible(true);
                Object object = field.get(this);
                return object == null ? field.getType() : object.getClass();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException ignored) {
            }

        return null;
    }

    /**
     * get a list of the values contained in this.
     *
     * @return a list of the values contained in this
     * @see Map#values()
     */
    /*final*/
    default Collection<Object> values() {
        return this.map().values();
    }

    /**
     * just an {@link ArrayList array list}
     * but it can store it's class.
     */
    class CastList<E> extends ArrayList<E> {
        /**
         * elements class.
         */
        private Class<E> klass;

        /**
         * init this.
         *
         * @param klass elements class
         */
        public CastList(Class<E> klass) {
            super();
            this.klass = klass;
        }

        /**
         * init this.
         *
         * @param initialCapacity capacity to began with
         * @param klass           elements class
         * @see java.util.ArrayList#ArrayList(int) original method
         */
        public CastList(Class<E> klass, int initialCapacity) {
            super(initialCapacity);
            this.klass = klass;
        }

        /**
         * init this.
         *
         * @param klass      elements class
         * @param collection collection to copy from
         * @see java.util.ArrayList#ArrayList(Collection) original method
         */
        public CastList(Class<E> klass, Collection<E> collection) {
            super(collection);
            this.klass = klass;
        }

        /**
         * cast elements in this to {@link #klass targeted class}.
         */
        public void castElements() {
            List<E> list = new ArrayList<>();
            this.forEach((e) -> list.add(Structure.CastObject(this.klass, e)));
            this.clear();
            this.addAll(list);
        }

        /**
         * get the type of elements of this.
         *
         * @return the type of the elements of this
         */
        public Class<E> getComponentType() {
            return this.klass;
        }
    }
}
