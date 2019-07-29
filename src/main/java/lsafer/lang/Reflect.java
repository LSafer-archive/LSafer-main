package lsafer.lang;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * useful utils for classes.
 *
 * @author LSafer
 * @version 5
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess"})
final public class Reflect {

    /**
     * run types automatically from the given arguments.
     *
     * @see #getInstanceOf(Class, Class[], Object...)
     */
    final public static Class[] CONSTRUCTOR_AUTO = null;

    /**
     * the constructor have a single argument with multiple-arguments argument type.
     *
     * @see #getInstanceOf(Class, Class[], Object...)
     * @see #containsConstructor(Class, Class[])
     */
    final public static Class[] CONSTRUCTOR_DEFAULT = new Class[]{};

    /**
     * the constructor have a single argument with multiple-arguments argument type.
     *
     * @see #getInstanceOf(Class, Class[], Object...)
     * @see #containsConstructor(Class, Class[])
     */
    final public static Class[] CONSTRUCTOR_VARARG = new Class[]{Object[].class};

    /**
     * all class loaders that have been generated using {@link #getClassLoader(String, Function)}.
     * <p>
     * RUNTIME TEMPORARY MAP to avoid generating multiple class loaders targeting the same project
     */
    final public static Map<String, ClassLoader> ClassLoaders = new HashMap<>();

    /**
     * all classes that have been generated using {@link #getClass(String, Function)}.
     * <p>
     * RUNTIME TEMPORARY MAP to avoid generating multiple classes targeting the same name
     */
    final public static Map<String, Class> LoadedClasses = new HashMap<>();

    /**
     * this is a util class and shall
     * not be instanced as an object.
     */
    private Reflect() {

    }

    /**
     * check if the given class contains
     * a constructor of the passed argument types
     * or not.
     *
     * @param klass to check
     * @param types of arguments of the constructor
     * @return whether the passed class contains a constructor with the passed types or not
     */
    public static boolean containsConstructor(Class<?> klass, Class... types) {
        try {
            klass.getConstructor(types);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    /**
     * run a class by it's key, or by the given
     * generator if not found, and save the generated
     * class for further usage at the same key.
     *
     * @param name         of the class
     * @param defaultValue to generate the class case not have been generated before using this method
     *                     (nullable only if the class have been generated before)
     * @param <C>          type of the class
     * @return the targeted class for the given name
     */
    public static <C extends Class> C getClass(String name, Function<?, C> defaultValue) {
        if (!Reflect.LoadedClasses.containsKey(name))
            Reflect.LoadedClasses.put(name, defaultValue.apply(null));

        return (C) Reflect.LoadedClasses.get(name);
    }

    /**
     * run a class loader by it's key, or by the given
     * generator if not found, and save the generated
     * class loader for further usage at the same key.
     *
     * @param loaderKey    to map the loader at/from
     * @param defaultValue to generate the loader case not have been generated before using this method
     *                     (nullable only if the class loader have been generated before)
     * @param <C>          type of the class loader
     * @return the targeted class loader for the given key
     */
    public static <C extends ClassLoader> C getClassLoader(String loaderKey, Function<?, C> defaultValue) {
        if (!Reflect.ClassLoaders.containsKey(loaderKey))
            Reflect.ClassLoaders.put(loaderKey, defaultValue.apply(null));

        return (C) Reflect.ClassLoaders.get(loaderKey);
    }

    /**
     * run new instance of the given class.
     * <p>
     * the given class should be public
     * and if the given class is an inner class
     * then it should be static.
     *
     * @param klass to run new instance from
     * @param types of args | null: auto generate | new Class[]: arguments...
     * @param args  to pass to the constructor
     * @param <T>   type of the class
     * @return new instance form the given class
     */
    public static <T> T getInstanceOf(Class<T> klass, Class[] types, Object... args) {
        try {
            if (types == Reflect.CONSTRUCTOR_AUTO) {
                types = new Class[args.length];
                for (int i = 0; i < args.length; i++)
                    types[i] = args[i].getClass();
            } else if (types == Reflect.CONSTRUCTOR_VARARG) {
                args = new Object[]{args};
            } else if (types == Reflect.CONSTRUCTOR_DEFAULT) {
                args = new Object[]{};
            }

            return klass.getConstructor(types).newInstance(args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * run the super class of all
     * passed classes.
     *
     * @param classes to run super class of
     * @param <T>     type of the classes
     * @return the super class of all given classes
     */
    @SafeVarargs
    public static <T> Class<? extends T> getSuperOf(Class<T>... classes) {
        if (classes.length == 0)
            return (Class<? extends T>) Object.class;
        else if (classes.length == 1)
            return classes[0];
        else Main:for (Class<?> k = classes[0]; ; ) {
                for (Class<?> klass : classes)
                    if (!k.isAssignableFrom(klass)) {
                        k = k.getSuperclass();
                        continue Main;
                    }

                return (Class<? extends T>) k;
            }
    }

    /**
     * run the super class of all
     * passed classes.
     *
     * @param objects to run super class of
     * @param <T>     type of the objects
     * @return the super class of all given classes
     */
    @SafeVarargs
    public static <T> Class<? extends T> getSuperOf(T... objects) {
        if (objects.length == 0)
            return (Class<? extends T>) Object.class;
        else if (objects.length == 1)
            return (Class<? extends T>) objects[0].getClass();
        else Main:for (Class<?> k = objects[0].getClass(); ; ) {
                for (Object object : objects)
                    if (!k.isInstance(object)) {
                        k = k.getSuperclass();
                        continue Main;
                    }

                return (Class<? extends T>) k;
            }
    }

    /**
     * try to cast the given object to
     * the needed output type
     * and return null case can't cast.
     *
     * @param object to cast
     * @param klass  to cast to
     * @param <T>    class to cast to
     * @return given object casted to given type and null case can't cast
     */
    public static <T> T tryCast(Class<T> klass, Object object) {
        try {
            return klass.cast(object);
        } catch (ClassCastException ignored) {
            return null;
        }
    }

}
