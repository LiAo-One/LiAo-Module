package com.liao.web.controller.system;

import com.liao.common.constant.Constants;
import com.liao.common.core.R;
import com.liao.common.core.entity.LoginBody;
import com.liao.common.core.entity.SysAdmin;
import com.liao.common.core.entity.SysMenu;
import com.liao.common.utils.SecurityUtils;
import com.liao.framework.web.service.SysLoginService;
import com.liao.framework.web.service.SysPermissionService;
import com.liao.system.services.SysMenuService;
import com.liao.system.services.TokenCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 登录拦截
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private SysPermissionService permissionService;

    // 按钮
    @Autowired
    private TokenCheckService tokenCheckService;

    @Autowired
    private SysMenuService sysMenuService;

    @PostMapping("/auth/login")
    public R login(@RequestBody LoginBody loginBody) {
        R success = R.success();
        String toke = sysLoginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        success.put(Constants.TOKEN, toke);
        return success;
    }

    /**
     * 获取登录用户信息
     *
     * @return 用户
     */
    @GetMapping("/auth/getInfo")
    public R getInfo() {

        // 获取用户信息
        SysAdmin user = SecurityUtils.getLoginUser().getUser();

        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);

        R success = R.success();

        success.put("user", user);
        success.put("roles", roles);
        success.put("permissions", Collections.singletonList("*:*:*"));
        return success;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/auth/getRouters")
    public R getRouters() {
        List<SysMenu> sysMenus = tokenCheckService.selectMenuTreeByUserId();
        return R.success(sysMenuService.buildMenus(sysMenus));
    }
}
