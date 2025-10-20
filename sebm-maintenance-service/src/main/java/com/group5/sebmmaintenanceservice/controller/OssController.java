package com.group5.sebmmaintenanceservice.controller;

import com.group5.sebmcommon.BaseResponse;
import com.group5.sebmcommon.ResultUtils;
import com.group5.sebmcommon.utils.OssService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oss")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @Value("${aliyun.oss.bucketName}")
    private String bucket;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @GetMapping("/uploadUrl")
    public BaseResponse<Map<String, String>> getUploadUrl(@RequestParam String filename,
                                            @RequestParam String contentType) {
        String objectName = "uploads/" + System.currentTimeMillis() + "-" + filename;
        String signedUrl = ossService.generatePresignedUrl(objectName, contentType);

        String publicUrl = String.format("https://%s.%s/%s", bucket, endpoint, objectName);

        Map<String, String> result = new HashMap<>();
        result.put("uploadUrl", signedUrl);
        result.put("fileUrl", publicUrl);
        log.info("Upload URL: " + signedUrl);
        log.info("File URL: " + publicUrl);
        return ResultUtils.success(result);
    }
}
