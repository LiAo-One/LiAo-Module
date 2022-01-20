package com.liao.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.core.page.PageUtils;
import com.liao.common.exception.check.MissingParametersException;
import com.liao.quartz.entity.SysJobLog;
import com.liao.quartz.service.SysJobLogService;
import com.liao.common.utils.StringUtils;
import com.liao.quartz.dao.SysJobLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 定时任务调度日志Service业务层处理
 *
 * @author LiAo
 * @date 2022-01-18
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

    @Autowired
    private SysJobLogMapper sysJobLogMapper;

    /**
     * 查询定时任务调度日志列表
     *
     * @param sysJobLog 条件
     * @return 定时任务调度日志集合
     */
    @Override
    public IPage<SysJobLog> selectSysJobLogList(SysJobLog sysJobLog) {

        // 分页信息
        IPage<SysJobLog> page = PageUtils.startPage();

        // 构造条件
        QueryWrapper<SysJobLog> queryWrapper = getSysJobLogQueryWrapper(sysJobLog);

        // 返回结果
        return  sysJobLogMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据id查询SysJobLog数据
     *
     * @param jobLogId jobLogId
     * @return 结果
     */
    @Override
    public SysJobLog selectSysJobLogById(Long jobLogId) {
        return sysJobLogMapper.selectById(jobLogId);
    }

    /**
     * 新增定时任务调度日志
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSysJobLog(SysJobLog sysJobLog){
        return sysJobLogMapper.insert(sysJobLog);
    }

    /**
     * 根据id修改定时任务调度日志
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSysJobLog(SysJobLog sysJobLog){
        if (StringUtils.isNull(sysJobLog.getJobLogId())) {
            throw new MissingParametersException("jobLogId");
        }

        return sysJobLogMapper.updateById(sysJobLog);
    }

    /**
     * 根据id删除定时任务调度日志
     *
     * @param jobLogId 定时任务调度日志ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysJobLogById(Long jobLogId){

        if (StringUtils.isEmpty(jobLogId)) {
            throw new MissingParametersException("jobLogId");
        }

        return sysJobLogMapper.deleteById(jobLogId);
    }

    /**
     * 根据id批量查询定时任务调度日志
     *
     * @param jobLogIds 定时任务调度日志id结合
     * @return 结果
     */
    @Override
    public List<SysJobLog> selectSysJobLogByIds(List<Long> jobLogIds){

        if (StringUtils.isEmpty(jobLogIds)) {
            throw new MissingParametersException("jobLogId");
        }

        return sysJobLogMapper.selectBatchIds(jobLogIds);
    }

    /**
     * 根据id批量删除定时任务调度日志
     *
     * @param jobLogIds 定时任务调度日志id集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysJobLogByIds(List<Long> jobLogIds){
        if (StringUtils.isEmpty(jobLogIds)) {
            throw new MissingParametersException("jobLogId");
        }

        return sysJobLogMapper.deleteBatchIds(jobLogIds);
    }

    /**
     * 清空数据
     */
    @Override
    public void clean() {
        sysJobLogMapper.cleanSysJobLog();
    }

    /**
     * SysJobLog条件构建
     *
     * @param sysJobLog sysJobLog
     * @return 结果
     */
    @Override
    public QueryWrapper<SysJobLog> getSysJobLogQueryWrapper(SysJobLog sysJobLog){

        // 构造条件
        QueryWrapper<SysJobLog> queryWrapper = new QueryWrapper<>();

        // 任务日志ID
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getJobLogId()), "job_log_id", sysJobLog.getJobLogId());
        // 任务名称
        queryWrapper.like(StringUtils.isNotNull(sysJobLog.getJobName()), "job_name", sysJobLog.getJobName());
        // 任务组名
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getJobGroup()), "job_group", sysJobLog.getJobGroup());
        // 调用目标字符串
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getInvokeTarget()), "invoke_target", sysJobLog.getInvokeTarget());
        // 日志信息
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getJobMessage()), "job_message", sysJobLog.getJobMessage());
        // 执行状态（0正常 1失败）
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getStatus()), "status", sysJobLog.getStatus());
        // 异常信息
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getExceptionInfo()), "exception_info", sysJobLog.getExceptionInfo());
        // 创建时间
        queryWrapper.eq(StringUtils.isNotNull(sysJobLog.getCreateTime()), "create_time", sysJobLog.getCreateTime());
        return queryWrapper;
    }
}
