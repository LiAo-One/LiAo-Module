package com.liao;

import com.liao.common.annotation.Excel;
import com.liao.system.entity.SysAdmin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2021/7/8
 */
public class Hello {

    public static void main(String[] args) {

        Class<SysAdmin> clazz = SysAdmin.class;

        // 存储类反射模板
        List<Field> tempFields = new ArrayList<>();
        // 获取反射类公共的、受保护的、默认的（包）访问和私有字段，但不包括继承的字段
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        for (Field tempField : tempFields) {
            if (tempField.isAnnotationPresent(Excel.class)) {
                Excel annotation = tempField.getAnnotation(Excel.class);
            }
        }
    }
}
