package com.liao.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.liao.common.constant.Constants;
import com.liao.common.constant.HttpStatus;
import com.liao.common.core.R;
import com.liao.common.core.entity.LoginUser;
import com.liao.common.utils.ServletUtils;
import com.liao.common.utils.StringUtils;
import com.liao.framework.manager.AsyncManager;
import com.liao.framework.manager.factory.AsyncFactory;
import com.liao.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 自定义退出类
 * </p>
 *
 * @author LiAo
 * @since 2021/12/7
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String username = loginUser.getUsername();
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGOUT, "退出成功"));
        }
        ServletUtils.renderString(response, JSON.toJSONString(R.error(HttpStatus.SUCCESS, "退出成功")));
    }
}
