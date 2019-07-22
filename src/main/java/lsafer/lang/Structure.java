package lsafer.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * structurable with a {@link Map} as a secondary container.
 * <p>
 * make sure your {@link Structure structure} matches all {@link Structurable structurables} rules
 *
 * @author LSafer
 * @version 7 release (19-Jul-2019)
 * @since 11 Jun 2019
 **/
@SuppressWarnings({"WeakerAccess"})
public class Structure implements Structurable {

    /**
     * secondary container.
     */
    final protected Map<Object, Object> $value = new HashMap<>();

    /**
     * to unify all Structures constructors.
     *
     * @param arguments if subclasses needs
     */
    public Structure(Object... arguments) {
    }

    @Override
    public void clean() {
        Structurable.super.clean();
        this.$value.clear();
    }

    @Override
    public void clear() {
        Structurable.super.clear();
        this.$value.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.$value.containsKey(key) || Structurable.super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.$value.containsValue(value) || Structurable.super.containsValue(value);
    }

    @Override
    public <VALUE> VALUE get(Object key) {
        VALUE value = Structurable.super.get(key);

        if (value == null)
            try {
                return (VALUE) this.$value.get(key);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        return value;
    }

    @Override
    public Map<Object, Object> map() {
        Map<Object, Object> map = new HashMap<>();
        map.putAll(this.$value); //avoiding unwanted updates
        map.putAll(Structurable.super.map()); //to make sure the values in the fields overrides the values in the secondary container
        return map;
    }

    @Override
    public Object put(Object key, Object value) {
        value = Structurable.super.put(key, value);

        this.$value.put(key, value);
        return value;
    }

    @Override
    public void remove(Object key) {
        Structurable.super.remove(key);
        this.$value.remove(key);
    }

    @Override
    public void reset() {
        this.$value.clear();
        Structurable.super.reset();
    }

    @Override
    public void shiftin() {
        this.$value.forEach(Structurable.super::put);
    }

    @Override
    public void shiftout() {
        this.$value.putAll(Structurable.super.map());
    }

    @Override
    public String toString() {
        return this.map().toString();
    }

}
