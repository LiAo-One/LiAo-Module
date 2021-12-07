package com.liao.common.exception.user;

/**
 * <p>
 * 验证码失效异常类
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public class CaptchaExpireException extends UserException {

    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire", null);
    }
}
