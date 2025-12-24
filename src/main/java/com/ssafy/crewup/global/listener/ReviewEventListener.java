package com.ssafy.crewup.global.listener;
import com.ssafy.crewup.global.event.ReviewCreatedEvent;
import com.ssafy.crewup.global.service.CourseAnalysisService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventListener {

    private final CourseAnalysisService courseAnalysisService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewCreated(ReviewCreatedEvent event) {
        courseAnalysisService.analyzeAndUpdate(event.courseId());
    }
}
