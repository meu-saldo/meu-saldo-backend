package com.nathannolacio.meusaldo.dto;

import java.util.List;
import java.util.Map;

public record RoadmapResponse(
        List<Milestone> milestones,
        Map<Integer, List<Issue>> issues
        ) {
}
