package com.fitness.activity.dto;

import com.fitness.activity.model.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponse {
    private String id;
    private String userId;
    private ActivityType type;
    private Integer caloriesBurned;
    private Integer duration;
    private LocalDateTime startTime;
    private Map<String, Object> additionMatrix;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
