package com.liao.web.controller.quartz;

import com.liao.common.annotation.Log;
import com.liao.common.constant.Constants;
import com.liao.common.core.R;
import com.liao.common.enums.BusinessType;
import com.liao.common.exception.job.TaskException;
import com.liao.common.utils.StringUtils;
import com.liao.quartz.entity.SysJob;
import com.liao.quartz.service.SysJobService;
import com.liao.quartz.util.CronUtils;
import com.liao.quartz.util.ScheduleUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务调度Controller
 *
 * @author LiAo
 * @date 2022-01-18
 */
@Validated
@RestController
@RequestMapping("/quartz/job")
@Api(tags = "定时任务调度")
public class SysJobController {

    @Autowired
    private SysJobService sysJobService;


    /**
     * 查询定时任务调度列表
     *
     * @param sysJob 条件
     * @return 定时任务调度集合
     */
    @GetMapping("/list")
    @ApiOperation("查询定时任务调度列表")
    public R list(SysJob sysJob) {
        return R.success(sysJobService.selectSysJobList(sysJob));
    }

    /**
     * 查询SysJob数据
     *
     * @param jobIds SysJobid 集合
     * @return 结果
     */
    @ApiOperation("查询SysJob数据")
    @GetMapping(value = "/{jobIds}")
    public R getInfo(@PathVariable("jobIds") List<Long> jobIds) {
        return R.success(sysJobService.selectSysJobByIds(jobIds));
    }

    /**
     * 新增定时任务调度
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    @PostMapping
    @ApiOperation("新增定时任务调度")
    @Log(title = "定时任务调度", businessType = BusinessType.INSERT)
    public R add(SysJob sysJob) throws TaskException, SchedulerException {

        if (!CronUtils.isValid(sysJob.getCronExpression())) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(sysJob.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget()
                , new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget()
                , new String[]{Constants.HTTP, Constants.HTTPS})) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(sysJob.getInvokeTarget())) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不在白名单内");
        }
        return R.r(sysJobService.insertSysJob(sysJob));
    }

    /**
     * 修改定时任务调度
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    @PutMapping
    @ApiOperation("修改定时任务调度")
    @Log(title = "定时任务调度", businessType = BusinessType.UPDATE)
    public R edit(SysJob sysJob) throws SchedulerException {

        if (!CronUtils.isValid(sysJob.getCronExpression())) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，Cron表达式不正确");
        } else if (StringUtils.containsIgnoreCase(sysJob.getInvokeTarget(), Constants.LOOKUP_RMI)) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget()
                , new String[]{Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS})) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget()
                , new String[]{Constants.HTTP, Constants.HTTPS})) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget(), Constants.JOB_ERROR_STR)) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(sysJob.getInvokeTarget())) {
            return R.error("新增任务'" + sysJob.getJobName() + "'失败，目标字符串不在白名单内");
        }

        return R.r(sysJobService.updateSysJob(sysJob));
    }

    /**
     * 定时任务立刻执行一次
     *
     * @param sysJob 任务内容部
     * @return 结果
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public R run(SysJob sysJob) throws SchedulerException {
        sysJobService.run(sysJob);
        return R.success();
    }

    /**
     * 删除定时任务调度
     *
     * @param jobIds SysJobid 集合
     * @return 结果
     */
    @DeleteMapping("/{jobIds}")
    @ApiOperation("删除定时任务调度")
    @Log(title = "定时任务调度", businessType = BusinessType.DELETE)
    public R remove(@PathVariable("jobIds") List<Long> jobIds) throws SchedulerException {
        sysJobService.deleteSysJobByIds(jobIds);
        return R.success();
    }


    /**
     * 定时任务状态修改
     *
     * @param job 任务
     * @return 结果
     */
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(SysJob job) throws SchedulerException {
        SysJob sysJob = sysJobService.selectSysJobById(job.getJobId());
        sysJob.setStatus(job.getStatus());
        return R.r(sysJobService.changeStatus(sysJob));
    }
}
