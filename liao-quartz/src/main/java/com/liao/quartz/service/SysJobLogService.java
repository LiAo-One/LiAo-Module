package com.liao.quartz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.core.R;
import com.liao.quartz.entity.SysJobLog;

import java.util.List;

/**
 * 定时任务调度日志Service接口
 *
 * @author LiAo
 * @date 2022-01-18
 */
public interface SysJobLogService extends IService<SysJobLog> {

    /**
     * 查询定时任务调度日志列表
     *
     * @param sysJobLog 条件
     * @return 定时任务调度日志集合
     */
    IPage<SysJobLog> selectSysJobLogList(SysJobLog sysJobLog);

    /**
     * 根据id查询SysJobLog数据
     *
     * @param jobLogId jobLogId
     * @return 结果
     */
     SysJobLog selectSysJobLogById(Long jobLogId);

    /**
     * 新增定时任务调度日志
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    int insertSysJobLog(SysJobLog sysJobLog);

    /**
     * 根据id修改定时任务调度日志
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    int updateSysJobLog(SysJobLog sysJobLog);

    /**
     * 根据id删除定时任务调度日志
     *
     * @param jobLogId 定时任务调度日志ID
     * @return 结果
     */
    int deleteSysJobLogById(Long jobLogId);

    /**
     * 根据id批量查询定时任务调度日志
     *
     * @param jobLogIds 定时任务调度日志id结合
     * @return 结果
     */
    List<SysJobLog> selectSysJobLogByIds(List<Long> jobLogIds);

    /**
     * 根据id批量删除定时任务调度日志
     *
     * @param jobLogIds 定时任务调度日志id集合
     * @return 结果
     */
    int deleteSysJobLogByIds(List<Long> jobLogIds);

    /**
     * 清空数据
     */
    void clean();

    /**
     * SysJobLog条件构建
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    QueryWrapper<SysJobLog> getSysJobLogQueryWrapper(SysJobLog sysJobLog);

}
