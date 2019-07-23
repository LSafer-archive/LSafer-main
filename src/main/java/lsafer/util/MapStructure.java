package lsafer.util;

import java.util.HashMap;
import java.util.Map;

/**
 * structurable with a {@link Map} as a secondary container.
 * <p>
 * make sure your {@link MapStructure structure} matches all {@link Structure structurables} rules
 *
 * @author LSafer
 * @version 8 release (19-Jul-2019)
 * @since 11 Jun 2019
 **/
@SuppressWarnings({"WeakerAccess"})
public class MapStructure implements Structure {

    /**
     * secondary container.
     */
    final protected Map<Object, Object> $value = new HashMap<>();

    /**
     * to unify all Structures constructors.
     *
     * @param arguments if subclasses needs
     */
    public MapStructure(Object... arguments) {
    }

    @Override
    public void clean() {
        Structure.super.clean();
        this.$value.clear();
    }

    @Override
    public void clear() {
        Structure.super.clear();
        this.$value.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.$value.containsKey(key) || Structure.super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.$value.containsValue(value) || Structure.super.containsValue(value);
    }

    @Override
    public <VALUE> VALUE get(Object key) {
        VALUE value = Structure.super.get(key);
        return value == null ? (VALUE) this.$value.get(key) : value;
    }

    @Override
    public Map<Object, Object> map() {
        Map<Object, Object> map = new HashMap<>();
        map.putAll(this.$value); //avoiding unwanted updates
        map.putAll(Structure.super.map()); //to make sure the values in the fields overrides the values in the secondary container
        return map;
    }

    @Override
    public Object put(Object key, Object value) {
        //pass up to parent's containers
        //and get the value case one of the
        //parents have changed it to match its
        //conditions
        value = Structure.super.put(key, value);

        this.$value.put(key, value);
        return value;
    }

    @Override
    public void remove(Object key) {
        Structure.super.remove(key);
        this.$value.remove(key);
    }

    @Override
    public void reset() {
        this.$value.clear();
        Structure.super.reset();
    }

    @Override
    public void shiftin() {
        this.$value.forEach(Structure.super::put);
    }

    @Override
    public void shiftout() {
        this.$value.putAll(Structure.super.map());
    }

    @Override
    public Class typeOf(Object key) {
        Class klass = Structure.super.typeOf(key);

        if (klass == null) {
            Object object = this.$value.get(key);
            klass = object == null ? null : object.getClass();
        }

        return klass;
    }

    @Override
    public String toString() {
        return this.map().toString();
    }

}
