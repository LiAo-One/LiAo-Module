package com.liao.quartz.util;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * <p>
 * cron表达式工具类
 * </p>
 *
 * @author LiAo
 * @since 2022/1/18
 */
public class CronUtils {

    /**
     * 返回一个布尔值代表一个给定的Cron表达式的有效性
     *
     * @param cronExpression Cron表达式
     * @return boolean 表达式是否有效
     */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 获取cron表达式的下一个执行时间
     *
     * @param cronExpression 表达式
     * @return 时间
     */
    public static Date getNextExecution(String cronExpression) {

        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
