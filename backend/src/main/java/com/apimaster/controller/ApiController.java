package com.apimaster.controller;

import com.apimaster.entity.Api;
import com.apimaster.repository.ApiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis")
public class ApiController {

    private final ApiRepository apiRepository;

    public ApiController(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @PostMapping
    public ResponseEntity<Api> createApi(@RequestBody Api api) {
        api.setStatus(1);
        Api savedApi = apiRepository.save(api);
        return new ResponseEntity<>(savedApi, HttpStatus.CREATED);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Api>> getApisByProject(@PathVariable Long projectId) {
        List<Api> apis = apiRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        return new ResponseEntity<>(apis, HttpStatus.OK);
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<Api>> getApisByFolder(@PathVariable Long folderId) {
        List<Api> apis = apiRepository.findByFolderIdOrderByCreatedAtDesc(folderId);
        return new ResponseEntity<>(apis, HttpStatus.OK);
    }

    @GetMapping("/project/{projectId}/search")
    public ResponseEntity<List<Api>> searchApis(@PathVariable Long projectId, @RequestParam String keyword) {
        List<Api> apis = apiRepository.findByProjectIdAndNameContainingOrPathContainingOrderByCreatedAtDesc(
                projectId, keyword, keyword);
        return new ResponseEntity<>(apis, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Api> getApiById(@PathVariable Long id) {
        return apiRepository.findById(id)
                .map(api -> new ResponseEntity<>(api, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Api> updateApi(@PathVariable Long id, @RequestBody Api api) {
        return apiRepository.findById(id)
                .map(existingApi -> {
                    existingApi.setName(api.getName());
                    existingApi.setPath(api.getPath());
                    existingApi.setMethod(api.getMethod());
                    existingApi.setDescription(api.getDescription());
                    existingApi.setFolderId(api.getFolderId());
                    existingApi.setStatus(api.getStatus());
                    Api updated = apiRepository.save(existingApi);
                    return new ResponseEntity<>(updated, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApi(@PathVariable Long id) {
        apiRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
