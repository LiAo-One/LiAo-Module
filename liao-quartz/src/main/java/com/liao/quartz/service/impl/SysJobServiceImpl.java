package com.liao.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.constant.ScheduleConstants;
import com.liao.common.core.page.PageUtils;
import com.liao.common.exception.check.MissingParametersException;
import com.liao.common.exception.job.TaskException;
import com.liao.common.utils.StringUtils;
import com.liao.quartz.dao.SysJobMapper;
import com.liao.quartz.entity.SysJob;
import com.liao.quartz.service.SysJobService;
import com.liao.quartz.util.ScheduleUtils;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 定时任务调度Service业务层处理
 *
 * @author LiAo
 * @date 2022-01-18
 */
@Service
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SysJobMapper sysJobMapper;

    @PostConstruct
    public void init() throws SchedulerException, TaskException {
        scheduler.clear();
        List<SysJob> sysJobs = sysJobMapper.selectList(null);
        for (SysJob job : sysJobs) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }


    /**
     * 查询定时任务调度列表
     *
     * @param sysJob 条件
     * @return 定时任务调度集合
     */
    @Override
    public IPage<SysJob> selectSysJobList(SysJob sysJob) {

        // 分页信息
        IPage<SysJob> page = PageUtils.startPage();

        // 构造条件
        QueryWrapper<SysJob> queryWrapper = getSysJobQueryWrapper(sysJob);

        // 返回结果
        return sysJobMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据id查询SysJob数据
     *
     * @param jobId jobId
     * @return 结果
     */
    @Override
    public SysJob selectSysJobById(Long jobId) {
        return sysJobMapper.selectById(jobId);
    }

    /**
     * 新增定时任务调度
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSysJob(SysJob sysJob) throws TaskException, SchedulerException {
        sysJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());

        int rows = sysJobMapper.insert(sysJob);

        if (rows > 0) {
            ScheduleUtils.createScheduleJob(scheduler, sysJob);
        }

        return rows;
    }

    /**
     * 根据id修改定时任务调度
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSysJob(SysJob sysJob) throws SchedulerException {
        if (StringUtils.isNull(sysJob.getJobId())) {
            throw new MissingParametersException("jobId");
        }

        int rows = sysJobMapper.updateById(sysJob);

        String status = sysJob.getStatus();

        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(sysJob);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(sysJob);
        }

        return rows;
    }

    /**
     * 根据id删除定时任务调度
     *
     * @param jobId 定时任务调度ID
     * @return 结果
     */
    @Override
    @Transactional
    public void deleteSysJobById(Long jobId) throws SchedulerException {

        if (StringUtils.isEmpty(jobId)) {
            throw new MissingParametersException("jobId");
        }

        SysJob sysJob = sysJobMapper.selectById(jobId);

        deleteJob(sysJob);
    }

    /**
     * 根据id批量查询定时任务调度
     *
     * @param jobIds 定时任务调度id结合
     * @return 结果
     */
    @Override
    public List<SysJob> selectSysJobByIds(List<Long> jobIds) {

        if (StringUtils.isEmpty(jobIds)) {
            throw new MissingParametersException("jobId");
        }

        return sysJobMapper.selectBatchIds(jobIds);
    }

    /**
     * 根据id批量删除定时任务调度
     *
     * @param jobIds 定时任务调度id集合
     * @return 结果
     */
    @Override
    @Transactional
    public void deleteSysJobByIds(List<Long> jobIds) throws SchedulerException {
        if (StringUtils.isEmpty(jobIds)) {
            throw new MissingParametersException("jobId");
        }

        List<SysJob> sysJobs = sysJobMapper.selectBatchIds(jobIds);

        for (SysJob sysJob : sysJobs) {
            deleteJob(sysJob);
        }
    }

    /**
     * 任务修改
     *
     * @param job 任务
     * @return 结果
     * @throws SchedulerException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysJob job) throws SchedulerException {
        int rows = 0;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = sysJobMapper.updateById(job);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    @Override
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = sysJobMapper.updateById(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(SysJob job) throws SchedulerException {
        SysJob sysJob = sysJobMapper.selectById(job.getJobId());

        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, sysJob);
        scheduler.triggerJob(ScheduleUtils.getJobKey(sysJob.getJobId(), sysJob.getJobGroup()), dataMap);
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    @Override
    public void deleteJob(SysJob job) throws SchedulerException {

        scheduler.deleteJob(ScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
    }

    /**
     * SysJob条件构建
     *
     * @param sysJob sysJob
     * @return 结果
     */
    @Override
    public QueryWrapper<SysJob> getSysJobQueryWrapper(SysJob sysJob) {

        // 构造条件
        QueryWrapper<SysJob> queryWrapper = new QueryWrapper<>();

        // 任务ID
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getJobId()), "job_id", sysJob.getJobId());
        // 任务名称
        queryWrapper.like(StringUtils.isNotNull(sysJob.getJobName()), "job_name", sysJob.getJobName());
        // 任务组名
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getJobGroup()), "job_group", sysJob.getJobGroup());
        // 调用目标字符串
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getInvokeTarget()), "invoke_target", sysJob.getInvokeTarget());
        // cron执行表达式
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getCronExpression()), "cron_expression", sysJob.getCronExpression());
        // 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getMisfirePolicy()), "misfire_policy", sysJob.getMisfirePolicy());
        // 是否并发执行（0允许 1禁止）
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getConcurrent()), "concurrent", sysJob.getConcurrent());
        // 状态（0正常 1暂停）
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getStatus()), "status", sysJob.getStatus());
        // 乐观锁
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getVersion()), "version", sysJob.getVersion());
        // 逻辑删除
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getDeleted()), "deleted", sysJob.getDeleted());
        // 创建时间
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getCreateTime()), "create_time", sysJob.getCreateTime());
        // 更新时间
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getUpdateTime()), "update_time", sysJob.getUpdateTime());
        // 备注信息
        queryWrapper.eq(StringUtils.isNotNull(sysJob.getRemark()), "remark", sysJob.getRemark());
        return queryWrapper;
    }
}
