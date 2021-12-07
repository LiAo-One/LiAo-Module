package com.liao.framework.web.service;

import com.liao.common.core.entity.SysAdmin;
import com.liao.system.dao.SysRoleMapper;
import com.liao.common.core.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 用户数据权限处理
 * </p>
 *
 * @author LiAo
 * @since 2021/12/2
 */
@Component
public class SysPermissionService {

    // 角色
    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysAdmin user) {
        // userRole
        SysRole sysRole = getLoginRolePermission(user);
        return new HashSet<String>(Collections.singletonList(sysRole.getRoleName()));
    }

    /**
     * 获取角色信息
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public SysRole getLoginRolePermission(SysAdmin user) {
        // userRole
        return sysRoleMapper.selLoginUserRole(user.getAdminId());
    }

}
