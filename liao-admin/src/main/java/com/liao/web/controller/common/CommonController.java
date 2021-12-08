package com.liao.web.controller.common;

import com.liao.common.constant.Constants;
import com.liao.common.utils.StringUtils;
import com.liao.common.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 通用请求处理
 * </p>
 *
 * @author LiAo
 * @since 2021/9/23
 */
@Slf4j
@RestController
public class CommonController {

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/common/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            // 判断文件名称是否合法
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }

            // 格式化文件名称
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);

            // 文件完整路径
            String filePath = Constants.DOWNLOAD_PATH + fileName;

            // 设置响应头类型
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            // 下载文件名重新编码
            FileUtils.setAttachmentResponseHeader(response, realFileName);

            // 输出 指定文件的 Byte 数组
            FileUtils.writeBytes(filePath, response.getOutputStream());

            // 文件删除
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}
