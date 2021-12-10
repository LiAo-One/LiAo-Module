package com.liao.web.controller.monitor;

import com.github.pagehelper.PageInfo;
import com.liao.common.annotation.Log;
import com.liao.common.constant.Constants;
import com.liao.common.constant.HttpStatus;
import com.liao.common.core.R;
import com.liao.common.core.entity.LoginUser;
import com.liao.common.core.page.TableDataInfo;
import com.liao.common.core.redis.RedisCache;
import com.liao.common.enums.BusinessType;
import com.liao.common.utils.StringUtils;
import com.liao.system.entity.SysUserOnline;
import com.liao.system.services.SysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 在线用户监控
 * </p>
 *
 * @author LiAo
 * @since 2021/12/10
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysUserOnlineService userOnlineService;

    @GetMapping("list")
    public TableDataInfo list(String ipaddr, String userName) {

        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");

        List<SysUserOnline> userOnlineList = new ArrayList<>();

        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);

            // 过滤ip 和 名称
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            }
            // 过滤 ip
            else if (StringUtils.isNotEmpty(ipaddr)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr())) {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            }
            // 过滤名称
            else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) {
                if (StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            }
            // 没有过滤
            else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }

        // 反转排序序列中的元素
        Collections.reverse(userOnlineList);

        // Collections.singleton(null) 返回值为空的
        // 删除 值为空的
        userOnlineList.removeAll(Collections.singleton(null));

        return getDataTable(userOnlineList);
    }

    /**
     * 强退用户
     */
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public R forceLogout(@PathVariable String tokenId) {
        redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
        return R.success();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }
}
