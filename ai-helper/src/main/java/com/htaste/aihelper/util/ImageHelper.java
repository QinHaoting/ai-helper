package com.htaste.aihelper.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Base64;

/**
 * @author Haoting Qin
 * @date 2025/9/10
 */
public class ImageHelper {
    public static String getImageAsBase64(String imageUrl) throws IOException {

        //1 图片转码：通过Base64编码将图片转化为字符串
        // 读取文件字节数组
        ClassPathResource resource = new ClassPathResource(imageUrl);
        byte[] byteArray = resource.getContentAsByteArray();
        // 转换为Base64
        String base64Image = Base64.getEncoder().encodeToString(byteArray);

        // 返回 Base64 编码后的图片字符串
        return base64Image;
    }
}
