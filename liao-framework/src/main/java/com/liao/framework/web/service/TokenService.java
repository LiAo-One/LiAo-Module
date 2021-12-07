package com.liao.framework.web.service;

import cn.hutool.core.util.IdUtil;
import com.liao.common.constant.Constants;
import com.liao.common.constant.SecurityConstants;
import com.liao.common.core.entity.LoginUser;
import com.liao.common.core.redis.RedisCache;
import com.liao.common.utils.AddressUtils;
import com.liao.common.utils.ServletUtils;
import com.liao.common.utils.StringUtils;
import com.liao.common.utils.ip.IpUtils;
import com.liao.framework.web.entity.server.Sys;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Token 校验处理
 * </p>
 *
 * @author LiAo
 * @since 2021/12/3
 */
@Component
public class TokenService {

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取用户身份信息
     *
     * @param request 请求信息
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                return redisCache.getCacheObject(userKey);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 删除用户登录凭证
     *
     * @param token 凭证
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotNull(token)) {
            String tokenKey = getTokenKey(token);
            redisCache.deleteObject(tokenKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 登录用户对象
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String uuid = IdUtil.fastUUID();
        loginUser.setToken(uuid);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, uuid);
        return createToken(claims);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 验证令牌有效期 ，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 用户
     */
    public void verifyToken(LoginUser loginUser) {
        Long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid 将 loginUser 缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * 获取令牌中的数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取请求Token
     *
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }
}
