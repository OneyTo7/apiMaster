package com.apimaster.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface MinioService {

    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     */
    void createBucket(String bucketName);

    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadFile(String bucketName, String objectName, MultipartFile file);

    /**
     * 上传文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param inputStream 输入流
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String uploadFile(String bucketName, String objectName, InputStream inputStream, String contentType);

    /**
     * 下载文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 输入流
     */
    InputStream downloadFile(String bucketName, String objectName);

    /**
     * 删除文件
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    void deleteFile(String bucketName, String objectName);

    /**
     * 列出存储桶中的文件
     * @param bucketName 存储桶名称
     * @return 文件列表
     */
    List<String> listObjects(String bucketName);

    /**
     * 获取文件访问URL
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expiry 过期时间（秒）
     * @return 文件访问URL
     */
    String getPresignedUrl(String bucketName, String objectName, int expiry);
}
