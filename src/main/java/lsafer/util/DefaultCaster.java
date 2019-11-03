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

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Default casting engine that supports this library.
 *
 * @author LSaferSE
 * @version 2 release (02-Nov-2019)
 * @since 31-Aug-19
 */
public class DefaultCaster extends Caster {
	/**
	 * The global instance to avoid unnecessary instancing.
	 */
	final public static DefaultCaster global = new DefaultCaster();

	/**
	 * Just like {@link #cast(Class, Object)} but in an array foreach element in the passed array.
	 *
	 * @param klass   the targeted array's class
	 * @param objects to be casted foreach inside a new instance of the targeted array
	 * @param <T>     the type of elements in the targeted array
	 * @return the given objects casted and stored on a new instance of the given array's class
	 */
	@Caster.CastingMethod(subs = true)
	public <T> T[] array2array(Class<? super T[]> klass, Object[] objects) {
		Class<T> type = (Class<T>) klass.getComponentType();
		T[] array = (T[]) Array.newInstance(type, objects.length);

		for (int i = 0; i < objects.length; i++)
			array[i] = objects[i] == null || type.isInstance(objects[i]) ? (T) objects[i] : this.cast(type, objects[i]);

		return array;
	}

	/**
	 * Get the value of the passed {@link Object Object[]} as a {@link Collection}.
	 *
	 * @param klass the class of the targeted collection
	 * @param array to be casted
	 * @param <C>   the type of the targeted collection
	 * @param <E>   type of elements inside the targeted collection
	 * @return the passed array as a collection
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true)
	public <C extends Collection<E>, E> C array2collection(Class<? super C> klass, E[] array) {
		try {
			C collection = (C) klass.getDeclaredConstructor(new Class[0]).newInstance();
			collection.addAll(java.util.Arrays.asList(array));
			return collection;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link Object Object[]} as the passed {@link Map map type}.
	 *
	 * @param klass of the targeted map type
	 * @param array to be casted
	 * @param <M>   targeted map type
	 * @param <V>   type of values inside the targeted map
	 * @return the passed array as a map
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true)
	public <M extends Map<? super Integer, V>, V> M array2map(Class<? super M> klass, V[] array) {
		try {
			M map = (M) klass.getDeclaredConstructor(new Class[0]).newInstance();

			for (int i = 0; i < array.length; i++)
				map.put(i, array[i]);

			return map;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link Collection} as the passed {@link Object[] array type}.
	 *
	 * @param klass      of the targeted map type
	 * @param collection to be casted
	 * @param <E>        type of the elements inside the targeted array
	 * @return the passed collection as an array
	 */
	@Caster.CastingMethod(subs = true)
	public <E> E[] collection2array(Class<? super E[]> klass, Collection<E> collection) {
		E[] array = (E[]) Array.newInstance(klass.getComponentType(), collection.size());

		int i = 0;
		for (E element : collection)
			array[i++] = element;

		return array;
	}

	/**
	 * Get the value of the passed {@link Collection} as the passed {@link Map map type}.
	 *
	 * @param klass      of the targeted map type
	 * @param collection to be casted
	 * @param <M>        targeted map type
	 * @param <V>        type of the values inside the targeted map
	 * @return the passed collection as an map
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true)
	public <M extends Map<? super Integer, V>, V> M collection2map(Class<? super M> klass, Collection<V> collection) {
		try {
			M map = (M) klass.getDeclaredConstructor(new Class[0]).newInstance();

			int i = 0;
			for (V element : collection)
				map.put(i++, element);

			return map;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link File} as a the targeted {@link File file type}.
	 *
	 * @param klass the class of the targeted file
	 * @param file  to be casted
	 * @param <F>   the type of the targeted file
	 * @return the passed file as the given file class
	 */
	@Caster.CastingMethod(subs = true)
	public <F extends File> F file2file(Class<? super F> klass, File file) {
		try {
			return (F) klass.getConstructor(File.class).newInstance(file);
		} catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link Map} as {@link Object Object[]}.
	 *
	 * @param klass the targeted array's class
	 * @param map   to be casted
	 * @param <V>   the targeted array's type
	 * @return the passed map as an array
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true)
	public <V> V[] map2array(Class<? super V[]> klass, Map<?, V> map) {
		return this.<ArrayList<V>, V>map2list(ArrayList.class, map).toArray((V[]) Array.newInstance(klass.getComponentType()));
	}

	/**
	 * Get the value of the passed {@link Map} as a {@link Collection}.
	 * <p>
	 * note: all values of the map will be an elements on the returned collection.
	 *
	 * @param klass the targeted collection class
	 * @param map   to be casted
	 * @param <C>   the targeted collection's type
	 * @param <E>   the type of elements in the targeted collection
	 * @return the passed map as a collection
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true, exclude = List.class)
	public <C extends Collection<E>, E> C map2collection(Class<? super C> klass, Map<?, E> map) {
		try {
			C collection = (C) klass.getDeclaredConstructor(new Class[0]).newInstance();

			collection.addAll(map.values());

			return collection;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link Map} as a {@link List}.
	 * <p>
	 * note: only values associated with an Integer key on the map will be an elements on the returned collection.
	 *
	 * @param klass the targeted list class
	 * @param map   to be casted
	 * @param <L>   the type of the targeted list
	 * @param <E>   the type of elements in the targeted list
	 * @return the passed map as a list
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true)
	public <L extends List<E>, E> L map2list(Class<? super L> klass, Map<?, E> map) {
		try {
			L list = (L) klass.getDeclaredConstructor(new Class[0]).newInstance();

			//noinspection Java8MapForEach value may not be used
			map.entrySet().forEach(entry -> {
				Object key = entry.getKey();

				if (key instanceof Integer) {
					if (((Integer) key) >= list.size())
						Collections.fill(list, ((Integer) key) + 1, i -> null);

					list.set((Integer) key, entry.getValue());
				}
			});

			return list;
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link Map} as the passed {@link Map map type}.
	 *
	 * @param klass of the targeted map type
	 * @param map   to be casted
	 * @param <M>   targeted map type
	 * @return the passed map as another map type
	 * @throws RuntimeException when instantiation exception occurs while instancing the given class
	 */
	@Caster.CastingMethod(subs = true)
	public <M extends Map> M map2map(Class<? super M> klass, Map<?, ?> map) {
		try {
			M instance = (M) klass.getDeclaredConstructor(new Class[0]).newInstance();
			//noinspection unchecked
			instance.putAll(map);
			return instance;
		} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link Number} as a {@link Double}.
	 *
	 * @param number to be casted
	 * @return the passed number as a double
	 */
	@Caster.CastingMethod
	public Double number2double(Number number) {
		return Double.valueOf(number.toString());
	}

	/**
	 * Get the value of the passed {@link Number} as a {@link Float}.
	 *
	 * @param number to be casted
	 * @return the passed number as a float
	 */
	@Caster.CastingMethod
	public Float number2float(Number number) {
		return Float.valueOf(number.toString());
	}

	/**
	 * Get the value of the passed {@link Number} as an {@link Integer}.
	 *
	 * @param number to be casted
	 * @return the passed number as an integer
	 */
	@Caster.CastingMethod
	public Integer number2integer(Number number) {
		try {
			String string = number.toString();
			return string.contains("E") ? (int) Float.parseFloat(string) : Integer.parseInt(string.split("[.]")[0]);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get the value of the passed {@link Number} as a {@link Long}.
	 *
	 * @param number to be casted
	 * @return the passed number as a long
	 */
	@Caster.CastingMethod
	public Long number2long(Number number) {
		try {
			String string = number.toString();
			return string.contains("E") ? (long) Float.parseFloat(string) : Long.parseLong(string.split("[.]")[0]);
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	/**
	 * Get the value of the passed {@link Object} as a {@link String}.
	 *
	 * @param object to be casted
	 * @return the string value of the passed object
	 */
	@Caster.CastingMethod
	public String object2string(Object object) {
		return String.valueOf(object);
	}

	/**
	 * Get the value of the passed {@link String} as a {@link Double}.
	 *
	 * @param string to be casted
	 * @return the passed string as a double
	 */
	@Caster.CastingMethod
	public Double string2double(String string) {
		return Double.valueOf(string);
	}

	/**
	 * Get the value of the passed {@link String} as a {@link File}.
	 *
	 * @param klass  the targeted file's class
	 * @param string to be casted
	 * @param <F>    the type of the targeted file
	 * @return the passed string as a file
	 */
	@Caster.CastingMethod(subs = true)
	public <F extends File> F string2file(Class<? super F> klass, String string) {
		try {
			return (F) klass.getConstructor(String.class).newInstance(string);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of the passed {@link String} as a {@link Float}.
	 *
	 * @param string to be casted
	 * @return the passed string as a float
	 */
	@Caster.CastingMethod
	public Float string2float(String string) {
		return Float.valueOf(string);
	}

	/**
	 * Get the value of the passed {@link String} as an {@link Integer}.
	 *
	 * @param string to be casted
	 * @return the passed string as an integer
	 */
	@Caster.CastingMethod
	public Integer string2integer(String string) {
		return string.contains("E") ? (int) Float.parseFloat(string) : Integer.parseInt(string.split("[.]")[0]);
	}

	/**
	 * Get the value of the passed {@link String} as a {@link Long}.
	 *
	 * @param string to be casted
	 * @return the passed string as a long
	 */
	@Caster.CastingMethod
	public Long string2long(String string) {
		return string.contains("E") ? (long) Float.parseFloat(string) : Long.parseLong(string.split("[.]")[0]);
	}
}
