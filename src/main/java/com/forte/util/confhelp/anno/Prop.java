package com.forte.util.confhelp.anno;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * 使用此注解来标记一个参数的对应配置路径（名称）
 * 对应名称格式类似于包名格式
 * 用在类上的时候，类下的所有Prop注解的
 * <ul>
 *     <li>{@link #nonNull()}</li>
 *     <li>{@link #setterable()}</li>
 * </ul>
 * 均使用类级注解
 * @author ForteScarlet <[email]ForteScarlet@163.com>
 * @since JDK1.8
 **/
@Retention(RetentionPolicy.RUNTIME)	//注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD, ElementType.TYPE}) //接口、类、枚举、注解
@Inherited //注解可以被子类继承
public @interface Prop {

    /** 配置类对应名称, 类上注解会与类下字段进行拼接 */
    String value() default "";

    /** 如果想把字符串类型切割为字符串数组类型，指定切割符 */
    String split() default ",";

    /** 只有当获取到的配置信息不为null的时候才进行注入 */
    boolean nonNull() default true;

    /** 是否通过setter进行注入，默认为false */
    boolean setterable() default false;

    /** 如果{@link #setterable()} 为true，则此处可以指定, 参数只能存在一个，且尽可能为String。
     *  如果存在多个参数唯一的setter方法，则优先获取参数为String的。
     *      如果有多个String参数的，或者有多个参数唯一的但是没有String参数的，则抛出异常
     * */
    String setterName() default "";


    /**
     * 注解{@link Prop} 的参数封装类
     */
    class PropData {
        /** 所属的Field字段 */
        private Field field;
        private Class<?> from;
        private String value;
        private String split;
        private boolean nonNull;
        private boolean setterable;
        private String setterName;

        /** 默认值信息， 不可以直接作为返回值 */
        private static final PropData DEFAULT = new PropData("", ",", true, false, "");

        /**
         * 构造
         * @param value         配置对应的名称
         * @param split         如果需要切割，切割默认值
         * @param nonNull       是否当不为null的时候再注入
         * @param setterable
         * @param setterName
         */
        private PropData(String value, String split, boolean nonNull, boolean setterable, String setterName) {
            this.value = value;
            this.split = split;
            this.nonNull = nonNull;
            this.setterable = setterable;
            this.setterName = setterName;
        }
        /**
         * 构造
         * @param value         配置对应的名称
         * @param split         如果需要切割，切割默认值
         * @param nonNull       是否当不为null的时候再注入
         * @param setterable
         * @param setterName
         */
        public PropData(Field field, Class<?> from, String value, String split, boolean nonNull, boolean setterable, String setterName) {
            this(value, split, nonNull, setterable, setterName);
            this.field = field;
            this.from = from;
        }



        /**
         * 根据注解参数获取实例
         */
        public static PropData getInstance(Prop prop, Field field, Class<?> from){
            return prop == null ? null : new PropData(
                    field,
                    from,
                    prop.value(),
                    prop.split(),
                    prop.nonNull(),
                    prop.setterable(),
                    prop.setterName()
            );
        }

        /**
         * 根据字段和所在类获取注解参数对象
         */
        public static PropData getInstance(Class<?> from, Field field){
            Objects.requireNonNull(from);
            Objects.requireNonNull(field);

            Prop classAnnotation = from.getAnnotation(Prop.class);
            Prop fieldAnnotation = field.getAnnotation(Prop.class);

            //准备参数
            String value = DEFAULT.value;
            String split = DEFAULT.split;
            boolean nonNull = DEFAULT.nonNull;
            boolean setterable = DEFAULT.setterable;
            String setterName = DEFAULT.setterName;

            PropData propData = null;

            //获取参数
            if(classAnnotation == null){
                if(fieldAnnotation == null){
                    //都没有
                    //所有值均使用默认值，value值使用类路径+类名(开头小写)+字段名
//                    value = from.getPackage().getName() + '.' + FieldUtils.headLower(from.getSimpleName()) + '.' + field.getName();
                    //如果都没有直接返回null，不再使用默认值
                    return null;
                }else{
                    //只有字段注解
                    //直接使用字段注解
                    propData = PropData.getInstance(fieldAnnotation, field, from);
                }
            }else{
                if(fieldAnnotation == null){
                    //只有类上注解
                    //value为类注解value + '.' + 字段名
                    //setterable、getterable、nonNull使用类注解，其余的使用默认值
                    String classValue = classAnnotation.value();
                    value = (classValue.trim().length() == 0 ? "" : (classValue  + '.')) + field.getName();
                    setterable = classAnnotation.setterable();
                    nonNull = classAnnotation.nonNull();
                    split = classAnnotation.split();
                }else{
                    //都有
                    //value为类上 + '.' + 字段上
                    //setterable、getterable、nonNull、split使用类上的，其余的使用字段上自己的
                    String propValue = fieldAnnotation.value();
                    String classValue = classAnnotation.value();
                    value = (classValue.trim().length() == 0 ? "" : classValue + '.') + (propValue.trim().length() == 0 ? field.getName() : propValue);
                    setterable = classAnnotation.setterable();
                    nonNull =    classAnnotation.nonNull();
                    split =      classAnnotation.split();

                    setterName = fieldAnnotation.setterName();
                }
            }

            if(propData == null) {
                propData = new PropData(
                        field,
                        from,
                        value,
                        split,
                        nonNull,
                        setterable,
                        setterName
                );
            }

            return propData;
        }

        /**
         * 获取类中所有的字段注解对象
         * 递归子类
         */
        public static PropData[] getInstances(Class<?> from){
            if(from == null){
                return new PropData[0];
            }
            //获取全部字段列表
            Field[] declaredFields = from.getDeclaredFields();
            Stream<PropData> thisClassProp = Arrays.stream(declaredFields).map(f -> getInstance(from, f));
            return Stream.concat(thisClassProp, Arrays.stream(getInstances(from.getSuperclass())))
                    .filter(Objects::nonNull)
                    .toArray(PropData[]::new);
        }


        public String value() {
            return value;
        }

        public String split() {
            return split;
        }

        public boolean nonNull() {
            return nonNull;
        }

        public boolean setterable() {
            return setterable;
        }

        public String setterName() {
            return setterName;
        }

        public void value(String value) {
            this.value = value;
        }

        public void split(String split) {
            this.split = split;
        }

        public void nonNull(boolean nonNull) {
            this.nonNull = nonNull;
        }

        public void setterable(boolean setterable) {
            this.setterable = setterable;
        }

        public void setterName(String setterName) {
            this.setterName = setterName;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Class<?> getFrom() {
            return from;
        }

        public void setFrom(Class<?> from) {
            this.from = from;
        }

        @Override
        public String toString() {
            return "PropData{" +
                    "value='" + value + '\'' +
                    ", split='" + split + '\'' +
                    ", nonNull=" + nonNull +
                    ", setterable=" + setterable +
                    ", setterName='" + setterName + '\'' +
                    '}';
        }
    }



}
