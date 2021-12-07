package com.liao.framework.web.service;

import com.liao.common.core.entity.LoginUser;
import com.liao.common.core.entity.SysAdmin;
import com.liao.common.exception.ServiceException;
import com.liao.common.utils.StringUtils;
import com.liao.system.services.SysAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysAdmin sysAdmins = sysAdminService.selectUserByUserName(username).get(0);

        if (StringUtils.isNull(sysAdmins)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        return createLoginUser(sysAdmins);
    }

    /**
     * 构建登录用户身份权限
     *
     * @param user 用户
     * @return 登录权限对象
     */
    public UserDetails createLoginUser(SysAdmin user) {
        return new LoginUser(user.getAdminId(), user, Collections.singletonList("*:*:*"));
    }
}
