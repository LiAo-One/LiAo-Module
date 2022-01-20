package com.liao.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>
 * 错误信息处理类
 * </p>
 *
 * @author LiAo
 * @since 2022/1/18
 */
public class ExceptionUtil {

    /**
     * 获取exception的详细错误信息。
     *
     * @param e 错误
     * @return 格式化后的信息
     */
    public static String getExceptionMessage(Throwable e) {
        StringWriter sw = new StringWriter();

        e.printStackTrace(new PrintWriter(sw, true));

        return sw.toString();
    }
}
