package com.liao.quartz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.liao.common.utils.StringUtils;
import com.liao.quartz.util.CronUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * <p>
 * 定时任务调度
 * </p>
 *
 * @author LiAo
 * @since 2022-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysJob对象", description = "定时任务调度")
public class SysJob {

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_id", type = IdType.ID_WORKER)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "任务ID")
    private Long jobId;

    @TableId(value = "job_name", type = IdType.ID_WORKER)
    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @TableId(value = "job_group", type = IdType.ID_WORKER)
    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    @ApiModelProperty(value = "调用目标字符串")
    private String invokeTarget;

    @ApiModelProperty(value = "cron执行表达式")
    private String cronExpression;

    @ApiModelProperty(value = "计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private String misfirePolicy;

    @ApiModelProperty(value = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    @ApiModelProperty(value = "状态（0正常 1暂停）")
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(select = false)
    @Version
    @ApiModelProperty(value = "乐观锁")
    private Long version;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(select = false)
    @TableLogic
    @ApiModelProperty(value = "逻辑删除")
    private Long deleted;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(cronExpression)) {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("jobId", getJobId())
                .append("jobName", getJobName())
                .append("jobGroup", getJobGroup())
                .append("cronExpression", getCronExpression())
                .append("nextValidTime", getNextValidTime())
                .append("misfirePolicy", getMisfirePolicy())
                .append("concurrent", getConcurrent())
                .append("status", getStatus())
                .append("version", getVersion())
                .append("deleted", getDeleted())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }

}
