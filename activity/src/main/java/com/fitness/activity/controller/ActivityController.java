package com.fitness.activity.controller;

import com.fitness.activity.dto.ActivityRequest;
import com.fitness.activity.dto.ActivityResponse;
import com.fitness.activity.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
//        logger.info("Tracking new activity: {}", request);
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivity(@RequestHeader("X-User-ID") String userId) {
//        logger.info("Fetching activities for userId={}", userId);
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable("activityId") String activityId) {
//        logger.info("Fetching activity with id={}", activityId);
        return ResponseEntity.ok(activityService.getActivity(activityId));
    }
}
