package com.liao.common.core.entity;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
@Data
public class LoginBody {

    // 用户名
    private String username;

    // 用户密码
    private String password;

    // 验证码
    private String code;

    // 唯一标识
    private String uuid = "";
}
