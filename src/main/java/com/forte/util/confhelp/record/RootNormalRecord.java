package com.forte.util.confhelp.record;

import java.util.Collection;

/**
 * 根数据，本质上没有区别
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class RootNormalRecord extends NormalRecord {
    public RootNormalRecord(String name, Collection<Record> collection) {
        super(name, collection);
    }

    public RootNormalRecord(String name) {
        super(name);
    }
}
