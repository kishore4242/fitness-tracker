package com.embarkzzfitness.aiservices.service;

import com.embarkzzfitness.aiservices.model.Activity;
import com.embarkzzfitness.aiservices.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    private final ActivityAiService activityAiService;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity){
        try {
            log.info("received active for processing: {}", activity.getId());
//            log.info("The gemini response is: {}", activityAiService.generateRecommendation(activity));
            Recommendation recommendation = activityAiService.generateRecommendation(activity);
        }
        catch (Exception e){
            log.error("Failed due to:{} \n {}", e.getMessage(),e.getLocalizedMessage());
        }
    }
}
