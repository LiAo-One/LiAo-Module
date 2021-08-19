package com.liao;

import com.liao.generator.services.GenTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LiaoAdminApplicationTests {

    @Autowired
    private GenTableService genTableService;

    @Test
    void contextLoads() {
        genTableService.downloadCode(new String[] {"sys_codes"});
    }

}
