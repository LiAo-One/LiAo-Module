package com.liao.system.services;

/**
 * <p>
 * 参数配置服务
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public interface ISysConfigService {

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    boolean selectCaptchaOnOff();
}
