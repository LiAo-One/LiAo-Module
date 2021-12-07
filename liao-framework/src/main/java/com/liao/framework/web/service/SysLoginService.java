package com.liao.framework.web.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liao.common.constant.Constants;
import com.liao.common.core.R;
import com.liao.common.core.entity.LoginUser;
import com.liao.common.core.entity.SysAdmin;
import com.liao.common.core.entity.SysMenu;
import com.liao.common.core.page.PageUtils;
import com.liao.common.core.redis.RedisCache;
import com.liao.common.exception.ServiceException;
import com.liao.common.exception.user.*;
import com.liao.common.utils.MessageUtils;
import com.liao.common.utils.RedisUtil;
import com.liao.common.utils.StringUtils;
import com.liao.common.utils.TokenUtil;
import com.liao.framework.manager.AsyncManager;
import com.liao.framework.manager.factory.AsyncFactory;
import com.liao.system.dao.SysAdminMapper;
import com.liao.system.dao.SysRoleMapper;
import com.liao.common.core.entity.SysRole;
import com.liao.system.entity.vo.RouterVo;
import com.liao.system.services.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 登录校验方法
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
@Component
public class SysLoginService {

    @Autowired
    private RedisCache redisCache;

    @Resource
    private AuthenticationManager authenticationManager;

    // 管理员
    @Autowired
    private SysAdminMapper sysAdminMapper;

    // Redis 操作
    @Autowired
    private RedisUtil redisUtil;

    // Token
    @Autowired
    private TokenService tokenService;

    // 角色
    @Autowired
    private SysRoleMapper sysRoleMapper;

    // 按钮
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        // 校验验证码
        validateCaptcha(username, code, uuid);

        // 用户验证
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                        MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS,
                MessageUtils.message("user.login.success")));

        // 获取登录用户
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String username, String code, String uuid) {
        // key
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        // 获取验证
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);

        if (StringUtils.isNull(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }

        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
    }

    /**
     * 旧的登录方法
     *
     * @param adminAccount  账号
     * @param adminPassword 密码
     * @return
     */
    public R login(String adminAccount, String adminPassword) {

        QueryWrapper<SysAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq("admin_account", adminAccount)
                .eq("admin_password", adminPassword);
        List<SysAdmin> sysAdminIPage = sysAdminMapper.selectPage(PageUtils.startDefPage(), wrapper).getRecords();

        // 登录校验
        if (StringUtils.isEmpty(sysAdminIPage)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(adminAccount, Constants.LOGIN_FAIL, "账号密码错误"));
            throw new LoginException();
        }

        Long userId = sysAdminIPage.get(0).getAdminId();

        // userRole
        SysRole sysRole = sysRoleMapper.selLoginUserRole(userId);

        if (StringUtils.isNull(sysRole)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(adminAccount, Constants.LOGIN_FAIL, "账户角色权限信息异常"));
            throw new PermissionException();
        }
        // userMenu
        List<SysMenu> menus = sysMenuService.selectLoginMenuList(sysRole.getRoleId());

        if (StringUtils.isNull(menus)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(adminAccount, Constants.LOGIN_FAIL, "账户角色权限信息异常"));
            throw new PermissionException();
        }

        // 转换为路由
        List<RouterVo> routerVos = sysMenuService.buildMenus(menus);

        // userMenu
        String userMenu = JSON.toJSONString(routerVos);

        // 生成唯一Redis-key
        String token = IdUtil.randomUUID();

        // 存储用户信息
        redisUtil.set(TokenUtil.getUserTokenKey(token), sysAdminIPage.get(0), Constants.EXPIRE_DATE);
        // 按钮 路由
        redisUtil.set(TokenUtil.getMenuTokenKey(token), userMenu, Constants.EXPIRE_DATE);
        // 角色
        redisUtil.set(TokenUtil.getRoleTokenKey(token), sysRole, Constants.EXPIRE_DATE);

        // token入库
        redisUtil.set(token, token, Constants.EXPIRE_DATE);

        AsyncManager.me().execute(AsyncFactory.recordLogininfor(adminAccount, Constants.LOGIN_SUCCESS, "登录成功"));

        return R.success(token);
    }
}
