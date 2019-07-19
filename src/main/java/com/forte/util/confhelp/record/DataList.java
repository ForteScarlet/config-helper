package com.forte.util.confhelp.record;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * List类型的数据
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class DataList<T> extends BaseData<List<T>> {
    /**
     * 构造
     *
     * @param name 名称
     * @param init 初始数据
     */
    public DataList(String name, List<T> init) {
        super(name, init);
    }

    /**
     * 构造
     * @param name 名称
     */
    public DataList(String name){
        super(name, new ArrayList<>());
    }

    /**
     * 增加一个数据
     */
    public boolean add(T addValue){
        return value.add(addValue);
    }

    /**
     * 数据长度
     */
    public int size(){
        return value.size();
    }


}
