package com.liao.web.controller.quartz;

import com.liao.common.annotation.Log;
import com.liao.common.core.R;
import com.liao.common.enums.BusinessType;
import com.liao.quartz.entity.SysJobLog;
import com.liao.quartz.service.SysJobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务调度日志Controller
 * 
 * @author LiAo
 * @date 2022-01-18
 */
@Validated
@RestController
@RequestMapping("/quartz/log")
@Api(tags = "定时任务调度日志")
public class SysJobLogController {

    @Autowired
    private SysJobLogService sysJobLogService;

    /**
     * 查询定时任务调度日志列表
     *
     * @param sysJobLog 条件
     * @return 定时任务调度日志集合
     */
    @GetMapping("/list")
    @ApiOperation("查询定时任务调度日志列表")
    public R list(SysJobLog sysJobLog){
        return R.success(sysJobLogService.selectSysJobLogList(sysJobLog));
    }

    /**
     * 查询SysJobLog数据
     *
     * @param jobLogIds SysJobLogid 集合
     * @return 结果
     */
    @ApiOperation("查询SysJobLog数据")
    @GetMapping(value = "/{jobLogIds}")
    public R getInfo(@PathVariable("jobLogIds") List<Long> jobLogIds) {
        return R.success(sysJobLogService.selectSysJobLogByIds(jobLogIds));
    }

    /**
     * 新增定时任务调度日志
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    @PostMapping
    @ApiOperation("新增定时任务调度日志")
    @Log(title = "定时任务调度日志" , businessType = BusinessType.INSERT)
    public R add(SysJobLog sysJobLog) {
        return R.r(sysJobLogService.insertSysJobLog(sysJobLog));
    }

    /**
     * 修改定时任务调度日志
     *
     * @param sysJobLog 定时任务调度日志
     * @return 结果
     */
    @PutMapping
    @ApiOperation("修改定时任务调度日志")
    @Log(title = "定时任务调度日志" , businessType = BusinessType.UPDATE)
    public R edit(SysJobLog sysJobLog) {
        return R.r(sysJobLogService.updateSysJobLog(sysJobLog));
    }

    /**
     * 删除定时任务调度日志
     *
     * @param jobLogIds SysJobLogid 集合
     * @return 结果
     */
    @DeleteMapping("/{jobLogIds}")
    @ApiOperation("删除定时任务调度日志")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    public R remove(@PathVariable("jobLogIds") List<Long> jobLogIds) {
        return R.r(sysJobLogService.deleteSysJobLogByIds(jobLogIds));
    }

    /**
     * 清空操作日志
     *
     * @return 结果
     */
    @DeleteMapping("clean")
    @ApiOperation("清空日志")
    @Log(title = "清空日志", businessType = BusinessType.CLEAN)
    public R clean() {
        sysJobLogService.clean();
        return R.success();
    }
}
