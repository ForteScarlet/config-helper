package com.forte.util.confhelp;

import com.forte.util.confhelp.exception.ConfigsHelperException;
import com.forte.util.confhelp.exception.ConfigsInitException;
import com.forte.util.confhelp.exception.ConfigsTypeException;
import com.forte.util.confhelp.properties.Properties;
import com.forte.util.confhelp.reader.Configs;
import com.forte.util.confhelp.reader.PropertiesConfigs;
import com.forte.util.confhelp.reader.XmlConfigs;
import com.forte.util.confhelp.reader.YamlConfigs;
import com.forte.util.confhelp.util.VerifyFileType;
import sun.reflect.Reflection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Configs工厂
 *
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class ConfigsFactory {


    /**
     * 所有对应配置
     */
    private static Map<String, BiFunction<InputStream, Properties, ? extends Configs>> CONFIGS = new ConcurrentHashMap<String, BiFunction<InputStream, Properties, ? extends Configs>>() {{
        for (String type : ConfigType.YAML.TYPES) {
            //yaml
            put(type, (in, p) -> {
                try {
                    return new YamlConfigs(in, p);
                } catch (Exception e) {
                    throw new ConfigsInitException("yaml数据解析异常", e);
                }
            });
        }
        //xml
        for (String type : ConfigType.XML.TYPES) {
            put(type, (in, p) -> {
                try {
                    return new XmlConfigs(in, p);
                } catch (Exception e) {
                    throw new ConfigsInitException("XML数据解析异常", e);
                }
            });
        }
        for (String type : ConfigType.PROPERTIES.TYPES) {
            //properties
            put(type, (in, p) -> {
                try {
                    return new PropertiesConfigs(in, p);
                } catch (Exception e) {
                    throw new ConfigsInitException("properties数据解析异常", e);
                }
            });
        }
    }};

    //for safe
    static {
        Reflection.registerFieldsToFilter(ConfigsFactory.class, "CONFIGS");
    }

    /**
     * 注册一个配置文件读取器
     *
     * @param creator 通过输入流和配置参数创建读取器
     * @param types   对应的类型数组, 将会直接转化为全小写字符
     * @throws ConfigsTypeException type is null、type‘s length is 0、type already existed
     */
    public synchronized static void rigister(BiFunction<InputStream, Properties, ? extends Configs> creator, String[] types) {
        //先判断types是否有存在的
        Objects.requireNonNull(types);
        if (types.length == 0) {
            throw new ConfigsTypeException("Second parameter「type」length can not less than 1");
        }

        //判断是否存在
        for (String type : types) {
            if (CONFIGS.get(type.toLowerCase()) != null) {
                throw new ConfigsTypeException("type「" + type + "」already existed");
            }
        }

        //判断都通过，保存记录
        for (String type : types) {
            CONFIGS.put(type.toLowerCase(), creator);
        }
    }


    /**
     * 获取某个类型的解析器
     */
    public static BiFunction<InputStream, Properties, ? extends Configs> getCreator(String type) {
        return CONFIGS.get(type.toLowerCase());
    }

    /**
     * 获取一个读取器
     *
     * @param type 类型
     * @param in   数据的输入流
     */
    public static Configs getConfigs(String type, InputStream in, Properties properties, boolean close) throws IOException {
        BiFunction<InputStream, Properties, ? extends Configs> creator = getCreator(type);
        if (creator == null) {
            throw new ConfigsTypeException("can not find type called 「" + type + "」");
        }
        Configs apply = creator.apply(in, properties);
        if (close) {
            in.close();
        }
        return apply;

    }

    /**
     * 获取一个读取器
     *
     * @param type 类型
     * @param in   数据的输入流
     */
    public static Configs getConfigs(String type, InputStream in, boolean close) throws IOException {
        return getConfigs(type, in, null, close);
    }

    /**
     * 获取一个读取器，默认不关闭流
     *
     * @param type 类型
     * @param in   输入流
     * @return
     */
    public static Configs getConfigs(String type, InputStream in, Properties properties) {
        try {
            return getConfigs(type, in, properties, false);
        } catch (IOException e) {
            throw new ConfigsHelperException(e);
        }
    }

    /**
     * 获取一个读取器，默认不关闭流
     *
     * @param type 类型
     * @param in   输入流
     * @return
     */
    public static Configs getConfigs(String type, InputStream in) {
        try {
            return getConfigs(type, in, false);
        } catch (IOException e) {
            throw new ConfigsHelperException(e);
        }
    }

    /**
     * 获取一个读取器
     *
     * @param file 文件对象
     * @throws IOException          开流、关流的时候涉及到此异常
     * @throws ConfigsTypeException 判断文件类型、是否可用的时候涉及到此异常
     */
    public static Configs getConfigs(File file, Properties properties, boolean close) throws IOException {
        if (!file.isFile()) {
            throw new ConfigsTypeException("File is not a file type");
        } else if (!file.exists()) {
            throw new ConfigsTypeException("File is not exist");
        } else if (!file.canRead()) {
            throw new ConfigsTypeException("File can not read");
        } else {
            String[] split = file.getName().split("\\.");
            String type = split.length > 1 ? split[split.length - 1] : null;
            if (type == null) {
                //无法通过名称获取类型，尝试通过getFileTypeByHead获取
                type = VerifyFileType.getFileType(file);
            }
            if (type == null) {
                throw new ConfigsTypeException("can not determine type for file: " + file.getAbsolutePath());
            }

            BiFunction<InputStream, Properties, ? extends Configs> creator = CONFIGS.get(type);
            FileInputStream fileInputStream = new FileInputStream(file);
            Configs apply = creator.apply(fileInputStream, properties);
            if (close) {
                fileInputStream.close();
            }
            return apply;
        }
    }


    /**
     * 获取一个读取器, 默认关流
     *
     * @param file 文件对象
     */
    public static Configs getConfigs(File file, Properties properties) throws IOException {
        return getConfigs(file, properties, true);
    }

    /**
     * 获取一个读取器, 默认关流
     *
     * @param file 文件对象
     */
    public static Configs getConfigs(File file) throws IOException {
        return getConfigs(file, null, true);
    }

    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     *
     * @param resourceAsStreamName 路径
     * @param type                 类型
     * @param close                是否闭流
     */
    public static Configs getConfigs(String resourceAsStreamName, String type, Properties properties, boolean close) throws IOException {
        Objects.requireNonNull(resourceAsStreamName);
        return getConfigs(type, ConfigsFactory.class.getResourceAsStream(resourceAsStreamName), properties, close);
    }

    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     *
     * @param resourceAsStreamName 路径
     * @param type                 类型
     * @param close                是否闭流
     */
    public static Configs getConfigs(String resourceAsStreamName, String type, boolean close) throws IOException {
        Objects.requireNonNull(resourceAsStreamName);
        return getConfigs(type, ConfigsFactory.class.getResourceAsStream(resourceAsStreamName), null, close);
    }


    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     * 通过name切割判断文件类型
     *
     * @param resourceAsStreamName 路径
     * @param close                是否闭流
     */
    public static Configs getConfigs(String resourceAsStreamName, Properties properties, boolean close) throws IOException {
        String[] split = resourceAsStreamName.split("\\.");
        if (split.length == 1) {
            throw new ConfigsTypeException("can not determine type for resourceAsStreamName: " + resourceAsStreamName);
        }
        String type = split[split.length - 1];
        return getConfigs(resourceAsStreamName, type, properties, close);
    }

    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     * 通过name切割判断文件类型
     * 默认闭流
     *
     * @param resourceAsStreamName 路径
     */
    public static Configs getConfigs(String resourceAsStreamName, Properties properties) throws IOException {
        return getConfigs(resourceAsStreamName, properties, true);
    }

    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     * 默认闭流
     *
     * @param resourceAsStreamName 路径
     */
    public static Configs getConfigs(String resourceAsStreamName, String type, Properties properties) throws IOException {
        return getConfigs(resourceAsStreamName, type, properties, true);
    }

    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     * 默认闭流
     *
     * @param resourceAsStreamName 路径
     */
    public static Configs getConfigs(String resourceAsStreamName, String type) throws IOException {
        return getConfigs(resourceAsStreamName, type, null, true);
    }

    /**
     * 通过 {@link Class#getResourceAsStream(String)} 方法获取流对象
     * 默认闭流
     *
     * @param resourceAsStreamName 路径
     */
    public static Configs getConfigs(String resourceAsStreamName) throws IOException {
        return getConfigs(resourceAsStreamName, (Properties) null, true);
    }


}
