package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.repository.TimeControlRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimeControlService {
    private final TimeControlRepository timeControlRepository;

    public TimeControl createArrivalTimeControl(Employee e, WorkingShift ws, boolean onShift, LocalDateTime time) {
        TimeControl tc = new TimeControl();
        tc.setArrival(time);
        tc.setDeparture(null);
        tc.setEmployee(e);
        tc.setOnShift(onShift);
        tc.setWorkingShift(ws);

        return timeControlRepository.save(tc);
    }

    public TimeControl updateTimeControlForDeparture(Long empId, LocalDateTime time) {
        TimeControl tc = timeControlRepository.findByEmployee_IdAndIsOnShiftTrueAndWorkingShift_IsEndedFalse(empId);
        tc.setDeparture(time);
        tc.setOnShift(false);

        return timeControlRepository.save(tc);
    }

}
