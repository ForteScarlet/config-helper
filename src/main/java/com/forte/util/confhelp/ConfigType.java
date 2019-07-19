package com.forte.util.confhelp;

/**
 * 默认中的配置类型
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public enum ConfigType {

    /** yaml格式的数据 */
    YAML("yaml", "yml"),
    /** xml格式的数据 */
    XML("xml"),
    /** properties格式的数据 */
    PROPERTIES("properties")
    ;

    /** 类型字符串 */
    public final String[] TYPES;

    /** 构造 */
    ConfigType(String... types){
        this.TYPES = types;
    }

    /**
     * 判断类型是否相同
     */
    public boolean equals(String type){
        if(type == null || type.trim().length() == 0){
            return false;
        }

        for (String s : TYPES) {
            if(s.equalsIgnoreCase(type)){
                return true;
            }
        }
        return false;
    }



}
