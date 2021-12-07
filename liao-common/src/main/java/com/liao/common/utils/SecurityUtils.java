package com.liao.common.utils;

import com.liao.common.constant.HttpStatus;
import com.liao.common.constant.SecurityConstants;
import com.liao.common.core.entity.LoginUser;
import com.liao.common.exception.ServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <p>
 * 权限安全工具类
 * </p>
 *
 * @author LiAo
 * @since 2021/10/25
 */
public class SecurityUtils {

    /**
     * 替换Token 前缀
     *
     * @param token token
     * @return 结果
     */
    public static String replaceTokenPrefix(String token) {
        if (StringUtils.isNotEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 获取登录用户
     *
     * @return 用户信息
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取 Authentication
     *
     * @return Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
