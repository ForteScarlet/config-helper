package com.forte.util.confhelp.record;

import com.forte.util.confhelp.exception.ConfigsHelperException;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据，可以对应普通的String数据, 也可以对应集合，键值对等
 * TODO 考虑增加一个空值Data以代替返回值为null的情况
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public interface Data<T> extends Record, Map.Entry<String, T> {

    /** 获取此数据的值 */
    @Override
    T getValue();


    /** 获取数据并转化 */
    default <U> U getValue(Function<T, U> format){
        return format.apply(getValue());
    }

    /**
     * 替换值
     * @param newValue  新的值
     * @return          被替换掉的值
     */
    @Override
    T setValue(T newValue);



    /** 获取子数据 不存在子数据 */
    @Override
    @Deprecated
    default Collection<Record> records(){
        return null;
    }

    /** 数据无法添加子数据 */
    @Override
    @Deprecated
    default boolean add(Record record){
        return false;
    }

    //**************** Map.Entry接口实现 ****************//

    /**
     * Returns the key corresponding to this entry.
     *
     * @return the key corresponding to this entry
     * @throws IllegalStateException implementations may, but are not
     *                               required to, throw this exception if the entry has been
     *                               removed from the backing map.
     */
    @Override
    default String getKey() {
        return getName();
    }

}
