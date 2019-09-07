package lsafer.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * A structure with a {@link Map} as a secondary container.
 *
 * <ul>
 * <li>note: make sure your {@link HashStructure structure} matches all {@link Structure structurables} rules.</li>
 * </ul>
 *
 * @author LSafer
 * @version 10 release (06-Sep-2019)
 * @since 11 Jun 2019
 **/
public class HashStructure implements Structure {
    /**
     * The secondary container.
     */
    protected transient Map<Object, Object> value = new HashMap<>();

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
        Set<Object> set = new HashSet<>(this.value.keySet());
        set.addAll(Structure.super.keySet());
        return set;
    }

    @Override
    public <K, V> Map<K, V> map() {
        //make sure the values in the fields overrides the values in the secondary container
        Map<K, V> map = new HashMap<>((Map<K, V>) this.value);
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
    public boolean remove(Object key) {
        this.value.remove(key);
        return Structure.super.remove(key);
    }

    @Override
    public <S extends Structure> S reset() {
        this.value.clear();
        Structure.super.reset();
        return (S) this;
    }

    @Override
    public int size() {
        int size0 = Structure.super.size();
        int size1 = this.value.size();

        return size0 > size1 ? size0 : size1;
    }

    @Override
    public String toString() {
        return this.map().toString();
    }

    @Override
    public Collection<Object> values() {
        return this.map().values();
    }

    /**
     * Put all nodes from the {@link #value map secondary container} to the fields inside this.
     *
     * @param <H> this
     * @return this
     */
    public <H extends HashStructure> H overrideFromMap() {
        this.value.forEach(Structure.super::put);
        return (H) this;
    }

    /**
     * Put all node from the fields inside this to the {@link #value map secondary container}.
     *
     * @param <H> this
     * @return this
     */
    public <H extends HashStructure> H overrideMap() {
        this.value.putAll(Structure.super.map());
        return (H) this;
    }

    /**
     * Backdoor initializing method, or custom deserialization method.
     *
     * @param stream to initialize this using
     * @throws ClassNotFoundException if the class of a serialized object could not be found.
     * @throws IOException            if an I/O error occurs.
     */
    protected void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.value = (Map<Object, Object>) stream.readObject();
    }

    /**
     * Custom hash-structure serialization method.
     *
     * @param stream to use to serialize this
     * @throws IOException if an I/O error occurs
     */
    protected void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.value);
    }
}
