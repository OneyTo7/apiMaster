package com.apimaster.controller;

import com.apimaster.entity.ApiFolder;
import com.apimaster.repository.ApiFolderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-folders")
public class ApiFolderController {

    private final ApiFolderRepository apiFolderRepository;

    public ApiFolderController(ApiFolderRepository apiFolderRepository) {
        this.apiFolderRepository = apiFolderRepository;
    }

    @PostMapping
    public ResponseEntity<ApiFolder> createFolder(@RequestBody ApiFolder folder) {
        ApiFolder savedFolder = apiFolderRepository.save(folder);
        return new ResponseEntity<>(savedFolder, HttpStatus.CREATED);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ApiFolder>> getFoldersByProject(@PathVariable Long projectId) {
        List<ApiFolder> folders = apiFolderRepository.findByProjectIdAndParentIdIsNullOrderBySortAsc(projectId);
        return new ResponseEntity<>(folders, HttpStatus.OK);
    }

    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<List<ApiFolder>> getAllFoldersByProject(@PathVariable Long projectId) {
        List<ApiFolder> folders = apiFolderRepository.findByProjectIdOrderBySortAsc(projectId);
        return new ResponseEntity<>(folders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiFolder> getFolderById(@PathVariable Long id) {
        return apiFolderRepository.findById(id)
                .map(folder -> new ResponseEntity<>(folder, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiFolder> updateFolder(@PathVariable Long id, @RequestBody ApiFolder folder) {
        return apiFolderRepository.findById(id)
                .map(existingFolder -> {
                    existingFolder.setName(folder.getName());
                    existingFolder.setDescription(folder.getDescription());
                    existingFolder.setSort(folder.getSort());
                    existingFolder.setParentId(folder.getParentId());
                    ApiFolder updated = apiFolderRepository.save(existingFolder);
                    return new ResponseEntity<>(updated, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        apiFolderRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
