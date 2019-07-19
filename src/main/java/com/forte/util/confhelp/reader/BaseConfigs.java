package com.forte.util.confhelp.reader;

import com.forte.util.confhelp.properties.ConfigsProperties;
import com.forte.util.confhelp.properties.Properties;
import com.forte.util.confhelp.record.*;

import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 配置信息类的基类
 * 定义构造方法
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public abstract class BaseConfigs implements Configs {

    /** 保存所有的Record数据 */
    private final Records records;

    /**
     * 获取根数据集合
     */
    @Override
    public Records getRootRecords() {
        return records;
    }

    /**
     * 获取某个名称下的数据，没有对应的则直接返回null
     */
    @Override
    public Record getRecord(String name) {
        Objects.requireNonNull(name);
        String[] split = name.split(SPLIT);
        String findName = split[0];
        if(split.length == 1){
            for (Record record : records) {
                if(record.getName().equals(findName)){
                    return record;
                }
            }
        }else{
            for (Record record : records) {
                if(record.getName().equals(findName)){
                    return record.find(Arrays.stream(split).skip(1).toArray(String[]::new));
                }
            }
        }
        return null;
    }

    /**
     * 获取某个名称下的全部Record
     *
     * @param name
     */
    @Override
    @Deprecated
    public Records getRecords(String name) {
        return null;
    }

    /**
     * 通过名称获取一个数据对象， 如果没有对应则返回null
     */
    @Override
    public Data getData(String name) {
        return (Data) getRecord(name);
    }

    /**
     * 获取某个名称组下的所有数据
     */
    @Override
    @Deprecated
    public Datas getDatas(String name) {
        return null;
    }

    /** 通过输入流解析数据 */
    protected abstract Records read(InputStream inputStream, Properties properties) throws Exception;

    /** 将类似于properties类型的数据转化为Records数据 */
    protected Records toRecords(Map<String, Object> originalMaps){
        Records records = new Records();

        originalMaps.forEach((k, v) -> {
            //根据切割符切割
            String[] split = k.split(SPLIT);
            String rootName = split[0];

            //根数据
            Record rootRecord = null;
            //遍历切割后的结果
            for (int i = 0; i < split.length; i++) {
                String name = split[i];
                if(i == 0 && split.length > 1){
                    //第一个
                    rootRecord = new NormalRecord(rootName);
                    if (!records.add(rootRecord)) {
                        rootRecord = records.get(rootName);
                    }
                }else if(i != split.length - 1){
                    //不是最后一个也不是第一个
                    String[] findNames = Arrays.stream(split).limit(i).skip(1).toArray(String[]::new);
                    NormalRecord addRecord = new NormalRecord(name);
                    if(findNames.length == 0){
                        //是第二个，直接保存
                        rootRecord.add(addRecord);
                    }else{
                        //否则先查找再保存，理论上不会查询到空值
                        Record record = rootRecord.find(findNames);
                        record.add(addRecord);
                    }
                }else{
                    //是最后一个, 也有可能只有这一个
                    Data<?> lastData;
                    String[] findNames = Arrays.stream(split).limit(split.length - 1).skip(1).toArray(String[]::new);
                    if(v instanceof List || v.getClass().isArray()){
                        List<?> data;
                        if(v.getClass().isArray()){
                            data = Arrays.stream((Object[]) v).collect(Collectors.toList());
                        }else{
                            data = (List<?>) v;
                        }
                        lastData = new DataList<>(name, data);
                    }else{
                        //不是集合类型，当作普通类型
                        lastData = new NormalData<>(name, v);
                    }
                    if(split.length == 1){
                        //如果只有一条
                        records.add(lastData);
                    }else if(split.length == 2){
                        //如果第二条就是数据，直接在根数据中添加
                        rootRecord.add(lastData);
                    }else{
                        rootRecord.find(findNames).add(lastData);
                    }

                }
            }
        });


        return records;
    }

    /**
     * 将递归类型的Map集合转化为records对象
     * 递归类型的key不应该再出现需要切割的情况
     * 此类型一般适用于yaml类型数据
     * @param rootRecordCreater 根节点的实例函数
     */
    protected Records toRecordsRecursive(Map<String, Object> originalMaps, BiFunction<String, Collection<Record>, Record> rootRecordCreater){
        Records records = new Records();

        //遍历
        originalMaps.forEach((k, v) -> {
            //判断数据类型
            //如果是list、Array，则作为Data直接保存
            if(v instanceof List || v.getClass().isArray()){
                Data<?> listData;
                if(v.getClass().isArray()){
                    listData = new DataList<>(k, Arrays.stream((Object[]) v).collect(Collectors.toList()));
                }else{
                    listData = new DataList<>(k, (List<?>) v);
                }
                records.add(listData);
            }else if(v instanceof Map){
                //如果是Map类型，说明有深层
                Record root = rootRecordCreater.apply(k, null);
                Map<String, Object> inner = (Map<String, Object>) v;
                Records innerRecords = toRecordsRecursive(inner, (n, va) -> va == null ? new NormalRecord(n) : new NormalRecord(n, va));
                root.addAll(innerRecords);
                records.add(root);
            }else{
                //否则，是普通的数据类型，直接保存
                records.add(new NormalData<>(k, v));
            }

        });

        return records;
    }

    /**
     * 将递归类型的Map集合转化为records对象
     * 递归类型的key不应该再出现需要切割的情况
     * 此类型一般适用于yaml类型数据
     */
    protected Records toRecordsRecursive(Map<String, Object> originalMaps){
        return toRecordsRecursive(originalMaps, RootNormalRecord::new);
    }


        /**
         * 构造
         * @param inputStream 输入流
         */
    public BaseConfigs(InputStream inputStream, Properties properties) throws Exception {
        Records read = read(inputStream, properties == null ? ConfigsProperties.getEmptyProperties() : properties);
        this.records = read == null ? new Records() : read;
    }
}
