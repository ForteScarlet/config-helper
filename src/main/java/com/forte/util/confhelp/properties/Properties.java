package com.forte.util.confhelp.properties;

import org.apache.commons.beanutils.ConvertUtils;

import java.util.Map;

/**
 * 参数，此参数接口定义部分需要参数的读取器。实现Map接口
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public interface Properties extends Map<String, Object> {

    /**
     * 获取并将类型转化为需要的类型
     * @param key   key
     * @param type  type class
     * @param <T>   type what you need
     */
    default <T> T get(String key, Class<T> type){
        return (T) get(key);
    }

    /**
     * 通过{@link ConvertUtils} 来实现类型转化
     * @param key   key
     * @param type  type Class
     * @param <T>   type what you need
     */
    default <T> T getConvert(String key, Class<T> type){
        Object value = get(key);
        return value == null ? null : (T) ConvertUtils.convert(value, type);
    }

    /**
     * 通过{@link String#valueOf(Object)} 来转化为String类型
     * @param key
     * @return
     */
    default String getString(String key){
        Object o = get(key);
        return o == null ? null : String.valueOf(o);
    }


    default String getString(String key, String ifNullDefault){
        Object o = get(key);
        return o == null ? ifNullDefault : String.valueOf(o);
    }

}
