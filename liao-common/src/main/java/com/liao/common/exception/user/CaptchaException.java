package com.liao.common.exception.user;

/**
 * <p>
 *  验证码错误异常类
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("user.jcaptcha.error", null);
    }
}
