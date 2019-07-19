package com.forte.util.confhelp.properties;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 配置类的实现，直接继承HashMap并实现{@link Properties} 接口
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class ConfigsProperties extends HashMap<String, Object> implements Properties {

    public static final ConfigsProperties EMPTY_PROPERTIES = new EmptyConfigsProperties();

    public static ConfigsProperties getEmptyProperties(){
        return EMPTY_PROPERTIES;
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *                                  or the load factor is nonpositive
     */
    public ConfigsProperties(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public ConfigsProperties(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public ConfigsProperties() {
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public ConfigsProperties(Map<? extends String, ?> m) {
        super(m);
    }

    /**
     * 空值类型
     */
    private static class EmptyConfigsProperties extends ConfigsProperties {
        private static final long serialVersionUID = 6428348081105594320L;

        @Override
        public int size()                          {return 0;}
        @Override
        public boolean isEmpty()                   {return true;}
        @Override
        public boolean containsKey(Object key)     {return false;}
        @Override
        public boolean containsValue(Object value) {return false;}
        @Override
        public Object get(Object key)                   {return null;}
        @Override
        public Set<String> keySet()                     {return Collections.emptySet();}
        @Override
        public Collection<Object> values()              {return Collections.emptySet();}
        @Override
        public Set<Map.Entry<String, Object>> entrySet()      {return Collections.emptySet();}

        @Override
        public boolean equals(Object o) {
            return (o instanceof Map) && ((Map<?,?>)o).isEmpty();
        }

        @Override
        public int hashCode()                      {return 0;}

        //****************  Override default methods in Map ****************//

        @Override
        @SuppressWarnings("unchecked")
        public Object getOrDefault(Object k, Object defaultValue) {
            return defaultValue;
        }

        @Override
        public void forEach(BiConsumer<? super String, ? super Object> action) {
            Objects.requireNonNull(action);
        }

        @Override
        public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
            Objects.requireNonNull(function);
        }

        @Override
        public Object putIfAbsent(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(String key, Object oldValue, Object newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object replace(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object computeIfAbsent(String key,
                                 Function<? super String, ? extends Object > mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object computeIfPresent(String key,
                                  BiFunction<? super String, ? super Object , ? extends Object > remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object compute(String key,
                         BiFunction<? super String, ? super Object , ? extends Object > remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object merge(String key, Object value,
                       BiFunction<? super Object , ? super Object , ? extends Object > remappingFunction) {
            throw new UnsupportedOperationException();
        }

        // Preserves singleton property
        private Object readResolve() {
            return Collections.EMPTY_MAP;
        }
    }


}
