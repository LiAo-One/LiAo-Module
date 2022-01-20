package com.liao.quartz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.exception.job.TaskException;
import com.liao.quartz.entity.SysJob;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * 定时任务调度Service接口
 *
 * @author LiAo
 * @date 2022-01-18
 */
public interface SysJobService extends IService<SysJob> {

    /**
     * 查询定时任务调度列表
     *
     * @param sysJob 条件
     * @return 定时任务调度集合
     */
    IPage<SysJob> selectSysJobList(SysJob sysJob);

    /**
     * 根据id查询SysJob数据
     *
     * @param jobId jobId
     * @return 结果
     */
    SysJob selectSysJobById(Long jobId);

    /**
     * 新增定时任务调度
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    int insertSysJob(SysJob sysJob) throws SchedulerException, TaskException;

    /**
     * 根据id修改定时任务调度
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    int updateSysJob(SysJob sysJob) throws SchedulerException;

    /**
     * 根据id删除定时任务调度
     *
     * @param jobId 定时任务调度ID
     * @return 结果
     */
    void deleteSysJobById(Long jobId) throws SchedulerException;

    /**
     * 根据id批量查询定时任务调度
     *
     * @param jobIds 定时任务调度id结合
     * @return 结果
     */
    List<SysJob> selectSysJobByIds(List<Long> jobIds);

    /**
     * 根据id批量删除定时任务调度
     *
     * @param jobIds 定时任务调度id集合
     * @return 结果
     */
    void deleteSysJobByIds(List<Long> jobIds) throws SchedulerException;


    /**
     * 任务秀海
     *
     * @param job 任务
     * @return 结果
     * @throws SchedulerException
     */
    int changeStatus(SysJob job) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int resumeJob(SysJob job) throws SchedulerException;

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int pauseJob(SysJob job) throws SchedulerException;

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     * @return 结果
     */
    void run(SysJob job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    void deleteJob(SysJob job) throws SchedulerException;

    /**
     * SysJob条件构建
     *
     * @param sysJob 定时任务调度
     * @return 结果
     */
    QueryWrapper<SysJob> getSysJobQueryWrapper(SysJob sysJob);

}
