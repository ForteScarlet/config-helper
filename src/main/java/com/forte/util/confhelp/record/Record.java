package com.forte.util.confhelp.record;

import com.sun.xml.internal.fastinfoset.stax.events.EmptyIterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * 读取到的数据
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public interface Record extends Iterable<Record> {

    String SPLIT = "\\.";

    /** 获取名称 */
    String getName();

    /** 获取子数据 */
    Collection<Record> records();

    /** 增加一个数据 */
    boolean add(Record record);

    /** 添加多条数据 */
    default void addAll(Collection<Record> recordCollection){
        recordCollection.forEach(this::add);
    }

    /** 寻找某个名称的Record */
    default Record find(String... names){
        if(names == null || names.length == 0){
            return null;
        }else if(names.length == 1){
            String name = names[0];
            //寻找
            for (Record record : records()) {
                if(record.getName().equals(name)){
                    return record;
                }
            }
            return null;
        }else{
            Record first = find(names[0]);
            if(first != null){
                return first.find(Arrays.stream(names).skip(1).toArray(String[]::new));
            }else{
                return null;
            }
        }
    }

    /**
     * Returns an iterator over elements of type {@code Record}.
     * @return an Iterator.
     */
    @Override
    default Iterator<Record> iterator() {
        Collection<Record> records = records();
        return records == null ?
                EmptyIterator.getInstance() :
                Stream.concat(records.stream(), Stream.of(this)).iterator();
    }



}
