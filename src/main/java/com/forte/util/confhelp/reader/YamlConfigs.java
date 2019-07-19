package com.forte.util.confhelp.reader;


import com.forte.util.confhelp.properties.Properties;
import com.forte.util.confhelp.record.Records;
import org.ho.yaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class YamlConfigs extends BaseConfigs {

    /**
     * 构造
     * @param inputStream 输入流
     */
    public YamlConfigs(InputStream inputStream, Properties properties) throws Exception {
        super(inputStream, properties);
    }

    /**
     * 通过输入流解析数据
     */
    @Override
    protected Records read(InputStream inputStream, Properties properties) {
        Map<String, Object> load = (Map<String, Object>) Yaml.load(inputStream);
        return toRecordsRecursive(load);
    }
}
