package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.RoadmapResponse;
import com.nathannolacio.meusaldo.service.GithubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("api/github")
public class GitHubController {

    private final GithubService gitHubService;

    public GitHubController(GithubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/roadmap")
    public ResponseEntity<?> getRoadmap() {
        try {
            RoadmapResponse roadmap = gitHubService.getRoadmap();
            return ResponseEntity.ok(roadmap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar roadmap: " + e.getMessage());
        }
    }
}
