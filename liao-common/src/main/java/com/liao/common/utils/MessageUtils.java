package com.liao.common.utils;

import com.liao.common.utils.spring.SpringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * <p>
 * 获取i18n资源文件
 * </p>
 *
 * @author LiAo
 * @since 2021/12/3
 */
public class MessageUtils {


    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 根据消息键和参数 获取消息 委托给spring messageSource
     * @param args 参数
     * @returnc 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
