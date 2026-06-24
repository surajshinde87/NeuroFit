package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;

    @KafkaListener(
            topics = "${kafka.topic.name}",
            groupId = "activity-processor-group"
    )
    public void processActivity(Activity activity) {

        log.info("Received Activity for processing: {}", activity.getUserId());

        try {

            Recommendation recommendation =
                    activityAIService.generateRecommendation(activity);

            recommendationRepository.save(recommendation);

            log.info("Recommendation saved successfully for user: {}",
                    activity.getUserId());

        } catch (WebClientResponseException.TooManyRequests ex) {

            log.error("Gemini API rate limit exceeded (429) for user: {}",
                    activity.getUserId(), ex);

            // Skip this message so Kafka can continue processing

        } catch (WebClientResponseException ex) {

            log.error("Gemini API error: {} for user: {}",
                    ex.getStatusCode(),
                    activity.getUserId(),
                    ex);

        } catch (Exception ex) {

            log.error("Unexpected error while processing activity for user: {}",
                    activity.getUserId(),
                    ex);
        }
    }
}