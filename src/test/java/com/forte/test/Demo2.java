package com.forte.test;


import com.forte.util.confhelp.ConfigsFactory;
import com.forte.util.confhelp.anno.Prop;
import com.forte.util.confhelp.reader.Configs;

import java.util.Arrays;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@Prop(value = "demo", nonNull = false)
public class Demo2 {

    public static void main(String[] args) throws Exception {

        Configs configs = ConfigsFactory.getConfigs("/robot.properties");

        RealConfig realConfig = new RealConfig();

        configs.inject(realConfig);

        System.out.println(Arrays.toString(realConfig.getNo()));
        System.out.println(realConfig.getUsername());
        System.out.println(realConfig.getPassword());
        System.out.println(realConfig.getIp());
        System.out.println(realConfig.getUrl());

    }

}
