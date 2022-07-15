package com.liao.common.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liao.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 管理员
 * </p>
 *
 * @author LiAo
 * @since 2020-12-17
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysAdmin对象", description = "管理员")
public class SysAdmin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Excel(name = "主键", type = Excel.Type.EXPORT)
    @TableId(value = "admin_id", type = IdType.ID_WORKER)
    private Long adminId;

    @Excel(name = "账户")
    @ApiModelProperty(value = "账户")
    private String adminAccount;

    @Excel(name = "密码")
    @ApiModelProperty(value = "密码")
    // @TableField(select = false)
    private String adminPassword;

    @Excel(name = "姓名")
    @ApiModelProperty(value = "姓名")
    private String adminName;

    @Excel(name = "性别")
    @ApiModelProperty(value = "性别")
    private String adminSex;

    @ApiModelProperty(value = "头像连接")
    private String adminAvatar;

    @Excel(name = "邮箱")
    @ApiModelProperty(value = "邮箱")
    private String adminEmail;

    @Excel(name = "昵称")
    @ApiModelProperty(value = "昵称")
    private String adminNickname;

    @ApiModelProperty(value = "备注")
    private String adminRemarks;

    @Version
    @TableField(select = false)
    @ApiModelProperty(value = "乐观锁")
    private Integer version;

    @TableLogic
    @TableField(select = false)
    @ApiModelProperty(value = "逻辑删除")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "创建时间", type = Excel.Type.EXPORT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @JsonIgnore
    @JsonProperty
    public String getAdminPassword() {
        return adminPassword;
    }
}
