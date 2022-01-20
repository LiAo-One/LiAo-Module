package com.liao.common.constant;

/**
 * <p>
 * 通用常量信息
 * </p>
 *
 * @author LiAo
 * @since 2020/12/14
 */
public class Constants {

    // 页码常量
    public static final String PAGE_NUM = "pageNum";

    // 数据长度
    public static final String PAGE_SIZE = "pageSize";

    // 排序列
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    // 排序方向 "desc" 或者 "asc"
    public static final String IS_ASC = "isAsc";

    // GBK 字符集
    public static final String GBK = "GBK";

    // 通用成功标识
    public static final String SUCCESS = "0";

    // 通用失败标识
    public static final String FAIL = "1";

    // 登录成功
    public static final String LOGIN_SUCCESS = "Success";

    // 登录失败
    public static final String LOGIN_FAIL = "Error";

    // 注销
    public static final String LOGOUT = "Logout";

    // Redis Token 设置过期时间 7天
    public static final long EXPIRE_DATE = 60 * 60 * 24 * 7;

    // token
    public static final String SIGNATURE_VALIDATION = "token_str";

    // timeInfo
    public static final String TIME_INFO = "timeInfo";

    // 接口校验秘钥
    public static final String SECRET = "c353fdcac26c4035bdb123c6d8f2e2b1";

    // 字典管理 cache key
    public static final String SYS_DICT_KEY = "sys_dict:";

    // 上传路径
    public static final String DOWNLOAD_PATH = "D:/li_ao/uploadPath/upload";

    // 参数管理 cache key
    public static final String SYS_CONFIG_KEY = "sys_config:";

    // 验证码 redis key
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    // 验证码有效期（分钟）
    public static final Integer CAPTCHA_EXPIRATION = 2;

    // 登录用户 redis key
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    // 令牌前缀
    public static final String LOGIN_USER_KEY = "login_user_key";

    // 令牌
    public static final String TOKEN = "token";

    // RMI 远程方法调用
    public static final String LOOKUP_RMI = "rmi:";

    // LDAP 远程方法调用
    public static final String LOOKUP_LDAP = "ldap:";

    // LDAPS 远程方法调用
    public static final String LOOKUP_LDAPS = "ldaps:";

    // http请求
    public static final String HTTP = "http://";

    // https请求
    public static final String HTTPS = "https://";

    //定时任务违规的字符
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.liao.common.utils.file" };

    // 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
    public static final String[] JOB_WHITELIST_STR = { "com.liao" };

}
