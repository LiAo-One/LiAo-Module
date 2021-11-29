package com.liao.web.controller.monitor;

import com.liao.common.core.R;
import com.liao.framework.web.entity.Server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务监控
 * </p>
 *
 * @author LiAo
 * @since 2021/11/29
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    @GetMapping()
    public R getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.success(server);
    }
}
