package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.RoadmapResponse;
import com.nathannolacio.meusaldo.dto.Issue;
import com.nathannolacio.meusaldo.dto.Milestone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class GithubService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.owner}")
    private String owner;

    @Value("${github.repo}")
    private String repo;

    private final RestTemplate restTemplate = new RestTemplate();

    public RoadmapResponse getRoadmap() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String milestoneUrl = String.format("https://api.github.com/repos/%s/%s/milestones", owner, repo);
        ResponseEntity<Milestone[]> milestoneResponse = restTemplate.exchange(milestoneUrl, HttpMethod.GET, entity, Milestone[].class);

        Milestone[] milestones = milestoneResponse.getBody();
        Map<Integer, List<Issue>> issuesMap = new HashMap<>();

        if (milestones != null) {
            for (Milestone milestone : milestones) {
                String issuesUrl = String.format("https://api.github.com/repos/%s/%s/issues", owner, repo);

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl(issuesUrl)
                        .queryParam("milestone", milestone.number())
                        .queryParam("state", "all");

                ResponseEntity<Issue[]> issuesResponse = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        Issue[].class
                );

                Issue[] issues = issuesResponse.getBody();
                List<Issue> filtered = Arrays.stream(issues)
                        .filter(issue -> issue.pullRequest() == null)
                        .sorted(Comparator.comparingInt(Issue::number))
                        .toList();

                issuesMap.put(milestone.number(), filtered);
            }
        }

        return new RoadmapResponse(Arrays.asList(milestones), issuesMap);
    }

}
