package lsafer.util;

import lsafer.io.File;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that contains a casting methods that designed to cast for each casting situation.
 * <br>
 * Each casting method is (suppose to be) designed to be invoked by {@link #cast(Class, Object) the main dynamic casting method}.
 *
 * @author LSaferSE
 * @version 2 alpha (06-Sep-19)
 * @since 31-Aug-19
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CastingEngine {
    /**
     * To avoid repetitive method searching.
     */
    final protected Map<String, Method> methods = new HashMap<>();

    /**
     * Cast the given Object to the targeted class. By searching for a matching method then invoke it then return the results of it.
     *
     * <ul>
     * <li>
     * note: after finding a matching method. It'll be stored for next time casts so
     * the next casts will be faster. (using {@link #methods Methods Map}.
     * </li>
     * </ul>
     *
     * @param klass  to cast the object to
     * @param object to be casted
     * @param <T>    type of the targeted class
     * @return the given object casted to the given class, or null case casting failure
     */
    public <T> T cast(Class<T> klass, Object object) {
        if (object == null || klass.isInstance(object))
            return (T) object;
        else if (klass == char.class)
            klass = (Class<T>) Character.class;
        else if (klass == int.class)
            klass = (Class<T>) Integer.class;
        else if (klass == boolean.class)
            klass = (Class<T>) Boolean.class;
        else if (klass == byte.class)
            klass = (Class<T>) Byte.class;
        else if (klass == double.class)
            klass = (Class<T>) Double.class;
        else if (klass == float.class)
            klass = (Class<T>) Float.class;
        else if (klass == long.class)
            klass = (Class<T>) Long.class;
        else if (klass == short.class)
            klass = (Class<T>) Short.class;

        String method_key = klass.getName() + ":" + object.getClass().getName();
        Method method = this.methods.get(method_key);

        if (method == null)
            for (Method method1 : this.getClass().getMethods())
                if (method1.isAnnotationPresent(CastingMethod.class) && method1.getParameters().length == 2) {
                    CastingMethod annotation = method1.getAnnotation(CastingMethod.class);
                    assert annotation != null;

                    Class<?>[] input = method1.getParameterTypes();
                    Class<?> output = method1.getReturnType();

                    if (input[input.length == 1 ? 0 : 1].isInstance(object) &&
                        (klass == output ||
                         (annotation.subOutput() && output.isAssignableFrom(klass)) ||
                         (annotation.superOutput() && klass.isAssignableFrom(output)))) {
                        method = method1;
                        this.methods.put(method_key, method1);
                    }
                }

        if (method != null)
            try {
                switch (method.getParameters().length) {
                    case 1:
                        return (T) method.invoke(this, klass);
                    case 2:
                        return (T) method.invoke(this, klass, object);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        return null;
    }

    /**
     * The annotation to declare what casting methods.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface CastingMethod {

        /**
         * Allow the case "the targeted class is instance of the return class".
         * <br><br><b>example:</b>
         * <pre>
         *     return Target cast(Class&lt;? instanceOf Target&gt; klass, Object object)
         * </pre>
         *
         * @return if the output is a sub output
         */
        boolean subOutput() default false;

        /**
         * Allow the case "the targeted class is a super class of the return class".
         * <br><br><b>example</b>
         * <pre>
         *     return Target cast(Class&lt;? super Target&gt; klass, Object object)
         * </pre>
         *
         * @return if the output is a super output
         */
        boolean superOutput() default false;

    }

    /**
     * Default casting engine that supports this library.
     */
    public static class Default extends CastingEngine {
        /**
         * The global instance to avoid unnecessary instancing.
         */
        final public static Default instance = new Default();

        /**
         * Get the value of the passed {@link Object Object[]} as a {@link List}.
         *
         * @param array to be casted
         * @return the passed array as a list
         */
        @CastingMethod
        public List array2list(Object[] array) {
            return Arrays.asList(array);
        }

        /**
         * Get the value of the passed {@link Object Object[]} as the passed {@link ArrayStructure ArrayStructure type}.
         *
         * @param klass of the targeted ArrayStructure type
         * @param array to be casted
         * @param <A>   targeted array-structure type
         * @return the passed array as an array-structure
         */
        @CastingMethod(subOutput = true)
        public <A extends ArrayStructure> A array2structure(Class<A> klass, Object[] array) {
            try {
                return klass.newInstance().putAll(array);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Get the value of the passed {@link Object Object[][]} as the passed {@link ArrayStructure ArrayStructure[] type}.
         *
         * @param klass  of the targeted ArrayStructure type
         * @param arrays to be casted
         * @param <A>    targeted array-structures type
         * @return the passed arrays as an array-structures
         */
        @CastingMethod(subOutput = true)
        public <A extends ArrayStructure> A[] arrays2structures(Class<A[]> klass, Object[][] arrays) {
            A[] structures = (A[]) Array.newInstance(klass.getComponentType(), arrays.length);

            for (int i = 0; i < arrays.length; i++)
                try {
                    structures[i] = ((Class<A>) klass.getComponentType()).newInstance().putAll(arrays[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return structures;
        }

        /**
         * Get the value of the passed {@link java.io.File java File} as a {@link lsafer.io.File lsafer File}.
         *
         * @param file to be casted
         * @return the passed java-file as a lsafer-file
         */
        public File file2file(java.io.File file) {
            return new File(file);
        }

        /**
         * Get the value of the passed {@link List} as the passed {@link ArrayStructure ArrayStructure type}.
         *
         * @param klass of the targeted ArrayStructure type
         * @param list  to be casted
         * @param <A>   targeted array-structure type
         * @return the passed list as an array-structure
         */
        @CastingMethod(subOutput = true)
        public <A extends ArrayStructure> A list2structure(Class<A> klass, List list) {
            try {
                return klass.newInstance().putAll(list);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Get the value of the passed {@link List lists} as the passed {@link ArrayStructure ArrayStructure type}.
         *
         * @param klass of the targeted ArrayStructure type
         * @param lists to be casted
         * @param <A>   targeted array-structures type
         * @return the passed lists as an array-structures
         */
        @CastingMethod(subOutput = true)
        public <A extends ArrayStructure> A[] lists2structures(Class<A[]> klass, List[] lists) {
            A[] structures = (A[]) Array.newInstance(klass.getComponentType(), lists.length);

            for (int i = 0; i < lists.length; i++)
                try {
                    structures[i] = ((Class<A>) klass.getComponentType()).newInstance().putAll(lists[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return structures;
        }

        /**
         * Get the value of the passed {@link Map} as the passed {@link Structure structure type}.
         *
         * @param klass of the targeted Structure type
         * @param map   to be casted
         * @param <S>   targeted structure type
         * @return the passed map as a structure
         */
        @CastingMethod(subOutput = true)
        public <S extends Structure> S map2structure(Class<S> klass, Map map) {
            try {
                return klass.newInstance().putAll(map);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Get the value of the passed {@link Map maps} as the passed {@link Structure structure type}.
         *
         * @param klass of the targeted Structure type
         * @param maps  to be casted
         * @param <S>   targeted structure type
         * @return the passed maps as a structures
         */
        @CastingMethod(subOutput = true)
        public <S extends Structure> S[] maps2structures(Class<S[]> klass, Map[] maps) {
            S[] structures = (S[]) Array.newInstance(klass.getComponentType(), maps.length);

            for (int i = 0; i < maps.length; i++)
                try {
                    structures[i] = ((Class<S>) klass.getComponentType()).newInstance().putAll(maps[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return structures;
        }

        /**
         * Get the value of the passed {@link Number} as a {@link Double}.
         *
         * @param number to be casted
         * @return the passed number as a double
         */
        @CastingMethod
        public Double number2double(Number number) {
            return Double.valueOf(number.toString());
        }

        /**
         * Get the value of the passed {@link Number} as a {@link Float}.
         *
         * @param number to be casted
         * @return the passed number as a float
         */
        @CastingMethod
        public Float number2float(Number number) {
            return Float.valueOf(number.toString());
        }

        /**
         * Get the value of the passed {@link Number} as an {@link Integer}.
         *
         * @param number to be casted
         * @return the passed number as an integer
         */
        @CastingMethod
        public Integer number2integer(Number number) {
            String string = number.toString();
            return string.contains("E") ? (int) (float) Float.valueOf(string) :
                   Integer.valueOf(string.split("[.]")[0]);
        }

        /**
         * Get the value of the passed {@link Number} as a {@link Long}.
         *
         * @param number to be casted
         * @return the passed number as a long
         */
        @CastingMethod
        public Long number2long(Number number) {
            String string = number.toString();
            return string.contains("E") ? (long) (float) Float.valueOf(string) :
                   Long.valueOf(string.split("[.]")[0]);
        }

        /**
         * Get the value of the passed {@link Object} as a {@link String}.
         *
         * @param object to be casted
         * @return the string value of the passed object
         */
        @CastingMethod
        public String object2string(Object object) {
            return String.valueOf(object);
        }

        /**
         * Get the value of the passed {@link String} as a {@link Double}.
         *
         * @param string to be casted
         * @return the passed string as a double
         */
        @CastingMethod
        public Double string2double(String string) {
            return Double.valueOf(string);
        }

        /**
         * Get the value of the passed {@link String} as a {@link File}.
         *
         * @param string to be casted
         * @return the passed string as a file
         */
        @CastingMethod(superOutput = true)
        public File string2file(String string) {
            return new File(string);
        }

        /**
         * Get the value of the passed {@link String} as a {@link Float}.
         *
         * @param string to be casted
         * @return the passed string as a float
         */
        @CastingMethod
        public Float string2float(String string) {
            return Float.valueOf(string);
        }

        /**
         * Get the value of the passed {@link String} as an {@link Integer}.
         *
         * @param string to be casted
         * @return the passed string as an integer
         */
        @CastingMethod
        public Integer string2integer(String string) {
            return string.contains("E") ? (int) (float) Float.valueOf(string) :
                   Integer.valueOf(string.split("[.]")[0]);
        }

        /**
         * Get the value of the passed {@link String} as a {@link Long}.
         *
         * @param string to be casted
         * @return the passed string as a long
         */
        @CastingMethod
        public Long string2long(String string) {
            return string.contains("E") ? (long) (float) Float.valueOf(string) :
                   Long.valueOf(string.split("[.]")[0]);
        }

        /**
         * Get the value of the passed {@link ArrayStructure} as {@link Object Object[]}.
         *
         * @param structure to be casted
         * @return the passed array-structure as an array
         */
        @CastingMethod
        public Object[] structure2array(ArrayStructure structure) {
            return structure.array();
        }

        /**
         * Get the value of the passed {@link ArrayStructure} as a {@link List}.
         *
         * @param structure to be casted
         * @return the passed array-structure as a list
         */
        @CastingMethod
        public List structure2list(ArrayStructure structure) {
            return structure.list();
        }

        /**
         * Get the value of the passed {@link Structure} as a {@link Map}.
         *
         * @param structure to be casted
         * @return the passed structure as a map
         */
        @CastingMethod
        public Map structure2map(Structure structure) {
            return structure.map();
        }

        /**
         * Get the value of the passed {@link Structure} as the passed {@link Structure structure type}.
         *
         * @param klass     of the targeted Structure type
         * @param structure to be casted
         * @param <S>       targeted structure type
         * @return the passed structure as another structure type
         */
        @CastingMethod(subOutput = true)
        public <S extends Structure> S structure2structure(Class<S> klass, Structure structure) {
            return structure.clone(klass);
        }

        /**
         * Get the value of the passed {@link ArrayStructure ArrayStructure[]} as a {@link Object Object[][]}.
         *
         * @param structures to be casted
         * @return the passed structures as an arrays
         */
        @CastingMethod
        public Object[][] structures2arrays(ArrayStructure[] structures) {
            Object[][] arrays = new Object[structures.length][];

            for (int i = 0; i < structures.length; i++)
                arrays[i] = structures[i] == null ? null : structures[i].array();

            return arrays;
        }

        /**
         * Get the value of the passed {@link ArrayStructure ArrayStructure[]} as a {@link List List[]}.
         *
         * @param structures to be casted
         * @return the passed structures as a lists
         */
        @CastingMethod
        public List[] structures2lists(ArrayStructure[] structures) {
            List[] lists = new List[structures.length];

            for (int i = 0; i < structures.length; i++)
                lists[i] = structures[i] == null ? null : structures[i].list();

            return lists;
        }

        /**
         * Get the value of the passed {@link Structure Structure[]} as a {@link Map Map[]}.
         *
         * @param structures to be casted
         * @return the passed structures as a maps
         */
        @CastingMethod
        public Map[] structures2maps(Structure[] structures) {
            Map[] maps = new Map[structures.length];

            for (int i = 0; i < structures.length; i++)
                maps[i] = structures[i] == null ? null : structures[i].map();

            return maps;
        }
    }
}
