package com.liao.quartz.task;

import org.springframework.stereotype.Component;

/**
 * <p>
 * 定时任务调度测试
 * </p>
 *
 * @author LiAo
 * @since 2022/1/19
 */
@Component("laTask")
public class LATask {

    public void laNoParams() {
        System.out.println("执行无参方法");
    }
}
