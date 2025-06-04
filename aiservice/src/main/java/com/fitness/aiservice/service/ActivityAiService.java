package com.fitness.aiservice.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repo.RecommendationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiInteractionService geminiInteractionService;
    private final RecommendationRepo recommendationRepo;
    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiInteractionService.getAnswer(prompt);
        log.info("Response from the Ai is: {}",aiResponse);
        Recommendation recommendation = processAiResponse(aiResponse,activity);
        recommendationRepo.save(recommendation);
        return recommendation;
    }

    private Recommendation processAiResponse(String aiResponse, Activity activity) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(aiResponse);

            JsonNode textNode = jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n","")
                    .trim();

//            log.info("Passed response from AI {}",jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);

            //to get the analysis and append it in a string builder
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall:");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace:");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","HeartRate:");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","CaloriesBurned:");


            List<String> improvement = extractImprovements(analysisJson.path("improvements"));

            List<String> suggestion = extractSuggestion(analysisJson.path("suggestions"));

            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvement)
                    .suggestions(suggestion)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return createRecommendation(activity);
        }
    }

    private Recommendation createRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate the analysis")
                .improvements(Collections.singletonList("Collection with your current work"))
                .suggestions(Collections.singletonList("Consider consulting a expert"))
                .safety(Arrays.asList(
                        "Stay hydrate",
                        "Listen to your body")
                )
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(safe -> safety.add(safe.asText()));
        };

        return safety.isEmpty() ?
                Collections.singletonList("Follow this guidelines for your safety"):
                safety;
    }

    private List<String> extractSuggestion(JsonNode suggestionNode) {
        List<String> suggetions = new ArrayList<>();
        if(suggestionNode.isArray()){
            suggestionNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggetions.add(String.format("%s : %s",workout,description));
            });
        }
        return suggetions.isEmpty() ?
                Collections.singletonList("No specific suggestions"):
                suggetions;
    }

    private List<String> extractImprovements(JsonNode improvementNode) {
        List<String> improvement = new ArrayList<>();
        if(improvementNode.isArray()){
            improvementNode.forEach(improve -> {
                String area = improve.path("area").asText();
                String detail = improve.path("recommendation").asText();
                improvement.add(String.format("%s : %s",area,detail));
            });
        }
        return improvement.isEmpty() ?
                Collections.singletonList("No specific improvements"):
                improvement;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                         Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
                                {
                                  "analysis": {
                                    "overall": "Overall analysis here",
                                    "pace": "Pace analysis here",
                                    "heartRate": "Heart rate analysis here",
                                    "caloriesBurned": "Calories analysis here"
                                  },
                                  "improvements": [
                                    {
                                      "area": "Area name",
                                      "recommendation": "Detailed recommendation"
                                    }
                                  ],
                                  "suggestions": [
                                    {
                                      "workout": "Workout name",
                                      "description": "Detailed workout description"
                                    }
                                  ],
                                  "safety": [
                                    "Safety point 1",
                                    "Safety point 2"
                                  ]
                                }
                        
                                Analyze this activity:
                                Activity Type: %s
                                Duration: %d minutes
                                Calories Burned: %d
                                Additional Metrics: %s
                        
                                Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                                Ensure the response follows the EXACT JSON format shown above.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionMatrix()
        );
    }
}

