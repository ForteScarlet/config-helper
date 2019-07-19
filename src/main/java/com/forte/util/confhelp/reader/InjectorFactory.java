package com.forte.util.confhelp.reader;

import com.forte.util.confhelp.anno.Prop;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注入器工厂，此处缓存曾已经获取过的注入器
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class InjectorFactory {

    /** 记录所有的注入器 */
    private static final Map<Class<?>, Injector<?>> INJECTORS = new Injectors();

    /**
     * 获取一个注入器，如果没有则尝试创建
     */
    public static <T> Injector<T> getInjector(Class<T> type){
        Injector<T> injector = (Injector<T>) INJECTORS.get(type);
        if(injector == null){
            Prop.PropData[] instances = Prop.PropData.getInstances(type);
            injector = new Injector<>(instances, type);
            INJECTORS.put(type, injector);
            return injector;
        }else{
            return injector;
        }
    }






    /** 注入器列表，继承线程同步Map，暂时没什么意义，只是单纯的好看 */
    private static class Injectors extends ConcurrentHashMap<Class<?>, Injector<?>> {
    }
}
