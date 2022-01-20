package com.liao.quartz.util;

import com.liao.quartz.entity.SysJob;
import org.quartz.JobExecutionContext;

/**
 * <p>
 * 定时任务处理（允许并发执行）
 * </p>
 *
 * @author LiAo
 * @since 2022/1/18
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行异常
     */
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
