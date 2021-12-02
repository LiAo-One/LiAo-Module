package com.liao.framework.web.service;

import com.liao.common.constant.Constants;
import com.liao.common.core.entity.SysAdmin;
import com.liao.common.exception.ServiceException;
import com.liao.common.exception.user.PermissionException;
import com.liao.common.utils.StringUtils;
import com.liao.framework.manager.AsyncManager;
import com.liao.framework.manager.factory.AsyncFactory;
import com.liao.system.dao.SysRoleMapper;
import com.liao.system.entity.SysRole;
import com.liao.system.services.SysAdminService;
import com.liao.system.services.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户验证处理
 * </p>
 *
 * @author LiAo
 * @since 2021/12/2
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private SysAdminService sysAdminService;

    // 角色
    @Autowired
    private SysRoleMapper sysRoleMapper;

    // 按钮
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysAdmin sysAdmins = sysAdminService.selectUserByUserName(username).get(0);

        if (StringUtils.isNull(sysAdmins)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        return createLoginUser(sysAdmins);
    }

    public UserDetails createLoginUser(SysAdmin user) {

        // userRole
        SysRole sysRole = sysRoleMapper.selLoginUserRole(user.getAdminId());

        if (StringUtils.isNull(sysRole)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getAdminName(), Constants.LOGIN_FAIL, "账户角色权限信息异常"));
            throw new PermissionException();
        }

        return null;
    }
}
