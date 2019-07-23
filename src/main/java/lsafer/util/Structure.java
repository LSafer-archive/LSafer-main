package lsafer.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <li>all public fields well be a node (even final and static fields)[all but fields excluded in this list]</li>
 * <li>private field well not be a node</li>
 * <li>protected fields sometimes well not be a node (but sometimes well be by mistake , so please add '$' char in their name to make sure it's excluded)</li>
 * <li>fields that contains any of {@link #$DESTRUCT_SYMBOLS destruct symbols} in their names well not be a node</li>
 * <li>java non-object fields (like int, float, etc...) may cause casting problems</li>
 * <li>declaring fields in the constructor is an unprotectable way</li>
 * <li>you can {@link #put(Object, Object) put} some objects with a type different than the targeted field's type, see {@link #castValue(Class, Object)} for more info</li>
 * <li>the only way to declare a default value is to declare it directly to the field (or on it's constructor) , other ways may not work , so be careful while using nested structures</li>
 * </ul>
 *
 * <p>
 * to use any of these methods with your structurable :
 * <ul>
 * <li>{@link #castValue(Class, Object) castValue(YourClass, Object)}</li>
 * <li>{@link #newInstance(Class, Object...) newInstance(YourClass, Object)}</li>
 * <li>{@link #clone(Class) clone(YourClass)}</li>
 * <li>{@link #reset() YourClass.reset()}</li>
 * </ul>
 * your structurable should match this rules :
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
 * <li>add a secondary container like a {@link Map} to your structurable so you can store keys even if the class don't have a {@link Field field} matches it.</li>
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
 * <li>{@link #shiftin()} fill the fields from the secondary containers, use {@link #put(Object, Object) Structure.super.put(Object, Object)}</li>
 * <li>{@link #shiftout()} fill the secondary containers from the fields, use {@link #map() Structure.super.map()}</li>
 * <li></li>
 * </ul>
 *
 * @author LSaferSE
 * @version 5 release (19-Jul-2019)
 * @since 06-Jul-19
 */
public interface Structure {

    /**
     * Strings that if it run found in the field's name then the field well NOT be struct.
     */
    String[] $DESTRUCT_SYMBOLS = {
            "$", //a destruct key
            "IncrementalChange", //android specific system field
            "serialVersionUID", //android specific system field
    };

    /**
     * cast the given object to the given klass
     * used to cast objects that we have to cast it manually.
     *
     * <p>----------</p>
     * <p>
     * supported casts (else well return null):
     * <ul>
     * <li>(value equals null)</li>
     * <li>(value getInstanceOf klass)</li>
     * <li>(klass subClassOf {@link Structure}) and (value getInstanceOf {@link Map})</li>
     * <li>(klass equals {@link Map}) and (value getInstanceOf {@link Structure})</li>
     * <li>(klass.component subClassOf {@link Structure}) and (value getInstanceOf {@link Map[]})</li>
     * <li>(klass.component equals {@link Map}) and (value getInstanceOf {@link Structure[]})</li>
     * <li>(klass subClassOf {@link Number}) and (value getInstanceOf {@link Number} or {@link String})</li>
     * <li>(klass subClassOf {@link List}) and (value getInstanceOf {@link Object[]})</li>
     * <li>(klass subClassOf {@link Object[]}) and (value getInstanceOf {@link List})</li>
     * </ul>
     *
     * @param klass   to cast to
     * @param value   to cast
     * @param <VALUE> type of the class to cast to
     * @return value casted to the given class
     * @see #newInstance(Class, Object...) used to run a new instance of the klass (case the structuable is assignable from it)
     * @see #putAll(Map) used to fill the structable (case the structuable is assignable from the klass and the value is a map)
     */
    static <VALUE> VALUE castValue(Class<VALUE> klass, Object value) {
        //? <- null
        //(value equals null)
        if (value == null) {
            return null;
        }

        //? <- ?
        //(value getInstanceOf klass)
        else if (klass.isInstance(value)) {
            return (VALUE) value;
        } else if (Structure.class.isAssignableFrom(klass) && value instanceof Structure) {
            ((Structure) value).clone((Class<? extends Structure>) klass);
        }

        //Structure <- Map
        //(klass subClassOf Structure) and (value getInstanceOf Map)
        else if (Structure.class.isAssignableFrom(klass) && value instanceof Map) {
            Structure structure = newInstance((Class<? extends Structure>) klass);
            structure.putAll((Map<String, Object>) value);
            return (VALUE) structure;
        }

        //Map <- Structure
        //(klass equals Map) and (value getInstanceOf Structure)
        else if (klass.equals(Map.class) && value instanceof Structure) {
            return (VALUE) ((Structure) value).map();
        }

        //Structure[] <- Map[]
        //(klass.component subClassOf Structure) and (value getInstanceOf Map[])
        else if (Structure.class.isAssignableFrom(klass.getComponentType()) && value instanceof Map[]) {
            Map[] maps = (Map[]) value;
            Structure[] structures = (Structure[]) Array.newInstance(klass.getComponentType(), maps.length);

            for (int i = 0; i < maps.length; i++) {
                structures[i] = newInstance((Class<? extends Structure>) klass);
                structures[i].putAll(maps[i]);
            }

            return (VALUE) structures;
        }

        //Map[] <- Structure[]
        //(klass.component equals Map) and (value getInstanceOf Structure[])
        else if (klass.getComponentType().equals(Map.class) && value instanceof Structure[]) {
            Structure[] structures = (Structure[]) value;
            Map[] maps = (Map[]) Array.newInstance(klass.getComponentType(), structures.length);

            for (int i = 0; i < structures.length; i++) {
                maps[i] = structures[i].map();
            }

            return (VALUE) maps;
        }

        //Number <- Float | Double | Integer | Long | String
        //(klass subClassOf Number) and (value getInstanceOf Number or String)
        else if (Number.class.isAssignableFrom(klass) && (value instanceof Number || value instanceof String)) {
            try {
                return (VALUE) klass.getMethod("valueOf", String.class).invoke(null, value.toString());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        //List <- Object[]
        //(klass subClassOf List) and (value getInstanceOf Object[])
        else if (List.class.isAssignableFrom(klass) && value instanceof Object[]) {
            return (VALUE) Arrays.asList(((Object[]) value));
        }

        //Object[] <- List
        //(klass subClassOf Object[]) and (value getInstanceOf List)
        else if (Object[].class.isAssignableFrom(klass) && value instanceof List) {
            //noinspection unchecked
            return (VALUE) Arrays.asArray((List) value, klass.getComponentType());
        }

        //failed to cast
        return null;
    }

    /**
     * run new Instance of the given class.
     * there is no problem init with the constructor directly :)
     *
     * @param klass      to run new instance of
     * @param arguments  to pass to the constructor
     * @param <INSTANCE> type of the object
     * @return new Instance of the given class
     */
    static <INSTANCE extends Structure> INSTANCE newInstance(Class<? extends INSTANCE> klass, Object... arguments) {
        if (Reflect.containsConstructor(klass, Reflect.CONSTRUCTOR_VARARG))
            return Reflect.getInstanceOf(klass, Reflect.CONSTRUCTOR_VARARG, arguments);
        else if (Reflect.containsConstructor(klass, Reflect.CONSTRUCTOR_DEFAULT))
            return Reflect.getInstanceOf(klass, Reflect.CONSTRUCTOR_DEFAULT);
        else
            throw new IllegalStateException("can't create new Instance of ( " + klass + " )");
    }

    /**
     * remove any key that this dose not
     * have a field with the same name
     * of it.
     */
    default void clean() {

    }

    /**
     * remove all nodes from this
     * and set all fields to null.
     */
    default void clear() {
        for (Field field : this.getClass().getFields())
            try {
                if (!Strings.any(field.getName(), $DESTRUCT_SYMBOLS))
                    field.set(this, null);
            } catch (IllegalAccessException e) {
                //field is final or private
            }
    }

    /**
     * copy this structurable as other structurable class.
     *
     * @param klass   to copy to
     * @param <CLONE> type of the class
     * @return new instance with same values of this but different class
     * @see #newInstance(Class, Object...) used to run a new instance of the given class
     * @see #putAll(Structure) used to put all of this into the new instance
     */
    default <CLONE extends Structure> CLONE clone(Class<CLONE> klass) {
        CLONE structure = newInstance(klass);
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
        if (!Strings.any(name, $DESTRUCT_SYMBOLS))
            try {
                return type.isAssignableFrom(this.getClass().getField(name).getType());
            } catch (NoSuchFieldException e) {
                //
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
        if (key instanceof String && !Strings.any((String) key, $DESTRUCT_SYMBOLS))
            try {
                return this.getClass().getField((String) key).get(this) != null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                //
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
            if (!Strings.any(field.getName(), $DESTRUCT_SYMBOLS))
                try {
                    if (field.get(this) == value)
                        return true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        return false;
    }

    /**
     * run a value mapped to the given key.
     *
     * @param key     to run it's mapped value
     * @param <VALUE> type of value
     * @return the mapped value to the given key
     */
    default <VALUE> VALUE get(Object key) {
        if (key instanceof String && !Strings.any((String) key, $DESTRUCT_SYMBOLS))
            try {
                return (VALUE) this.getClass().getField((String) key).get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                //no field matches the given key
            }

        return null;
    }

    /**
     * run a value mapped to the given key
     * and returns the given default value
     * case the key didn't exist or mapped
     * to null.
     *
     * @param key          to run it's mapped value
     * @param defaultValue case key not found
     * @param <VALUE>      type of value
     * @return the mapped value to the given key
     * @see #get(Object) to run the value
     */
    /*final*/
    default <VALUE> VALUE get(Object key, VALUE defaultValue) {
        VALUE value = this.get(key);
        return value == null ? defaultValue : value;
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
     * @param <VALUE>      type of value
     * @return the mapped value to the given key
     * @see #get(Object) to run the value
     */
    /*final*/
    default <VALUE> VALUE get(Class<VALUE> klass, Object key, VALUE defaultValue) {
        VALUE value = this.get(key);
        return klass.isInstance(value) ? value : defaultValue;
    }

    /**
     * run this as a map.
     *
     * @return this as a map
     */
    default Map<Object, Object> map() {
        Map<Object, Object> map = new HashMap<>();

        for (Field field : this.getClass().getFields())
            if (!Strings.any(field.getName(), $DESTRUCT_SYMBOLS))
                try {
                    map.put(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

        return map;
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
     * @return the value that have been added to the field
     * and null if the passed value is null
     * @see #castValue(Class, Object) used to cast values to field's type
     */
    default Object put(Object key, Object value) {
        if (key instanceof String && !Strings.any((String) key, $DESTRUCT_SYMBOLS))
            try {
                Field field = this.getClass().getField((String) key);

                if (!Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    Object casted_value = castValue(field.getType(), value);

                    if (casted_value != null) {
                        field.set(this, casted_value);
                        return casted_value;
                    }
                }

            } catch (IllegalAccessException e) {
                //field is final or private
            } catch (NoSuchFieldException e) {
                //no field matches the given key
            }

        return value;
    }

    /**
     * copy all (key-value) links from the given
     * structure to this.
     *
     * @param structure to copy from
     * @see #map() used to run structure's map
     * @see #putAll(Map) used to putAll the structure's map
     */
    /*final*/
    default void putAll(Structure structure) {
        this.putAll(structure.map());
    }

    /**
     * copy all (key-value) links from the given
     * map to this.
     *
     * @param map to copy from
     * @see #put(Object, Object) used to put foreach value
     */
    /*final*/
    default void putAll(Map<?, ?> map) {
        map.forEach(this::put);
    }

    /**
     * unmap the given key from it's value.
     *
     * @param key to unmap
     */
    default void remove(Object key) {
        if (key instanceof String && !Strings.any((String) key, $DESTRUCT_SYMBOLS))
            try {
                this.getClass().getField((String) key).set(this, null);
            } catch (IllegalAccessException e) {
                //field is private
            } catch (NoSuchFieldException e) {
                //no field matches the targeted key
            }
    }

    /**
     * reset all values to default.
     *
     * @see #newInstance(Class, Object...) used to run defaults from the new Instance
     * @see #putAll(Structure) used to put defaults
     */
    default void reset() {
        this.putAll(newInstance(this.getClass()));
    }

    /**
     * update fields from the secondary container.
     */
    default void shiftin() {

    }

    /**
     * update the secondary container from fields.
     * <p>
     * use {@link #map() Structure.super.map()} to run a
     * map of this object's fields
     */
    default void shiftout() {

    }

    /**
     * get the type of the value mapped
     * to the given key.
     *
     * @param key to get mapped value from
     * @return the type of the value mapped in the given key
     */
    default Class typeOf(Object key) {
        if (key instanceof String && !Strings.any((String) key, $DESTRUCT_SYMBOLS))
            try {
                return this.getClass().getField((String) key).getType();
            } catch (NoSuchFieldException e) {
                //no field
            }

        return null;
    }

}
