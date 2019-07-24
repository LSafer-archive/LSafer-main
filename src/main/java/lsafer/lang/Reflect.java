package lsafer.lang;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * useful utils for classes.
 *
 * @author LSafer
 * @version 4
 * @since 11 Jun 2019
 */
@SuppressWarnings({"WeakerAccess"})
final public class Reflect {

    /**
     * this is a util class and shall
     * not be instanced as an object.
     */
    private Reflect(){

    }

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
        } catch (NoSuchMethodException e) {
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
     * @param <CLASS>      type of the class
     * @return the targeted class for the given name
     */
    public static <CLASS extends Class> CLASS getClass(String name, Function<?, CLASS> defaultValue) {
        if (!LoadedClasses.containsKey(name))
            LoadedClasses.put(name, defaultValue.apply(null));

        return (CLASS) LoadedClasses.get(name);
    }

    /**
     * run a class loader by it's key, or by the given
     * generator if not found, and save the generated
     * class loader for further usage at the same key.
     *
     * @param loaderKey    to map the loader at/from
     * @param defaultValue to generate the loader case not have been generated before using this method
     *                     (nullable only if the class loader have been generated before)
     * @param <LOADER>     type of the class loader
     * @return the targeted class loader for the given key
     */
    public static <LOADER extends ClassLoader> LOADER getClassLoader(String loaderKey, Function<?, LOADER> defaultValue) {
        if (!ClassLoaders.containsKey(loaderKey))
            ClassLoaders.put(loaderKey, defaultValue.apply(null));

        return (LOADER) ClassLoaders.get(loaderKey);
    }

    /**
     * run new instance of the given class.
     * <p>
     * the given class should be public
     * and if the given class is an inner class
     * then it should be static.
     *
     * @param klass      to run new instance from
     * @param types      of args | null: auto generate | new Class[]: arguments...
     * @param args       to pass to the constructor
     * @param <INSTANCE> type of the class
     * @return new instance form the given class
     */
    public static <INSTANCE> INSTANCE getInstanceOf(Class<INSTANCE> klass, Class[] types, Object... args) {
        try {
            if (types == CONSTRUCTOR_AUTO) {
                types = new Class[args.length];
                for (int i = 0; i < args.length; i++)
                    types[i] = args[i].getClass();
            } else if (types == CONSTRUCTOR_VARARG) {
                args = new Object[]{args};
            } else if (types == CONSTRUCTOR_DEFAULT) {
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
     * @param <TYPE>  type of the classes
     * @return the super class of all given classes
     */
    @SafeVarargs
    public static <TYPE> Class<? extends TYPE> getSuperOf(Class<TYPE>... classes) {
        if (classes.length == 0)
            return (Class<? extends TYPE>) Object.class;
        else if (classes.length == 1)
            return classes[0];
        else Main:for (Class<?> k = classes[0]; ; ) {
                for (Class<?> klass : classes)
                    if (!k.isAssignableFrom(klass)) {
                        k = k.getSuperclass();
                        continue Main;
                    }

                return (Class<? extends TYPE>) k;
            }
    }

    /**
     * run the super class of all
     * passed classes.
     *
     * @param objects to run super class of
     * @param <TYPE>  type of the objects
     * @return the super class of all given classes
     */
    @SafeVarargs
    public static <TYPE> Class<? extends TYPE> getSuperOf(TYPE... objects) {
        if (objects.length == 0)
            return (Class<? extends TYPE>) Object.class;
        else if (objects.length == 1)
            return (Class<? extends TYPE>) objects[0].getClass();
        else Main:for (Class<?> k = objects[0].getClass(); ; ) {
                for (Object object : objects)
                    if (!k.isInstance(object)) {
                        k = k.getSuperclass();
                        continue Main;
                    }

                return (Class<? extends TYPE>) k;
            }
    }

    /**
     * try to cast the given object to
     * the needed output type
     * and return null case can't cast.
     *
     * @param object   to cast
     * @param klass    to cast to
     * @param <CASTED> class to cast to
     * @return given object casted to given type and null case can't cast
     */
    public static <CASTED> CASTED tryCast(Class<CASTED> klass, Object object) {
        try {
            return klass.cast(object);
        } catch (ClassCastException e) {
            return null;
        }
    }

//    /**
//     * call method with more freedom :).
//     * <p>
//     * no need to sort inputs :)
//     *
//     * @param object    to invoke
//     * @param name      of method
//     * @param types     of arguments
//     * @param args      to pass
//     * @param <RETURNS> type of results
//     * @return results of method
//     */
//    public static <RETURNS> RETURNS invoke(Object object, String name, Class[] types, Object... args) {
//        try {
//            if (types == CONSTRUCTOR_AUTO) {
//                types = new Class[args.length];
//                for (int i = 0; i < args.length; i++)
//                    types[i] = args[i].getClass();
//            } else if (types == CONSTRUCTOR_VARARG) {
//                types = new Class[]{Object[].class};
//                args = new Object[]{args};
//            }
//
//            return (RETURNS) object.getClass().getMethod(name, types).invoke(object, args);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * make sure that the given object
//     * is in specific range of types
//     * using casters and if it's type
//     * is not mentioned then return it.
//     * <p>
//     * NOTE : casters well be applied in order
//     * so the first eligible type well be taken :>
//     *
//     * @param object  to cast
//     * @param casters to cast the object if on of them support it
//     * @return casted object to the given ranges (if supported)
//     */
//    public static Object rangeTypes(Object object, Caster<?, ?>... casters) {
//        for (Caster caster : casters)
//            try {
//                //noinspection unchecked
//                return caster.cast(object);
//            } catch (ClassCastException e) {
//                //
//            }
//
//        return object;
//    }
//
//    /**
//     * try to cast the given object to
//     * the needed output type
//     * and return null case can't cast.
//     *
//     * @param object to cast
//     * @param <LOADER> class to cast to
//     * @return given object casted to given type and null case can't cast
//     */
//    public static <LOADER> LOADER tryCast(Object object) {
//        try {
//            return (LOADER) object;
//        } catch (ClassCastException e) {
//            return null;
//        }
//    }

}
