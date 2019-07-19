package com.forte.test;


import com.forte.util.confhelp.anno.Prop;

import java.lang.reflect.Field;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@Prop(value = "demo", nonNull = false)
public class Demo2 {

    public static void main(String[] args) throws Exception {

        Prop.PropData[] instances = Prop.PropData.getInstances(RealConfig.class);
        for (Prop.PropData instance : instances) {
            System.out.println(instance);
        }


    }

}
