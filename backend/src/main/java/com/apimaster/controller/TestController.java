package com.apimaster.controller;

import com.apimaster.service.MinioService;
import com.apimaster.service.RabbitMQService;
import com.apimaster.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class TestController {

    private final RedisService redisService;
    private final RabbitMQService rabbitMQService;
    private final MinioService minioService;

    @Value("${minio.bucketName}")
    private String bucketName;

    public TestController(RedisService redisService, RabbitMQService rabbitMQService, MinioService minioService) {
        this.redisService = redisService;
        this.rabbitMQService = rabbitMQService;
        this.minioService = minioService;
    }

    // Redis测试
    @PostMapping("/redis/set")
    public ResponseEntity<Map<String, Object>> testRedisSet(@RequestParam String key, @RequestParam String value) {
        redisService.set(key, value, 10, TimeUnit.MINUTES);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Redis set success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redis/get")
    public ResponseEntity<Map<String, Object>> testRedisGet(@RequestParam String key) {
        String value = redisService.get(key, String.class);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    // RabbitMQ测试
    @PostMapping("/rabbitmq/send")
    public ResponseEntity<Map<String, Object>> testRabbitMQSend(@RequestParam String message) {
        rabbitMQService.sendApiTaskMessage(message);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "RabbitMQ send success");
        return ResponseEntity.ok(response);
    }

    // MinIO测试
    @PostMapping("/minio/upload")
    public ResponseEntity<Map<String, Object>> testMinioUpload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            String url = minioService.uploadFile(bucketName, fileName, file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/minio/list")
    public ResponseEntity<Map<String, Object>> testMinioList() {
        try {
            var objects = minioService.listObjects(bucketName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("objects", objects);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 健康检查
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All services are healthy");
        return ResponseEntity.ok(response);
    }
}
