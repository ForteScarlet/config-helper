package com.forte.util.confhelp.properties;

import com.forte.util.confhelp.reader.XmlConfigs;

/**
 * XML连接的时候使用的相关配置，区别在于提供静态方法
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class XmlConfigsProperties extends ConfigsProperties {

    /** 请使用静态工厂方法获取实例对象 */
    @Deprecated
    public XmlConfigsProperties(){}

    /**
     * 获取实例
     * @param rootElement 根标签的标签名
     * @param configsFatherElement  内部为配置数据的配置组标签
     * @param configElement 配置信息的标签，此标签会去获取参数的名称与参数的值
     * @param attrName  参数名的标签参数name
     * @param attrValue 参数值的标签参数name
     * @return  配置类型
     */
    public static XmlConfigsProperties getInstance(String rootElement, String configsFatherElement, String configElement, String attrName, String attrValue){
        return new XmlConfigsProperties(){{
           put(XmlConfigs.ROOT_ELEMENT_KEY, rootElement);
           put(XmlConfigs.CONFIGS_FATHER_ELEMENT_KEY, configsFatherElement);
           put(XmlConfigs.CONFIG_ELEMENT_KEY, configElement);
           put(XmlConfigs.ATTR_NAME_KEY, attrName);
           put(XmlConfigs.ATTR_VALUE_KEY, attrValue);
        }};
    }

    /**
     * 获取实例
     * 未提到的参数使用默认值
     * @param rootElement 根标签的标签名
     * @param configsFatherElement  内部为配置数据的配置组标签
     * @param configElement 配置信息的标签，此标签会去获取参数的名称与参数的值
     * @return  配置类型
     */
    public static XmlConfigsProperties getInstance(String rootElement, String configsFatherElement, String configElement){
        return getInstance(rootElement, configsFatherElement, configElement, null, null);
    }

    /**
     * 获取实例
     * 未提到的参数使用默认值
     * @param rootElement 根标签的标签名
     * @param configsFatherElement  内部为配置数据的配置组标签
     * @return  配置类型
     */
    public static XmlConfigsProperties getInstance(String rootElement, String configsFatherElement){
        return getInstance(rootElement, configsFatherElement, null, null, null);
    }

    /**
     * 获取实例
     * 未提到的参数使用默认值
     * @param rootElement 根标签的标签名
     * @return  配置类型
     */
    public static XmlConfigsProperties getInstance(String rootElement){
        return getInstance(rootElement, null, null, null, null);
    }


}
