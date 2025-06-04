package com.fitness.aiservice.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {
    private String id;
    private String userId;
    private String Type;
    private Integer caloriesBurned;
    private Integer duration;
    private LocalDateTime startTime;
    private Map<String, Object> additionMatrix;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
