package lsafer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * structure with a {@link Map} as a secondary container.
 * <p>
 * make sure your {@link HashStructure structure} matches all {@link Structure structurables} rules
 *
 * @author LSafer
 * @version 9 release (19-Jul-2019)
 * @since 11 Jun 2019
 **/
@SuppressWarnings("WeakerAccess")
public class HashStructure implements Structure {

    /**
     * secondary container.
     */
    final transient protected Map<Object, Object> value = new HashMap<>();

    @Override
    public <S extends Structure> S clean() {
        Structure.super.clean();
        this.value.clear();
        return (S) this;
    }

    @Override
    public <S extends Structure> S clear() {
        Structure.super.clear();
        this.value.clear();
        return (S) this;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.value.containsKey(key) || Structure.super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value) || Structure.super.containsValue(value);
    }

    @Override
    public <T> T get(Object key) {
        T value = Structure.super.get(key);
        return value == null ? (T) this.value.get(key) : value;
    }

    @Override
    public Set<Object> keySet() {
        return this.value.keySet();
    }

    @Override
    public Map<Object, Object> map() {
        //make sure the values in the fields overrides the values in the secondary container
        Map<Object, Object> map = new HashMap<>(this.value);
        map.putAll(Structure.super.map());
        return map;
    }

    @Override
    public <V> V put(Object key, V value) {
        value = Structure.super.put(key, value);
        this.value.put(key, value);

        return (V) value;
    }

    @Override
    public void remove(Object key) {
        Structure.super.remove(key);
        this.value.remove(key);
    }

    @Override
    public <S extends Structure> S reset() {
        this.value.clear();
        Structure.super.reset();
        return (S) this;
    }

    @Override
    public int size() {
        return this.overrideMap().value.size();
    }

    @Override
    public Collection<Object> values() {
        return this.value.values();
    }

    @Override
    public String toString() {
        return this.map().toString();
    }

    /**
     * put all nodes from the {@link #value map secondary container} to the fields inside this.
     *
     * @param <H> this
     * @return this
     */
    public <H extends HashStructure> H overrideFromMap() {
        this.value.forEach(Structure.super::put);
        return (H) this;
    }

    /**
     * put all node from the fields inside this to the {@link #value map secondary container}.
     *
     * @param <H> this
     * @return this
     */
    public <H extends HashStructure> H overrideMap() {
        this.value.putAll(Structure.super.map());
        return (H) this;
    }

}
