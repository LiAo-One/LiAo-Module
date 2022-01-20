package com.liao.quartz.util;

import com.liao.common.constant.Constants;
import com.liao.common.constant.ScheduleConstants;
import com.liao.common.utils.ExceptionUtil;
import com.liao.common.utils.StringUtils;
import com.liao.common.utils.bean.BeanUtils;
import com.liao.common.utils.spring.SpringUtils;
import com.liao.quartz.entity.SysJob;
import com.liao.quartz.entity.SysJobLog;
import com.liao.quartz.service.SysJobLogService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2022/1/18
 */
@Slf4j
public abstract class AbstractQuartzJob implements Job {

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysJob sysJob = new SysJob();

        BeanUtils.copyBeanProp(sysJob, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES));

        try {
            before(context, sysJob);

            if (sysJob != null) {
                doExecute(context, sysJob);
            }

            after(context, sysJob, null);
        } catch (Exception e) {
            log.error("任务异常    -  :", e);

            after(context, sysJob, e);
        }
    }


    /**
     * 执行前
     *
     * @param context 工作执行上下文
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context doExecute
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final SysJobLog sysJobLog = new SysJobLog();

        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setStopTime(new Date());

        long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();

        sysJobLog.setJobMessage(sysJobLog.getJobName() + "总耗时：" + runMs + "毫秒");

        if (e != null) {
            sysJobLog.setStatus(Constants.FAIL);
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        } else {
            sysJobLog.setStatus(Constants.SUCCESS);
        }

        // 写入数据库中
        SpringUtils.getBean(SysJobLogService.class).insertSysJobLog(sysJobLog);

    }


    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
