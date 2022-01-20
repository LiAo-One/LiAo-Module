package com.liao.quartz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liao.quartz.entity.SysJobLog;
import org.apache.ibatis.annotations.Update;

/**
 * 定时任务调度日志Mapper接口
 * 
 * @author LiAo
 * @date 2022-01-18
 */
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    @Update("truncate table sys_job_log")
    void cleanSysJobLog();
}
