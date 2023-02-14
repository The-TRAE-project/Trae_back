package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.WorkShiftingDto;
import ru.trae.backend.dto.mapper.WorkShiftingDtoMapper;
import ru.trae.backend.entity.WorkShifting;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.WorkShiftingException;
import ru.trae.backend.repository.WorkShiftingRepository;
import ru.trae.backend.util.DayOrNight;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class WorkShiftingService {
    private final WorkShiftingRepository workShiftingRepository;
    private final WorkShiftingDtoMapper workShiftingDtoMapper;

    public void createWorkShifting() {
        WorkShifting ws = new WorkShifting();
        ws.setStartShift(LocalDateTime.now());
        ws.setEmployees(new ArrayList<>());
        ws.setEnded(false);
        ws.setTimeOfDay(LocalDateTime.now().getHour() >= 18 ? DayOrNight.NIGHT : DayOrNight.DAY);

        workShiftingRepository.save(ws);
    }

    public WorkShiftingDto getActive() {
        if (!existsActiveWorkShifting())
            throw new WorkShiftingException(HttpStatus.BAD_REQUEST, "Нет активных рабочих смен.");

        return workShiftingDtoMapper.apply(workShiftingRepository.findByIsEndedFalse());
    }

    public void closeWorkShifting() {
        if (!existsActiveWorkShifting()) return;

        WorkShifting ws = workShiftingRepository.findByIsEndedFalse();
        ws.setEnded(true);
        ws.setEndShift(LocalDateTime.now());

        workShiftingRepository.save(ws);
    }

    public void addOrRemoveEmployeeInShifting(Employee employee) {
        if (!existsActiveWorkShifting())
            throw new WorkShiftingException(HttpStatus.BAD_REQUEST, "Нет активных рабочих смен.");

        WorkShifting ws = workShiftingRepository.findByIsEndedFalse();
        if (ws.getEmployees().contains(employee)) {
            ws.getEmployees().remove(employee);
        } else {
            ws.getEmployees().add(employee);
        }

        workShiftingRepository.save(ws);
    }

    public boolean existsActiveWorkShifting() {
        return workShiftingRepository.existsByIsEndedFalse();
    }
}
