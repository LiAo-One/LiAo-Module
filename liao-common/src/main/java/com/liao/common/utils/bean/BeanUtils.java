package com.liao.common.utils.bean;

import java.util.regex.Pattern;

/**
 * <p>
 * Bean 工具类
 * </p>
 *
 * @author LiAo
 * @since 2022/1/18
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * Bean方法名中属性名开始的下标
     */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /**
     * 匹配getter方法的正则表达式
     */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /**
     * 匹配setter方法的正则表达式
     */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     *
     * @param dest 目标对象
     * @param src  源对象
     */
    public static void copyBeanProp(Object dest, Object src) {
        try {
            // 将给定源 bean 的属性值复制到目标 bean。
            copyProperties(src, dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
