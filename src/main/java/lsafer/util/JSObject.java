/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package lsafer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An interface defines that the targeted class can be used as a JSObject.
 * Basically JSObject means that the class can be working like a {@link Map}.
 * And every field of it (that don't matches the conditions of {@link #isTransient(Field)})
 * will be used as an {@link Map.Entry entry}.
 * <br><br>
 * If you want your JSObject to store {@link Entry entries} Even if there is no {@link Field} to contain it.
 * Then please add a transient field and link it with this by overriding the method {@link #entries()} of your JSObject.
 *
 * <ul>
 * <li>
 * tip: every field that matches the conditions of {@link #isTransient(Field)} will not be used as an {@link Map.Entry}.
 * </li>
 * <li>
 * tip: You can {@link #put(Object, Object) put} some objects with a type different than the targeted field's type.
 * Depending on {@link #caster() caster} of this JSObject.
 * </li>
 * <li>
 * tip: declaring fields in the constructor of a super class will not work.
 * Because it'll be set to the value on the subclass after super class's constructor ends.
 * </li>
 * </ul>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 14 release (28-Sep-2019)
 * @since 06-Jul-19
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
@JSObject.Configurations
public interface JSObject<K, V> extends Map<K, V>, Configurable, Caster.User {
	@Override
	default void clear() {
		this.entrySet().forEach(entry -> ((Entry<K, V>) entry).remove());
	}

	@Override
	default boolean containsKey(Object key) {
		return this.getEntry(key).exist();
	}

	@Override
	default boolean containsValue(Object value) {
		for (Map.Entry<K, V> entry : this.entrySet())
			if (entry.getValue().equals(value))
				return true;
		return false;
	}

	@Override
	default Set<Map.Entry<K, V>> entrySet() {
		Map<K, Entry<K, V>> entries = this.entries();
		Set<Map.Entry<K, V>> set = entries == null ? new HashSet<>() : new HashSet<>(entries.values());

		for (Field field : this.getClass().getFields())
			if (!this.isTransient(field)) {
				K key = this.getKey(field);

				set.add(entries == null ? new Entry<>(this, null, field, key) :
						entries.computeIfAbsent(key, k -> new Entry<>(this, entries, field, key)));
			}

		return set;
	}

	@Override
	default V get(Object key) {
		return this.getEntry(key).getValue();
	}

	@Override
	default boolean isEmpty() {
		return this.keySet().isEmpty();
	}

	@Override
	default Set<K> keySet() {
		Map<K, Entry<K, V>> entries = this.entries();
		Set<K> set = entries == null ? new HashSet<>() : new HashSet<>(entries.keySet());

		for (Field field : this.getClass().getFields())
			if (!this.isTransient(field))
				set.add(this.getKey(field));

		return set;
	}

	@Override
	default V put(K key, V value) {
		return this.getEntry(key).setValue(value);
	}

	@Override
	default void putAll(Map<? extends K, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	default V remove(Object key) {
		return this.getEntry(key).remove();
	}

	@Override
	default int size() {
		return this.keySet().size();
	}

	@Override
	default Collection<V> values() {
		List<V> collection = new ArrayList<>();

		this.entrySet().forEach(entry -> collection.add(entry.getValue()));

		return collection;
	}

	/**
	 * Get the extra-entries container. Needed because the JSObject can't handle
	 * those entries. That it have no fields to carry them. Or null if not needed
	 *
	 * @return the extra-entries container. Or null if not needed
	 */
	default Map<K, Entry<K, V>> entries() {
		return null;
	}

	/**
	 * Get the entry associated with the passed key.
	 * Or create a brand new one if there is not instance
	 * for it.
	 *
	 * @param key associated with the targeted entry
	 * @return an entry associated with the passed key.
	 */
	default Entry<K, V> getEntry(Object key) {
		Map<K, Entry<K, V>> entries = this.entries();
		Entry<K, V> entry = entries != null ? entries.get(key) : null;

		return entry == null ? new Entry<>(this, entries, this.getField(key), (K) key) : entry;
	}

	/**
	 * Get a field that is suppose to be an entry for the passed key. Or null if not found.
	 *
	 * @param key to get the field for
	 * @return a field that responsible on storing value for the presented key
	 */
	default Field getField(Object key) {
		if (key instanceof String)
			try {
				Field field = this.getClass().getField((String) key);
				return this.isTransient(field) ? null : field;
			} catch (NoSuchFieldException ignored) {
			}
		else if (key instanceof Integer)
			try {
				Field field = this.getClass().getField(this.configurations(Configurations.class, JSObject.class).indexer() + key);
				return this.isTransient(field) ? null : field;
			} catch (NoSuchFieldException ignored) {
			}

		return null;
	}

	/**
	 * Get the key that the given field is associated to.
	 *
	 * @param field to get the key of
	 * @return the key of the passed field
	 */
	default K getKey(Field field) {
		String name = field.getName();
		String indexKeyword = this.configurations(Configurations.class, JSObject.class).indexer();
		String[] split = name.split(indexKeyword);

		if (split.length == 2)
			try {
				return (K) Integer.valueOf(split[1]);
			} catch (NumberFormatException ignored) {
			}

		return (K) name;
	}

	/**
	 * Get whether the passed field is transient or not. So if it's so.
	 * Then it shouldn't be used as an entry container.
	 *
	 * @param field to be checked
	 * @return whether the passed field is transient or not
	 */
	default boolean isTransient(Field field) {
		int modifier = field.getModifiers();
		return Modifier.isPrivate(modifier) ||
			   Modifier.isProtected(modifier) ||
			   Modifier.isTransient(modifier) ||
			   field.isAnnotationPresent(Transient.class) ?
			   field.getAnnotation(Transient.class).value() :
			   this.configurations(Configurations.class, JSObject.class).restricted() ||
			   Strings.any(field.getName(), "serialVersionUID", "$assertionsDisabled");
	}

	/**
	 * A runtime annotation. Targets {@link JSObject structures}. And sets the configurations of it's targeted JSObject.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.TYPE_USE})
	@Inherited
	@interface Configurations {
		/**
		 * The keyword that defines that. The field with that keyword in the start of it's name.
		 * Is an Index field and it should be used as an list element holder.
		 * <br><br>
		 * note: the field name that starts with the keyword. SHOULD have an Integer name (excluding the keyword).
		 * <br><b>example:</b>
		 * <pre>
		 *     If: index=3 & keyword="i"
		 *     Then: field_name="i3"
		 * </pre>
		 *
		 * @return the keyword used to define an Index field
		 */
		String indexer() default "i";

		/**
		 * Defines whether the JSObject should ignore any field that is not annotated with {@link Transient} annotation.
		 *
		 * @return whether the JSObject should ignored unannotated field
		 */
		boolean restricted() default true;
	}

	/**
	 * Defines whether the annotated field is transient or not.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Transient {
		/**
		 * True if the field is transient. Or false if it's an entry container.
		 *
		 * @return whether the annotated field is transient or not
		 */
		boolean value();
	}

	/**
	 * An object to manage entries in the JSObject. The entry is the responsible
	 * for (remove, set, get) methods. And it's the one manages the appliance of
	 * operations for the it's targeted key for both field and map containers.
	 *
	 * @param <K> the type of key maintained by this entry
	 * @param <V> the type of mapped value
	 */
	final class Entry<K, V> implements java.util.Map.Entry<K, V> {
		/**
		 * A reference of the map where all entries of the JSObject this entry belongs to is contained on.
		 */
		public Map<K, Entry<K, V>> entries;

		/**
		 * The field where this entry is linked to in the JSObject this entry belongs to.
		 */
		public Field field;

		/**
		 * The key represented by this entry.
		 */
		public K key;

		/**
		 * The field where this entry is belongs to.
		 */
		public JSObject<K, V> object;

		/**
		 * The value represented by this entry.
		 */
		public V value;

		/**
		 * Initialize this.
		 * TODO more description
		 *
		 * @param object the JSObject that this entry belongs to
		 * @param entries   a reference to the entries map instance of the JSObject that this entry belongs to (null if there is no such instance)
		 * @param field     the field where this entry is linked to (null if there is no such field)
		 * @param key       the key represented by this entry
		 */
		private Entry(JSObject<K, V> object, Map<K, Entry<K, V>> entries, Field field, K key) {
			this.object = object;
			this.key = key;
			this.entries = entries;
			this.field = field;
		}

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			if (this.field != null)
				try {
					return (V) this.field.get(this.object);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			return this.value;
		}

		@Override
		public V setValue(V value) {
			V old = this.getValue();

			if (this.entries != null)
				this.entries.put(this.key, this);

			if (this.field != null)
				if (this.field.getType().isInstance(value)) {
					try {
						this.field.setAccessible(true);
						this.field.set(this.object, value);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					this.object.put(this.key, (V) this.object.caster().cast(this.field.getType(), value));
					return old;
				}

			this.value = value;
			return old;
		}

		/**
		 * Check if this entry is registered to a field. Or an entries-set.
		 *
		 * @return whether this entry is registered to a field or an entries-set
		 */
		public boolean exist() {
			return this.field != null || (this.entries != null && this.entries.containsKey(this.key));
		}

		/**
		 * Remove this entry from the JSObject where it's belongs to.
		 * By removing it from the {@link #entries entries object} in the linked
		 * JSObject (If the object is not null). Or setting the {@link #value value of this}
		 * to null. If this entry is linked to any {@link #field}.
		 *
		 * @return the previous value associated with this.
		 */
		public V remove() {
			V old = this.getValue();

			if (this.field == null) {
				if (this.entries != null)
					this.entries.remove(this.key, this);
			} else try {
				this.field.setAccessible(true);
				this.field.set(this, null);
				this.value = null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return old;
		}
	}
}
