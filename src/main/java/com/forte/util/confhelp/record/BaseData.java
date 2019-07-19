package com.forte.util.confhelp.record;

import java.util.Objects;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public abstract class BaseData<T> implements Data<T> {

    /** 名称 */
    private final String name;

    /** 数据 */
    protected volatile T value;

    @Override
    public String getName(){
        return name;
    }

    @Override
    public T getValue(){
        return this.value;
    }

    @Override
    public T setValue(T newValue){
        T old = this.value;
        this.value = newValue;
        return old;
    }

    /**
     * 构造
     * @param init 初始数据
     * @param name 名称
     */
    public BaseData(String name, T init){
        this.name = name;
        this.value = init;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseData<?> baseData = (BaseData<?>) o;
        return name.equals(baseData.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString(){
        String valueStr = "value="+(value.getClass().equals(String.class) ? "'"+ value +"'" : value);
        return this.getClass().getSimpleName() + "[name='"+ name +"', "+ valueStr +"]";
    }

}
