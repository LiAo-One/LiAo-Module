package com.liao.common.exception.user;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("验证码错误", null);
    }
}
