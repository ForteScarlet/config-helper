package com.forte.util.confhelp.reader;

import com.forte.util.confhelp.properties.Properties;
import com.forte.util.confhelp.record.Records;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

/**
 * Properties类型的参数读取
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class PropertiesConfigs extends BaseConfigs {

    /**
     * 构造
     * @param inputStream 输入流
     */
    public PropertiesConfigs(InputStream inputStream, Properties properties) throws Exception {
        super(inputStream, properties);
    }

    /**
     * 通过输入流解析数据
     */
    @Override
    protected Records read(InputStream inputStream, Properties configsProperties) throws IOException {
        java.util.Properties properties = new java.util.Properties();
        //读取数据
        properties.load(inputStream);
        //转化数据
        return toRecords(properties.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()))));
    }
}
