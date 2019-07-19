package com.forte.util.confhelp.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Record实现类
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class NormalRecord implements Record {

    private final String name;

    private final Collection<Record> children;

    public NormalRecord(String name, Collection<Record> collection){
        this.name = Objects.requireNonNull(name);
        this.children = collection == null ? new ArrayList<>() : collection;
    }

    public NormalRecord(String name){
        this(name, new ArrayList<>());
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取子数据
     */
    @Override
    public Collection<Record> records() {
        return children;
    }

    /**
     * 增加一个数据
     *
     * @param record
     */
    @Override
    public boolean add(Record record) {
        return !children.contains(record) && children.add(record);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NormalRecord records = (NormalRecord) o;
        return Objects.equals(name, records.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    @Override
    public String toString(){
        return "Record[name='"+ name +"', value("+ children.size() +")="+ children +"]";
    }

}
