package lsafer.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A structure linked with {@link Map}. As it's secondary container. And it acts like it's a list.
 * <br>
 * TODO more description
 *
 * @author LSaferSE
 * @version 3 alpha (06-Sep-19)
 * @since 19-Aug-19
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ArrayStructure extends HashStructure {
    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key instanceof Integer ? "$" + key : key);
    }

    @Override
    public <T> T get(Object key) {
        return super.get(key instanceof Integer ? "$" + key : key);
    }

    @Override
    public Set<Object> keySet() {
        Set<Object> set = new HashSet<>(super.keySet());
        Set<Object> set1 = new HashSet<>();

        set.forEach(key -> {
            if (key instanceof String)
                try {
                    set1.add(Integer.valueOf(((String) key).split("[$]")[1]));
                    return;
                } catch (NumberFormatException ignored) {
                } catch (IndexOutOfBoundsException ignored) {
                }

            set1.add(key);
        });

        return set1;
    }

    @Override
    public <K, V> Map<K, V> map() {
        Map<K, V> map = new HashMap<>();

        super.<K, V>map().forEach((key, value) -> {
            if (key instanceof String)
                try {
                    map.put((K) Integer.valueOf(((String) key).split("[$]")[1]), value);
                    return;
                } catch (NumberFormatException ignored) {
                } catch (IndexOutOfBoundsException ignored) {
                }

            map.put(key, value);
        });

        return map;
    }

    @Override
    public <V> V put(Object key, V value) {
        return super.put(key instanceof Integer ? "$" + key : key, value);
    }

    @Override
    public boolean remove(Object key) {
        return super.remove(key instanceof Integer ? "$" + key : key);
    }

    @Override
    public String toString() {
        return this.list().toString();
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <ul>
     * <li>uses: {@link #list()}.</li>
     * </ul>
     *
     * @param value to be appended in the end of this list
     * @param <V>   type of the added value
     * @return the actual value that have been added
     */
    public <V> V add(V value) {
        return this.put(this.list().size(), value);
    }

    /**
     * Appends all of the elements in the specified collection to the end of this list.
     *
     * <ul>
     * <li>uses: heavy {@link Collection#forEach(Consumer)}.</li>
     * <li>uses: repetitive {@link #add(Object)}.</li>
     * </ul>
     *
     * @param collection to be appended to the end of this list
     * @param <A>        this
     * @return this
     */
    public <A extends ArrayStructure> A addAll(Collection<?> collection) {
        collection.forEach(this::add);
        return (A) this;
    }

    /**
     * Appends all of the elements in the specified array to the end of this list.
     *
     * <ul>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #add(Object)}.</li>
     * </ul>
     *
     * @param objects to be appended to the end of this list
     * @param <A>     this
     * @return this
     */
    public <A extends ArrayStructure> A addAll(Object[] objects) {
        for (Object object : objects)
            this.add(object);

        return (A) this;
    }

    /**
     * Appends all of the elements in the specified array-structure to the end of this list.
     *
     * <ul>
     * <li>uses: {@link #list()}.</li>
     * <li>uses: {@link #addAll(Collection)}.</li>
     * </ul>
     *
     * @param structure to be appended to the end of this list
     * @param <A>       this
     * @return this
     */
    public <A extends ArrayStructure> A addAll(ArrayStructure structure) {
        return this.addAll(structure.list());
    }

    /**
     * Returns an array containing all of the elements in this list in proper sequence (from first to last element).
     * The returned array will be "safe" in that no references to it are maintained by this list.
     * (In other words, this method must allocate a new array).
     * The caller is thus free to modify the returned array.
     *
     * <ul>
     * <li>uses: {@link #list()}.</li>
     * </ul>
     *
     * @param <T> the assumed type of the array
     * @return an array containing all of the elements in this list in proper sequence
     */
    public <T> T[] array() {
        return (T[]) this.list().toArray();
    }

    /**
     * Returns an array containing all of the elements in this list in proper sequence (from first to last element).
     * All but elements not extends the given class.
     * The returned array will be "safe" in that no references to it are maintained by this list.
     * (In other words, this method must allocate a new array).
     * The caller is thus free to modify the returned array.
     *
     * <ul>
     * <li>uses: {@link #list(Class)}.</li>
     * </ul>
     *
     * @param <T>   The assumed type of the array
     * @param klass to filter any element not extending it
     * @return An array containing all of the elements in this list in proper sequence
     */
    public <T> T[] array(Class<T> klass) {
        return this.list(klass).toArray((T[]) Array.newInstance(klass));
    }

    /**
     * Returns a list containing all of the elements in this in proper sequence (from first to last element).
     * The returned list will be "safe" in that no references to it are maintained by this.
     * (In other words, this method must allocate a new list).
     * The caller is thus free to modify the returned list.
     *
     * <ul>
     * <li>uses: {@link #map()}.</li>
     * <li>uses: heavy {@link Map#forEach(BiConsumer)}.</li>
     * </ul>
     *
     * @param <E> the assumed type of the list
     * @return a list containing all of the elements in this in proper sequence
     */
    public <E> List<E> list() {
        List<E> list = new ArrayList<>();

        super.map().forEach((index_string, element) -> {
            if (index_string instanceof String)
                try {
                    int index = Integer.valueOf(((String) index_string).split("[$]")[1]);

                    if (index >= list.size())
                        Arrays.fill(list, index + 1, () -> null);

                    list.set(index, (E) element);
                } catch (NumberFormatException ignored) {
                } catch (IndexOutOfBoundsException ignored) {
                }
        });

        return list;
    }

    /**
     * Returns a list containing all of the elements in this list in proper sequence (from first to last element).
     * All but elements not extends the given class.
     * The returned list will be "safe" in that no references to it are maintained by this.
     * (In other words, this method must allocate a new list).
     * The caller is thus free to modify the returned list.
     *
     * <ul>
     * <li>uses: {@link #list()}.</li>
     * <li>uses: heavy {@link List#forEach(Consumer)}.</li>
     * </ul>
     *
     * @param <E>   The assumed type of the list
     * @param klass to filter any element not extending it
     * @return A list containing all of the elements in this list in proper sequence
     */
    public <E> List<E> list(Class<E> klass) {
        List<E> list = new ArrayList<>();

        this.list().forEach(element -> {
            if (klass.isInstance(element))
                list.add((E) element);
        });

        return list;
    }

    /**
     * Put all the entries ("elements") to this. Using it's indexes as it's "keys". And it itself as it's "values".
     *
     * <ul>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #put(Object, Object)}.</li>
     * </ul>
     *
     * @param list to be put in this
     * @param <A>  this
     * @return this
     */
    public <A extends ArrayStructure> A putAll(List<?> list) {
        for (int i = 0; i < list.size(); i++)
            this.put(i, list.get(i));

        return (A) this;
    }

    /**
     * Put all the entries ("elements") to this. Using it's indexes as it's "keys". And it itself as it's "values".
     *
     * <ul>
     * <li>uses: heavy foreach.</li>
     * <li>uses: repetitive {@link #put(Object, Object)}.</li>
     * </ul>
     *
     * @param objects to be put in this
     * @param <A>     this
     * @return this
     */
    public <A extends ArrayStructure> A putAll(Object[] objects) {
        for (int i = 0; i < objects.length; i++)
            this.put(i, objects[i]);

        return (A) this;
    }

    /**
     * Removes the element at the specified index in this structure "by it's index".
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * <ul>
     * <li>uses: {@link #list()}.</li>
     * <li>uses: {@link #putAll(List)}.</li>
     * </ul>
     *
     * @param index to be removed
     * @param <T>   the assumed type of the old value
     * @return the element previously at the specified position
     */
    public <T> T removeIndex(int index) {
        List list = this.list();

        if (index > 0 || index < list.size()) {
            T old = (T) list.remove(index);

            this.putAll(list);
            this.remove(list.size());

            return old;
        }

        return null;
    }
}
