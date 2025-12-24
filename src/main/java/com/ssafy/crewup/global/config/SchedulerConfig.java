package com.ssafy.crewup.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄러 활성화 설정
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // @Scheduled 어노테이션이 동작하도록 활성화
}
