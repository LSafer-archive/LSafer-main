package lsafer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author LSaferSE
 * @version 1 alpha (19-Aug-19)
 * @since 19-Aug-19
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class ArrayStructure implements Structure {

    /**
     *
     */
    final protected transient List<Object> value = new ArrayList<>();

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
        if (key instanceof Integer) {
            return this.value.size() > (Integer) key || Structure.super.containsKey("$" + key);
        } else {
            return Structure.super.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.contains(value) || Structure.super.containsValue(value);
    }

    @Override
    public <S extends Structure> S forEach(BiConsumer<?, ?> consumer) {
        List list = this.list();
        for (int i = 0; i < list.size(); i++)
            ((BiConsumer<Object, Object>) consumer).accept(i, list.get(i));

        return (S) this;
    }

    @Override
    public <T> T get(Object key) {
        if (key instanceof Integer) {
            Object object = Structure.super.get("$" + key);
            return (T) (object == null && this.value.size() > (Integer) key ? this.value.get((Integer) key) : object);
        } else {
            return (T) Structure.super.get(key);
        }
    }

    @Override
    public Set<Object> keySet() {
        Set<Object> set = new HashSet<>();

        for (int i = 0; i < value.size(); i++)
            set.add(i);

        return set;
    }

    @Override
    public Map<Object, Object> map() {
        Map<Object, Object> map = new HashMap<>();

        for (int i = 0; i < this.value.size(); i++)
            map.put(i, this.value.get(i));

        Structure.super.map().forEach((key, value) -> {
            int index = Integer.valueOf(((String) key).replace("$", ""));
            map.put(index, value);
        });

        return map;
    }

    @Override
    public <V> V put(Object key, V value) {
        if (key instanceof Integer) {
            value = Structure.super.put("$" + key, value);

            if (this.value.size() <= ((Integer) key))
                Arrays.fill(this.value, ((Integer) key) + 1, () -> null);

            this.value.set((Integer) key, value);

            return (V) value;
        } else {
            return Structure.super.put(key, value);
        }
    }

    @Override
    public void remove(Object key) {
        int index = (Integer) key;

        Structure.super.remove("$" + index);
        this.value.remove(index);
    }

    @Override
    public <S extends Structure> S reset() {
        this.value.clear();
        Structure.super.reset();
        return (S) this;
    }

    @Override
    public int size() {
        return this.overrideList().value.size();
    }

    @Override
    public Collection<Object> values() {
        return new ArrayList<>(this.value);
    }

    @Override
    public String toString() {
        return this.list().toString();
    }

    /**
     *
     */
    public <V> V add(V value) {
        int index = this.size();
        return this.put(index, value);
    }

    /**
     * @param <A>
     * @return
     */
    public <A extends ArrayStructure> A addAll(Object[] array) {
        for (Object object : array)
            this.add(object);

        return (A) this;
    }

    /**
     * @param <A>
     * @return
     */
    public <A extends ArrayStructure> A addAll(Collection<?> collection) {
        collection.forEach(this::add);

        return (A) this;
    }

    /**
     * @return
     */
    public Object[] array() {
        Object[] array = this.value.toArray();

        for (Map.Entry<Object, Object> entry : Structure.super.map().entrySet()) {
            int index = Integer.valueOf(((String) entry.getKey()).replace("$", ""));
            Object value = entry.getValue();

            if (array.length > index)
                array = Arrays.fill(array, index + 1, () -> null);

            array[index] = value;
        }

        return array;
    }

    /**
     * @return
     */
    public List<Object> list() {
        List<Object> list = new ArrayList<>(this.value);

        Structure.super.map().forEach((key, value) -> {
            if (value != null) {
                int index = Integer.valueOf(((String) key).replace("$", ""));

                if (list.size() <= index)
                    Arrays.fill(list, index + 1, () -> null);

                list.set(index, value);
            }
        });

        return list;
    }

    /**
     * put all nodes from the {@link #value map secondary container} to the fields inside this.
     *
     * @param <A> this
     * @return this
     */
    public <A extends ArrayStructure> A overrideFromList() {
        for (int i = 0; i < this.value.size(); i++) {
            Object object = this.value.get(i);
            Object stored = Structure.super.put("$" + i, object);
            if (object != stored)
                this.value.set(i, stored);
        }

        return (A) this;
    }

    /**
     * put all node from the fields inside this to the {@link #value map secondary container}.
     *
     * @param <A> this
     * @return this
     */
    public <A extends ArrayStructure> A overrideList() {
        Structure.super.map().forEach((key, value) -> {
            if (value != null) {
                int index = Integer.valueOf(((String) key).replace("$", ""));

                if (this.value.size() <= index)
                    Arrays.fill(this.value, index + 1, () -> null);

                this.value.set(index, value);
            }
        });


        return (A) this;
    }

    /**
     * @param <A>
     * @return
     */
    public <A extends ArrayStructure> A putAll(Object[] array) {
        for (int i = 0; i < array.length; i++)
            this.put(i, array[i]);

        return (A) this;
    }

    /**
     * @param <A>
     * @return
     */
    public <A extends ArrayStructure> A putAll(List list) {
        for (int i = 0; i < list.size(); i++)
            this.put(i, list.get(i));

        return (A) this;
    }

    /**
     * @param <A>
     * @return
     */
    public <A extends ArrayStructure> A removeAll(Object[] array) {
        for (Object object : array)
            this.removeValue(object);

        return (A) this;
    }

    /**
     * @param <A>
     * @return
     */
    public <A extends ArrayStructure> A removeAll(Collection<?> collection) {
        collection.forEach(this::removeValue);

        return (A) this;
    }

    /**
     * @param value
     */
    public void removeValue(Object value) {
        this.value.remove(value);
        this.overrideFromList();
    }

    //    /**
//     *
//     */
//    public <A extends ArrayStructure> A fix(){
//        List<Object> list = this.list();
//
//        while (list.size() != 0 && list.get(list.size()-1) == null)
//            list.remove(list.size()-1);
//
//        this.clear();
//        this.putAll(list);
//        return (A) this;
//    }
}
