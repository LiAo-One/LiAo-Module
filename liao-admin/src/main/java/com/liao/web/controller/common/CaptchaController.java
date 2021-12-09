package com.liao.web.controller.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.google.code.kaptcha.Producer;
import com.liao.common.annotation.SignatureValidation;
import com.liao.common.constant.Constants;
import com.liao.common.core.R;
import com.liao.common.core.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 验证码操作处理
 * </p>
 *
 * @author LiAo
 * @since 2021/12/1
 */
@RestController
public class CaptchaController {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;

    // 验证码类型
    @Value("${wick.captchaType}")
    private String captchaType;

    /**
     * 生成验码
     *
     * @param response 请求体
     * @return 验证码
     * @throws IOException ioException
     */
    @SignatureValidation
    @GetMapping("/captchaImage")
    public R getCode(HttpServletResponse response) throws IOException {
        R success = R.success();
        success.put("captchaOnOff", true);

        // 保存验证码信息 没有横线的UUID
        String uuid = IdUtil.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        // 判断验证码类型
        if (captchaType.equals("math")) {
            // 计算类型
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            // 创建一个扭曲的写入文本图像
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();

        try {
            assert image != null;
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return R.error(e.getMessage());
        }

        success.put("uuid", uuid);
        // 将十六进制八位字节编码为 Base64
        success.put("img", Base64.encode(os.toByteArray()));
        return success;
    }
}
