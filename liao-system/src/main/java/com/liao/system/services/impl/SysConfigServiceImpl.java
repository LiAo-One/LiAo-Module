package com.liao.system.services.impl;

import com.liao.common.constant.Constants;
import com.liao.common.core.redis.RedisCache;
import com.liao.common.core.text.Convert;
import com.liao.system.services.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
public class SysConfigServiceImpl implements SysConfigService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));




        return null;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaOnOff() {
        return false;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return Constants.SYS_CONFIG_KEY + configKey;
    }
}
