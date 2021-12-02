package com.liao.common.exception.user;
/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public class CaptchaExpireException extends UserException {

    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("验证码失效", null);
    }
}
