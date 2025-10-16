package com.group5.sebmcommon.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public String generatePresignedUrl(String objectName, String contentType) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 5分钟后过期
        Date expiration = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, objectName);
        request.setExpiration(expiration);
        request.setMethod(com.aliyun.oss.HttpMethod.PUT);
        request.setContentType(contentType);

        URL signedUrl = ossClient.generatePresignedUrl(request);
        ossClient.shutdown();
        return signedUrl.toString();
    }
}
