package com.liao.common.utils.file;

import com.liao.common.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 文件处理工具类
 * </p>
 *
 * @author LiAo
 * @since 2021/9/23
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * 检查文件是否可下载
     *
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) {
        // 禁止目录上跳级别  判断是否包含 ..
        if (StringUtils.contains(resource, "..")) {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource))) {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        String contentDispositionValue = "attachment; filename=" +
                percentEncodedFileName +
                ";" +
                "filename*=" +
                "utf-8''" +
                percentEncodedFileName;
        response.setHeader("Content-disposition", contentDispositionValue);
    }

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            // 创建文件对象
            File file = new File(filePath);
            // 验证文件是否存在
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            // 输入流
            fis = new FileInputStream(file);
            // 定义数组
            byte[] b = new byte[1024];
            // 长度
            int length;
            // 读取文件
            while ((length = fis.read(b)) > 0) {
                // 写入
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            // 关闭流
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return 是否成功
     */
    public static boolean deleteFile(String filePath) {
        // 状态
        boolean flag = false;

        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            // 删除
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }
}
