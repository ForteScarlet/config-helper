package com.forte.test;

import com.forte.util.confhelp.reader.Configs;
import com.forte.util.confhelp.reader.XmlConfigs;

import java.io.InputStream;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class Demo1 {

    public static void main(String[] args) throws Exception {
        InputStream resourceAsStream = Demo1.class.getResourceAsStream("/robot.xml");



        Configs configs = new XmlConfigs(resourceAsStream, null);
        configs.forEach(System.out::println);


    }

}
