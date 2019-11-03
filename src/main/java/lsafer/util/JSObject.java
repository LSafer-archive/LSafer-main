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

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * An interface defines that the targeted class can be used as a JSObject. Basically JSObject means that the class can be working like a {@link Map}.
 * And every field of it (that don't matches the conditions of {@link #istransient(Field)}) will be used as an {@link Map.Entry entry}.
 * <br><br>
 * If you want your JSObject to store {@link Entry entries} Even if there is no {@link Field} to contain it. Then please add a transient field and
 * link it with this by overriding the method {@link #entries()} of your JSObject.
 *
 * <ul>
 * <li>
 * tip: every field that matches the conditions of {@link #istransient(Field)} will not be used as an {@link Map.Entry}.
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
@JSObject.Configurations
public interface JSObject<K, V> extends Map<K, V>, Configurable, Caster.User {
	@Override
	default int size() {
		return this.keySet().size();
	}

	@Override
	default boolean isEmpty() {
		return this.keySet().isEmpty();
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
	default V get(Object key) {
		return this.getEntry(key).getValue();
	}

	@Override
	default V put(K key, V value) {
		return this.getEntry(key).setValue(value);
	}

	@Override
	default V remove(Object key) {
		return this.getEntry(key).remove();
	}

	@Override
	default void putAll(Map<? extends K, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	default void clear() {
		this.entrySet().forEach(entry -> ((Entry<K, V>) entry).remove());
	}

	@Override
	default Set<K> keySet() {
		Map<K, Entry<K, V>> entries = this.entries();
		Set<K> set = entries == null ? new HashSet<>() : new HashSet<>(entries.keySet());

		for (Field field : this.getClass().getFields())
			if (!this.istransient(field))
				set.add(this.getKey(field));

		return set;
	}

	@Override
	default Collection<V> values() {
		List<V> collection = new ArrayList<>();

		//noinspection Java8MapForEach key not used
		this.entrySet().forEach(entry -> collection.add(entry.getValue()));

		return collection;
	}

	@Override
	default Set<Map.Entry<K, V>> entrySet() {
		Map<K, Entry<K, V>> entries = this.entries();
		Set<Map.Entry<K, V>> set = entries == null ? new HashSet<>() : new HashSet<>(entries.values());

		if (entries == null) {
			for (Field field : this.getClass().getFields())
				if (!this.istransient(field))
					set.add(new Entry<>(this, null, field, this.getKey(field)));
		} else {
			for (Field field : this.getClass().getFields())
				if (!this.istransient(field))
					set.add(entries.computeIfAbsent(this.getKey(field), k -> new Entry<>(this, entries, field, k)));
		}

		return set;
	}

	/**
	 * Get the extra-entries container. Needed because the JSObject can't handle those entries. That it have no fields to carry them. Or null if not
	 * needed
	 *
	 * @return the extra-entries container. Or null if not needed
	 */
	default Map<K, Entry<K, V>> entries() {
		return null;
	}

	/**
	 * Get the entry associated with the passed key. Or create a brand new one if there is no instance for it.
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
		Configurations configurations = this.configurations(Configurations.class, JSObject.class);

		if (key instanceof String || key instanceof Integer)
			try {
				Field field = this.getClass().getField(key instanceof Integer ? configurations.indexer() + key : (String) key);
				return this.istransient(field) ? null : field;
			} catch (NoSuchFieldException ignored) {
				if (configurations.overridableKeys())
					for (Field field : this.getClass().getFields())
						if (this.istransient(field) &&
							field.isAnnotationPresent(EntryField.class) &&
							field.getAnnotation(EntryField.class).key().equals(key))
							return field;
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
		EntryField annotation = field.getAnnotation(EntryField.class);

		if (annotation != null && !annotation.key().equals("")) {
			return (K) annotation.key();
		} else {
			String name = field.getName();
			String[] split = name.split(this.configurations(Configurations.class, JSObject.class).indexer());

			if (split.length == 2)
				try {
					return (K) Integer.valueOf(split[1]);
				} catch (NumberFormatException ignored) {
				}

			return (K) name;
		}
	}

	/**
	 * Get whether the passed field is transient or not. So if it's so. Then it shouldn't be used as an entry container.
	 *
	 * @param field to be checked
	 * @return whether the passed field is transient or not
	 */
	default boolean istransient(Field field) {
		int modifier = field.getModifiers();
		return field.isAnnotationPresent(EntryField.class) ?
			   field.getAnnotation(EntryField.class).istransient() :
			   this.configurations(Configurations.class, JSObject.class).restricted() ||
			   Modifier.isPrivate(modifier) ||
			   Modifier.isProtected(modifier) ||
			   Modifier.isTransient(modifier);
	}

	/**
	 * A runtime annotation. Sets the configurations of it's targeted JSObject.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.TYPE_USE})
	@Inherited
	@interface Configurations {
		/**
		 * The word that if it found on the start of a name of a field. Then the field's name is an Integer (without that word).
		 * <br><br>
		 * note: the field name that starts with the keyword. SHOULD have an Integer name (excluding the keyword).
		 * <br><b>example:</b>
		 * <pre>
		 *     If: name=3 & indexer="i"
		 *     Then: field_name="i3"
		 * </pre>
		 *
		 * @return the keyword used to define an Integer field name
		 */
		String indexer() default "i";

		/**
		 * Get whether the annotated {@link JSObject}'s entry-fields names can be overridden using {@link EntryField#key()}.
		 *
		 * @return whether the fields names can be overridden or not
		 */
		boolean overridableKeys() default false;

		/**
		 * Since we are using fields to store some of the entries. We can't remove an entry associated with a field. So even when {@link
		 * #remove(Object)} get called. We can't remove that entry. So this configuration determine if the {@link Entry entry} should be set to null
		 * (instead of trying to remove it). Or just ignore the call.
		 * <p>
		 * This will be overridden by fields annotated with {@link EntryField}
		 *
		 * @return whether the entry (associated to a field) should be set to null (when remove get called) or ignored.
		 */
		boolean removable() default true;

		/**
		 * Defines whether the JSObject should ignore any field that is not annotated with {@link EntryField} annotation.
		 *
		 * @return whether the JSObject should ignored unannotated field
		 */
		boolean restricted() default true;
	}

	/**
	 * Defines whether the annotated field is an entry-field or not.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface EntryField {
		/**
		 * True if the field is an entry container. Or false if it's transient.
		 * <p>
		 * Note: this will override the any other modifiers (like it never exist) Also Note: This will be set automatically as soon as you annotate a
		 * field
		 *
		 * @return whether the annotated field is an entry-field or not
		 */
		boolean istransient() default false;

		/**
		 * The key of the annotated entry-field. This will override the default key.
		 * <p>
		 * Note: to use this you have to set {@link Configurations#overridableKeys()} true to your {@link JSObject}'s configurations.
		 *
		 * @return the key of the annotated entry-field
		 */
		String key() default "";

		/**
		 * Whether the annotated field is allowed to be set to null or not. This will only affect when trying to call the {@link #remove} on it.
		 *
		 * @return whether the annotated field is nullable or not
		 */
		boolean removable() default true;
	}

	/**
	 * An object to manage entries in the JSObject. The entry is the responsible for (remove, set, get) methods. And it's the one manages the
	 * appliance of operations for the it's targeted key for both field and map containers.
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
		 * Initialize this. TODO more description
		 *
		 * @param object  the JSObject that this entry belongs to
		 * @param entries a reference to the entries map instance of the JSObject that this entry belongs to (null if there is no such instance)
		 * @param field   the field where this entry is linked to (null if there is no such field)
		 * @param key     the key represented by this entry
		 */
		public Entry(JSObject<K, V> object, Map<K, Entry<K, V>> entries, Field field, K key) {
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

			if (this.field != null) {
				Class<V> type = (Class<V>) this.field.getType();

				if (type.isPrimitive() && value == null) {
					new Throwable(this.field + " with primitive type can't be set to null").printStackTrace();
					return old;
				} else if (!type.isInstance(value)) {
					this.object.put(this.key, this.object.caster().cast(type, value));
					return old;
				} else {
					try {
						this.field.setAccessible(true);
						this.field.set(this.object, value);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
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
		 * Remove this entry from the JSObject where it's belongs to. By removing it from the {@link #entries entries object} in the linked JSObject
		 * (If the object is not null). Or setting the {@link #value value of this} to null. If this entry is linked to any {@link #field}.
		 *
		 * @return the previous value associated with this.
		 */
		public V remove() {
			V old = this.getValue();

			if (this.field == null) {
				if (this.entries != null)
					this.entries.remove(this.key, this);
			} else if (this.field.getType().isPrimitive()) {
				new Throwable(this.field + " with primitive type can't be removed (can't be set to null)").printStackTrace();
				return old;
			} else {
				EntryField annotation = this.field.getAnnotation(EntryField.class);
				Configurations configurations = this.object.configurations(Configurations.class, JSObject.class);

				if ((annotation != null && annotation.removable()) || (configurations.restricted() && configurations.removable()))
					try {
						this.field.setAccessible(true);
						this.field.set(this.object, null);
						this.value = null;
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
			}

			return old;
		}
	}
}
