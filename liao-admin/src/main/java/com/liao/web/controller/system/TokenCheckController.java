package com.liao.web.controller.system;

import com.liao.common.annotation.Log;
import com.liao.common.core.R;
import com.liao.common.enums.BusinessType;
import com.liao.system.services.TokenCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * token校验
 * </p>
 *
 * @author LiAo
 * @since 2020/12/18
 */
@RestController
@RequestMapping("system/token")
@Api(tags = "Token")
public class TokenCheckController {

    @Autowired
    private TokenCheckService tokenCheckService;

    /**
     * Tokne 校验
     *
     * @param token token
     * @return 结果
     */
    @GetMapping("verify")
    @ApiOperation("Token校验")
    @Log(title = "token", businessType = BusinessType.OTHER)
    public R verify(String token) {
        return tokenCheckService.verify(token);
    }

    /**
     * 根据Key获取Token数据
     *
     * @param token Json 数据
     * @return 结果
     */
    @GetMapping("get_routers")
    @ApiOperation("获取Token数据")
    public Object getTokenMes(String token) {
        return R.success(tokenCheckService.getTokenMes(token));
    }

    /**
     * 获取登录用户所有数据
     *
     * @param token Json 数据
     * @return 结果
     */
    @GetMapping("token-mes-all")
    @ApiOperation("获取登录用户数据")
    public R getTokenMesAll(String token) {
        return tokenCheckService.getTokenMesAll(token);
    }
}
