package com.forte.util.confhelp.reader;

import com.forte.util.confhelp.ConfigsFactory;
import com.forte.util.confhelp.anno.Prop;
import com.forte.util.confhelp.exception.ConfigsHelperException;
import com.forte.util.confhelp.record.Data;
import com.forte.util.confhelp.util.FieldUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 配置注入器
 * 注入器一般会针对一个类型构建并记入缓存，不应当重复创建
 *
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
public class Injector<T> {

    /**
     * 注解信息
     */
    private final Prop.PropData[] propDatas;

    /**
     * 注解信息中，每一个字段对应的setter方法
     */
    private final Map<Field, BiConsumer<Configs, Object>> setterMap;

    /**
     * 所属的类型
     */
    private final Class<T> type;

    /**
     * 构造
     * TODO 测试
     */
    Injector(Prop.PropData[] propDatas, Class<T> type) {
        this.propDatas = propDatas;
        this.type = type;
//        setterMap = new HashMap<>(propDatas.length);
        //构建注入器
        setterMap = Arrays.stream(propDatas).map(pd -> {
            //赋值函数
            BiConsumer<Configs, Object> setter = getSetterConsumer(pd);
            return new AbstractMap.SimpleEntry<>(pd.getField(), setter);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    /**
     * 获取字段值注入函数
     */
    private static BiConsumer<Configs, Object> getSetterConsumer(Prop.PropData propData) {
        Class<?> from = propData.getFrom();
        Field field = propData.getField();
        //判断是否需要通过Setter注入
        BiConsumer<Configs, Object> consumer;
        if (propData.setterable()) {
            //需要通过setter函数注入
            //获取setter方法
            Method fieldSetter;
            String setterName = propData.setterName();
            if (setterName == null || setterName.trim().length() == 0) {
                fieldSetter = FieldUtils.getFieldSetter(from, field);
                if (fieldSetter == null) {
                    throw new ConfigsHelperException("无法获取setter方法");
                }
            } else {
                try {
                    fieldSetter = from.getMethod(setterName, field.getType());
                } catch (NoSuchMethodException e) {
                    throw new ConfigsHelperException("无法获取方法：" + setterName + "(" + field.getType() + ")", e);
                }
            }

            //根据setter的注入函数
            consumer = (c, b) -> {
                //通过setter方法注入
                //获取配置信息
                Data data = c.getData(propData.value());
                Object value = data == null ? null : data.getValue();
                //判断当前是否可以注入
                boolean canInject = true;
                //如果不可为null的时候值为null了，则不可注入
                if (propData.nonNull() && (value == null)) {
                    canInject = false;
                }
                //如果可以注入，执行注入
                if (canInject) {
                    Class<?> fieldType = field.getType();
                    Object fieldValue = toValue(value, fieldType, propData.split(), field);
                    //执行方法
                    try {
                        fieldSetter.invoke(b, fieldValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ConfigsHelperException("使用setter注入参数出现异常：", e);
                    }
//                    }
                }
            };
        } else {
            //不需要setter函数
            consumer = (c, b) -> {
                Data data = c.getData(propData.value());
                Object value = data == null ? null : data.getValue();
                //判断当前是否可以注入
                boolean canInject = true;
                //如果不可为null的时候值为null了，则不可注入
                if (propData.nonNull() && (value == null)) {
                    canInject = false;
                }
                //如果可以注入，执行注入
                if (canInject) {
                    //将字段打开
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    Object fieldValue = toValue(value, fieldType, propData.split(), field);
                    //赋值
                    try {
                        field.set(b, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new ConfigsHelperException("无法为字段" + field + "赋值: " + fieldValue, e);
                    }

                    field.setAccessible(false);
                }
            };
        }


        return consumer;
    }


    /**
     * 尝试将获取到的参数直接转化为需要的参数类型
     *
     * @param value 获取的参数类型
     * @param needs 转化后的参数类型
     * @param split 如果需要切割，此为切割符
     * @param field 当参数为list的时候，通过field对象来获取list类型的泛型类型
     *              TODO field参数与needs参数重复，考虑删除needs
     * @param <T>   获取的参数类型
     * @param <N>   转化后的参数类型
     */
    private static <T, N> N toValue(T value, Class<N> needs, String split, Field field) {
        if (value == null) {
            return null;
        } else {
            //如果类型相同或为子类，直接转化并返回
            if (FieldUtils.isChild(value, needs)) {
                return (N) value;
            }

            //如果需要的参数为数组或者collection类型
            if (needs.isArray() || FieldUtils.isChild(needs, List.class)) {
                //如果传入的参数也是数组类型或者collection类型，则进一步判断
                if (value.getClass().isArray() || FieldUtils.isChild(value, List.class)) {
                    if (FieldUtils.isChild(value, needs)) {
                        return (N) value;
                    } else {
                        //可能是类型交错
                        if (value.getClass().isArray() && FieldUtils.isChild(needs, List.class)) {
                            //值为数组，需要list
                            //将数组转化为List
                            //获取list的泛型类型并尝试转化
                            Class listFieldGeneric;
                            Class arrayGeneric = FieldUtils.getArrayGeneric((Object[]) value);
                            try {
                                listFieldGeneric = FieldUtils.getListFieldGeneric(field);
                            } catch (ClassNotFoundException e) {
                                throw new ConfigsHelperException("无法确认字段的泛型信息：" + field, e);
                            }
                            //如果数组类型为list类型或子类型，直接转化，否则尝试强转
                            if (FieldUtils.isChild(arrayGeneric, listFieldGeneric)) {
                                return (N) Arrays.stream((Object[]) value).collect(Collectors.toList());
                            } else {
                                return (N) Arrays.stream((Object[]) value)
                                        .map(a -> ConvertUtils.convert(a, listFieldGeneric))
                                        .collect(Collectors.toList());
                            }
                        } else if (FieldUtils.isChild(value, List.class) && needs.isArray()) {
                            //值为list，需要数组
                            //将List转化为数组
                            //增加三个基础数据类型的判断
                            Class arrayGeneric = FieldUtils.getArrayGeneric(needs);
                            if(arrayGeneric.equals(int.class)){
                                return (N) ((List) value).stream().mapToInt(la -> {
                                    if (!FieldUtils.isChild(la, int.class)) {
                                        return (int) ConvertUtils.convert(la, int.class);
                                    } else {
                                        return (int) la;
                                    }
                                }).toArray();
                            }else if(arrayGeneric.equals(double.class)){
                                return (N) ((List) value).stream().mapToDouble(la -> {
                                    if (!FieldUtils.isChild(la, double.class)) {
                                        return (double) ConvertUtils.convert(la, double.class);
                                    } else {
                                        return (double) la;
                                    }
                                }).toArray();
                            }else if(arrayGeneric.equals(long.class)){
                                return (N) ((List) value).stream().mapToDouble(la -> {
                                    if (!FieldUtils.isChild(la, long.class)) {
                                        return (long) ConvertUtils.convert(la, long.class);
                                    } else {
                                        return (long) la;
                                    }
                                }).toArray();
                            }else{
                                return (N) ((List) value).stream().map(la -> {
                                    if (!FieldUtils.isChild(la, arrayGeneric)) {
                                        return ConvertUtils.convert(la, arrayGeneric);
                                    } else {
                                        return la;
                                    }
                                }).toArray(i -> Array.newInstance(needs, i));
                            }
                        } else {
                            //其他情况, 直接抛出异常
                            throw new ConfigsHelperException("无法确定两个类型之前的转化形式：" + value.getClass() + " <-> " + needs);
                        }
                    }
                } else {
                    //传入的参数不是List类型或者数组，则判断是否为String类型，如果是，进行切割
                    String valueString = FieldUtils.isChild(value, String.class) ? (String) value : String.valueOf(value);
                    String[] valueSplit = valueString.split(split);
                    //尝试将String类型转化为所需要的类型
                    if (String[].class.equals(needs)) {
                        return (N) valueSplit;
                    } else if (FieldUtils.isChild(needs, List.class)) {
                        //数组转化为集合类型
                        //尝试获取泛型类型，如果获取失败则抛出异常
                        Class<?> listFieldGeneric;
                        try {
                            listFieldGeneric = FieldUtils.getListFieldGeneric(field);
                        } catch (ClassNotFoundException e) {
                            throw new ConfigsHelperException("无法确认字段的泛型信息：" + field, e);
                        }
                        //转化类型并返回
                        return (N) Arrays.stream(valueSplit).map(v -> ConvertUtils.convert(v, listFieldGeneric)).collect(Collectors.toList());
                    } else {
                        //否则，是类型不同的数组类型，获取数组的类型
                        Class arrayGeneric = FieldUtils.getArrayGeneric(needs);
                        //尝试转化并返回-如果与3大基础数据类型有关，则使用基本数据类型，需要特殊判断
                        if(arrayGeneric.equals(int.class)){
                            return (N) Arrays.stream(valueSplit).mapToInt(v -> (int) ConvertUtils.convert(v, int.class)).toArray();
                        }else if(arrayGeneric.equals(double.class)){
                            return (N) Arrays.stream(valueSplit).mapToDouble(v -> (double) ConvertUtils.convert(v, double.class)).toArray();
                        }else if(arrayGeneric.equals(long.class)){
                            return (N) Arrays.stream(valueSplit).mapToLong(v -> (long) ConvertUtils.convert(v, long.class)).toArray();
                        }else{
                            return (N) Arrays.stream(valueSplit).map(v -> ConvertUtils.convert(v, arrayGeneric)).toArray(i -> (Object[]) Array.newInstance(needs, i));
                        }
                    }
                }
            } else {
                //如果需要的参数不是数组类型，判断获取的值是什么类型
                if (FieldUtils.isChild(value, List.class) || value.getClass().isArray()) {
                    //如果获取的值是数组或者list类型，先转为String类型
                    String valueString;
                    if (FieldUtils.isChild(value, List.class)) {
                        valueString = value.toString();
                    } else {
                        valueString = Arrays.toString((Object[]) value);
                    }

                    //判断类型，如果也是String，直接赋值，否则尝试转化
                    if (needs.equals(String.class)) {
                        return (N) valueString;
                    } else {
                        return (N) ConvertUtils.convert(valueString, needs);
                    }

                } else {
                    //都是未知类型，尝试转化
                    return (N) ConvertUtils.convert(value, needs);
                }

            }

        }
    }


    /**
     * 执行注入
     *
     * @param config 配置对象
     * @param bean   被注入的对象
     */
    public void inject(Configs config, T bean) {
        //遍历所有注解信息，并进行注入
        //如果此字段获取不到，不做任何操作
        Arrays.stream(propDatas).forEach(pd -> setterMap.getOrDefault(pd.getField(), (c, b) -> {
        }).accept(config, bean));
    }


    public Prop.PropData[] getPropDatas() {
        return propDatas;
    }

    public Class<T> getType() {
        return type;
    }
}
