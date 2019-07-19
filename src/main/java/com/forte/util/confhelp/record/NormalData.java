package com.forte.util.confhelp.record;

import java.util.List;

/**
 * 除了List类型以外的数据类型
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class NormalData<T> extends BaseData<T> {
    /**
     * 构造
     *
     * @param name 名称
     * @param init 初始数据
     */
    public NormalData(String name, T init) {
        super(name, init);
    }

}
