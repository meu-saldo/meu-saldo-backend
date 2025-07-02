package com.nathannolacio.meusaldo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Milestone(
        int number,
        String title,
        String description,

        @JsonProperty("open_issues")
        int openIssues,

        @JsonProperty("closed_issues")
        int closedIssues
) {}
