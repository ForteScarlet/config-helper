package com.forte.util.confhelp.reader;

import com.forte.util.confhelp.properties.Properties;
import com.forte.util.confhelp.record.Records;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * XML的格式即为Configs中包含的全部Config <br>
 * ※ 根标签为 'robot' <br>
 * 例如：
 * <code>
 * &lt;robot&gt;
 * &lt;configs&gt;
 *      &lt;config name='a' value='1' /&gt;
 *      &lt;config name='b' value='2' /&gt;
 * &lt;/configs&gt;
 * &lt;/robot&gt;
 * </code>
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class XmlConfigs extends BaseConfigs {

    /** 根标签应为‘robot’ */
    private String rootElement;

    /** 配置的key */
    public static final String ROOT_ELEMENT_KEY = "rootElement";
    private static final String ROOT_ELEMENT_DEFAULT = "robot";

    /** 包含了配置的父类标签应为‘configs’ */
    private String configsFatherElement;

    /** 配置的key */
    public static final String CONFIGS_FATHER_ELEMENT_KEY = "configsFatherElement";
    private static final String CONFIGS_FATHER_ELEMENT_DEFAULT = "configs";

    /** 配置信息的标签应该为‘config’ */
    private String configElement;

    /** 配置的key */
    public static final String CONFIG_ELEMENT_KEY = "configElement";
    private static final String CONFIG_ELEMENT_DEFAULT = "config";


    /** 配置信息中代表name的信息 */
    private String attrName;

    public static final String ATTR_NAME_KEY = "attrName";
    private static final String ATTR_NAME_DEFAULT = "name";

    /** 配置信息中代表value的信息 */
    private  String attrValue;

    public static final String ATTR_VALUE_KEY = "attrValue";
    private static final String ATTR_VALUE_DEFAULT = "value";

    /**
     * 构造，通过输入流来保存数据
     */
    public XmlConfigs(InputStream inputStream, Properties properties) throws Exception {
        super(inputStream, properties);
    }

    /**
     * 通过输入流来读取数据
     *
     * @param inputStream 输入流
     * @return 转化为Map类型的参数
     */
    @Override
    protected Records read(InputStream inputStream, Properties properties) throws DocumentException {
        //初始化参数
        this.rootElement = properties.getString(ROOT_ELEMENT_KEY, ROOT_ELEMENT_DEFAULT);
        this.configsFatherElement = properties.getString(CONFIGS_FATHER_ELEMENT_KEY, CONFIGS_FATHER_ELEMENT_DEFAULT);
        this.configElement = properties.getString(CONFIG_ELEMENT_KEY, CONFIG_ELEMENT_DEFAULT);
        this.attrName = properties.getString(ATTR_NAME_KEY, ATTR_NAME_DEFAULT);
        this.attrValue = properties.getString(ATTR_VALUE_KEY, ATTR_VALUE_DEFAULT);


        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(inputStream);
        //获取root节点
        Element rootElement = doc.getRootElement();
        //判断是否符合规定
        if(!rootElement.getName().equals(this.rootElement)){
            throw new DocumentException("配置文件的根标签应为'<"+ this.rootElement +"></"+ this.rootElement +">'");
        }

        /** 获取configs标签下的全部元素，根据是否为config标签分组 */
        Map<Boolean, List<Element>> groupByConfigName = ((List<Element>) rootElement.elements(configsFatherElement))
                .stream().flatMap(e -> {
                    Stream<Element> configElements = e.elements().stream();
                    String faName = e.attributeValue(attrName);
                    if(faName != null){
                        //有父类名称，拼接
                        configElements = configElements.peek(ce -> {
                            if(ce.getName().equals(configElement)){
                                Attribute attribute = ce.attribute(attrName);
                                if(attribute == null){
                                    //没有key或者没有value
                                    throw new IllegalArgumentException(new DocumentException( configElement + "标签中缺少「"+ attrName +"」或「"+ attrValue +"」标签：" + e.asXML()));
                                }
                                attribute.setValue(faName + '.' + attribute.getValue());
                            }else{
                                //非config标签
                                ce.setName(faName + '.' + ce.getName());
                            }
                        });
                    }
                    return configElements;
                })
                .collect(Collectors.groupingBy(e -> e.getName().equals(configElement)));

        //保存数据，先从config数据开始保存
        Map<String, Object> dataMap = new LinkedHashMap<>(groupByConfigName.getOrDefault(true, Collections.emptyList()).size() + groupByConfigName.getOrDefault(false, Collections.emptyList()).size());
        for (Element e : groupByConfigName.getOrDefault(true, Collections.emptyList())) {
            String k = e.attributeValue(attrName);
            String v = e.attributeValue(attrValue);
            if(k == null || v == null){
                //没有key或者没有value
                throw new DocumentException("config标签中缺少「name」或「value」标签：" + e.asXML());
            }
            add(dataMap, k, v);
        }

        //然后保存非config数据
        for (Element e : groupByConfigName.getOrDefault(false, Collections.emptyList())) {
            //非config数据，标签名称为name，里面的text为值
            String v = e.getText();
            String k = e.getName();
            add(dataMap, k, v);
        }

        return toRecords(dataMap);
    }

    private void add(Map<String, Object> dataMap, String k, Object v){
            dataMap.merge(k, v, (old, val) -> {
                if(old instanceof List){
                    ((List) old).add(val);
                    return old;
                }else{
                    return new ArrayList(){{
                       add(old);
                       add(val);
                    }};
                }
            });
    }


    public String getRootElement() {
        return rootElement;
    }

    public String getConfigsFatherElement() {
        return configsFatherElement;
    }

    public String getConfigElement() {
        return configElement;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }
}


