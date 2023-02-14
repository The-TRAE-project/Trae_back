package ru.trae.backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.trae.backend.service.WorkShiftingService;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class WorkShiftingScheduler {
    private final WorkShiftingService workShiftingService;

    @Scheduled(cron = "${scheduler.start-day}")
    private void workShiftingDayHandler() {
        workShiftingService.createWorkShifting();
    }

    @Scheduled(cron = "${scheduler.start-night}")
    private void workShiftingNightHandler() {
        workShiftingService.createWorkShifting();
    }

    @Scheduled(cron = "${scheduler.end-day}")
    private void workShiftingDayEndHandler() {
        workShiftingService.closeWorkShifting();
    }

    @Scheduled(cron = "${scheduler.end-night}")
    private void workShiftingNightEndHandler() {
        workShiftingService.closeWorkShifting();
    }

}
