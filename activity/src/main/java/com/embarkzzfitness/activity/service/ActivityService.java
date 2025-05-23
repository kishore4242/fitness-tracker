package com.embarkzzfitness.activity.service;

import com.embarkzzfitness.activity.dto.ActivityRequest;
import com.embarkzzfitness.activity.dto.ActivityResponse;
import com.embarkzzfitness.activity.model.Activity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepo activityRepo;
    private final UserValidationService userValidationService;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request){
        boolean isValid = userValidationService.validateUser(request.getUserId());
        if(!isValid){
            throw new RuntimeException("Invalid User: "+request.getUserId());
        }
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionMatrix(request.getAdditionMatrix())
//                .locale("en_US")
                .build();
        Activity saveActivity = activityRepo.save(activity);
// rabbit mq for AI process
        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,saveActivity);
        }
        catch (Exception e){
            log.error("Error not able to push in the rabbit MQ");
        }
        return mapToResponse(saveActivity);
    }

    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setType(activity.getType());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionMatrix(activity.getAdditionMatrix());
        activityResponse.setCreatedAt(activity.getCreatedAt());
        activityResponse.setUpdatedAt(activity.getUpdatedAt());

        return activityResponse;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activity = activityRepo.findByUserId(userId);
        return activity.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ActivityResponse getActivity(String activityid) {
        return activityRepo.findById(activityid).map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("User not found for user id: "+activityid));
    }
}
