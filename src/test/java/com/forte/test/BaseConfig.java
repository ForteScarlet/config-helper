package com.forte.test;

import com.forte.util.confhelp.anno.Prop;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@Prop(value = "config", setterable = true)
public abstract class BaseConfig {

    @Prop(setterable = true)
    private String ip;

    @Prop(value = "path", setterable = true, setterName = "setPath")
    private String url;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        System.out.println("setIp!");
        this.ip = ip;
    }

    public void setPath(String path){
        System.out.println("setPath!");
        this.url = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
