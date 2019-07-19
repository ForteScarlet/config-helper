package com.forte.util.confhelp.reader;

import com.forte.util.confhelp.anno.Prop;
import com.forte.util.confhelp.record.Data;
import com.forte.util.confhelp.record.Datas;
import com.forte.util.confhelp.record.Record;
import com.forte.util.confhelp.record.Records;

import java.util.function.Consumer;

/**
 * 配置文件读取接口，定义接口以提供不同实现类来对不同类型的文件进行读取
 * 最终结果都将会读取成为Map类型
 * 读取到的配置信息中，key值类似于包路径的格式，使用xxx.xxx的形式
 *
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public interface Configs {

    String SPLIT = "\\.";

    /** 获取根数据集合 */
    Records getRootRecords();

    /** 获取某个名称下的数据，没有对应的则直接返回null */
    Record getRecord(String name);

    /** 获取某个名称下的全部Record */
    Records getRecords(String name);

    /** 通过名称获取一个数据对象， 如果没有对应则返回null */
    Data getData(String name);

    /** 获取某个名称组下的所有数据 */
    Datas getDatas(String name);

    /**
     * 从根数据开始遍历所有数据
     */
    default void forEach(Consumer<Record> consumer){
        getRootRecords().forEach(rs -> rs.forEach(consumer));
    }


    /**
     * 根据当前Configs数据，对参数进行配置注入
     */
    default <T> T inject(T obj){
        //获取其对应注入器
        Injector<T> injector = (Injector<T>) InjectorFactory.getInjector(obj.getClass());

        //执行注入
        injector.inject(this, obj);

        //返回其本身
        return obj;
    }










}