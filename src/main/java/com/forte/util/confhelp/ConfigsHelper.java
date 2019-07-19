package com.forte.util.confhelp;

import com.forte.util.confhelp.exception.ConfigsHelperException;
import com.forte.util.confhelp.reader.Configs;
import com.forte.util.confhelp.reader.Injector;
import com.forte.util.confhelp.reader.InjectorFactory;

/**
 * 配置类工具
 * 通过文件读取来对配置类的内容进行配置
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class ConfigsHelper {


    /**
     * 根据Configs对象进行注入
     */
    public static void getConfigs(Configs configs, Object configBean){
        Injector<?> injector = InjectorFactory.getInjector(configBean.getClass());
        if(injector == null){
            throw new ConfigsHelperException("无法获取"+ configBean.getClass() +"的注入器");
        }

    }


}
