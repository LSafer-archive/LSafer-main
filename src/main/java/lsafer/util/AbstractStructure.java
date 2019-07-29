package lsafer.util;

import java.util.HashMap;
import java.util.Map;

/**
 * structure with a {@link Map} as a secondary container.
 * <p>
 * make sure your {@link AbstractStructure structure} matches all {@link Structure structurables} rules
 *
 * @author LSafer
 * @version 9 release (19-Jul-2019)
 * @since 11 Jun 2019
 **/
@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractStructure implements Structure {

    /**
     * secondary container.
     */
    final protected Map<Object, Object> $value = new HashMap<>();

    /**
     * to unify all Structures constructors.
     *
     * @param arguments if subclasses needs
     */
    public AbstractStructure(Object... arguments) {
    }

    @Override
    public void clean() {
        Structure.super.clean();
        this.$value.forEach((key, value) -> {
            if (!this.isIgnored(key))
                this.$value.remove(key);
        });
    }

    @Override
    public void clear() {
        Structure.super.clear();
        this.$value.forEach((key, value) -> {
            if (!this.isIgnored(key))
                this.$value.remove(key);
        });
    }

    @Override
    public boolean containsKey(Object key) {
        return !this.isIgnored(key) && (this.$value.containsKey(key) || Structure.super.containsKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        if (Structure.super.containsValue(value))
            return true;
        for (Map.Entry entry : this.$value.entrySet())
            if (!this.isIgnored(entry.getKey()) && entry.getValue().equals(value))
                return true;
        return false;
    }

    @Override
    public <T> T get(Object key) {
        if (!this.isIgnored(key)) {
            T value = Structure.super.get(key);
            return value == null ? (T) this.$value.get(key) : value;
        }

        return null;
    }

    @Override
    public Map<Object, Object> map() {
        //make sure the values in the fields overrides the values in the secondary container
        Map<Object, Object> map = new HashMap<>();
        this.$value.forEach((key, value) -> {
            if (!isIgnored(key))
                map.put(key, value);
        });
        map.putAll(Structure.super.map());
        return map;
    }

    @Override
    public Object put(Object key, Object value) {
        if (!this.isIgnored(key)) {
            value = Structure.super.put(key, value);
            this.$value.put(key, value);
        }

        return value;
    }

    @Override
    public void remove(Object key) {
        if (!this.isIgnored(key)) {
            Structure.super.remove(key);
            this.$value.remove(key);
        }
    }

    @Override
    public void reset() {
        this.$value.forEach((key, value) -> {
            if (!this.isIgnored(key))
                this.$value.remove(key);
        });
        Structure.super.reset();
    }

    @Override
    public Class typeOf(Object key) {
        if (!this.isIgnored(key)) {
            Class klass = Structure.super.typeOf(key);

            if (klass == null) {
                Object object = this.$value.get(key);
                klass = object == null ? null : object.getClass();
            }

            return klass;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.map().toString();
    }

    /**
     * put all nodes from the {@link #$value map secondary container} to the fields inside this.
     *
     * @param <A> this
     * @return this
     */
    public <A extends AbstractStructure> A overrideFromMap() {
        this.$value.forEach(Structure.super::put);
        return (A) this;
    }

    /**
     * put all node from the fields inside this to the {@link #$value map secondary container}.
     *
     * @param <A> this
     * @return this
     */
    public <A extends AbstractStructure> A overrideMap() {
        this.$value.putAll(Structure.super.map());
        return (A) this;
    }

}
