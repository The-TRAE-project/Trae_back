package ru.trae.backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.trae.backend.service.WorkingShiftService;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class WorkShiftingScheduler {

	private final WorkingShiftService workingShiftService;

	@Scheduled(cron = "${scheduler.start-day}")
	private void workShiftingDayHandler() {
		workingShiftService.createWorkingShift();
	}

	@Scheduled(cron = "${scheduler.end-day}")
	private void workShiftingDayEndHandler() {
		workingShiftService.closeWorkingShift();
	}

	@PostConstruct
	private void createWorkingShiftAfterInit() {
		if (!workingShiftService.existsActiveWorkingShift())
			workingShiftService.createWorkingShift();
	}

}
