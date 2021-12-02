package com.liao.web.controller.system;

import com.liao.common.core.R;
import com.liao.common.core.entity.LoginBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    public R login(@RequestBody LoginBody loginBody) {
        R success = R.success();
        return null;
    }
}
