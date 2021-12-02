package com.liao.common.exception.user;

import com.liao.common.exception.base.BaseException;

/**
 * <p>
 * 用户信息异常类
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public class UserException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
