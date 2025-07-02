package com.nathannolacio.meusaldo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Issue(
        int number,
        String title,
        String state,

        @JsonProperty("pull_request")
        Map<String, Object> pullRequest
) {
}
