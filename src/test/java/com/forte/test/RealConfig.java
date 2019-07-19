package com.forte.test;

import com.forte.util.confhelp.anno.Prop;

/**
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@Prop(value = "config")
public class RealConfig extends BaseConfig {

    @Prop
    private String username;

    @Prop
    private String password;

    @Prop
    private double[] no;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double[] getNo() {
        return no;
    }

    public void setNo(double[] no) {
        this.no = no;
    }
}
