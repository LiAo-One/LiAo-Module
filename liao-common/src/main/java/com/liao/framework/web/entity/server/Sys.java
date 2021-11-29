package com.liao.framework.web.entity.server;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 系统相关信息
 * </p>
 *
 * @author LiAo
 * @since 2021/11/29
 */
@Data
public class Sys {

    // 服务器名称
    private String computerName;

    // 服务器Ip
    private String computerIp;

    // 项目路径
    private String userDir;

    // 操作系统
    private String osName;

    // 系统架构
    private String osArch;

    // 磁盘相关信息
    private List<SysFile> sysFiles = new LinkedList<SysFile>();
}
