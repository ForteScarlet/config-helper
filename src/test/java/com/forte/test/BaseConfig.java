package com.forte.test;

import com.forte.util.confhelp.anno.Prop;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@Prop
public abstract class BaseConfig {

    @Prop("config.ip")
    private String ip;

    @Prop("config.path")
    private String url;




}
