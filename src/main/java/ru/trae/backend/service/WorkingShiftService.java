package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.dto.mapper.WorkingShiftDtoMapper;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.WorkingShiftException;
import ru.trae.backend.repository.WorkingShiftRepository;
import ru.trae.backend.util.DayOrNight;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class WorkingShiftService {
    private final WorkingShiftRepository workingShiftRepository;
    private final WorkingShiftDtoMapper workingShiftDtoMapper;

    public void createWorkingShift() {
        WorkingShift ws = new WorkingShift();
        ws.setStartShift(LocalDateTime.now());
        ws.setEmployees(new ArrayList<>());
        ws.setEnded(false);
        ws.setTimeOfDay(LocalDateTime.now().getHour() >= 18 ? DayOrNight.NIGHT : DayOrNight.DAY);

        workingShiftRepository.save(ws);
    }

    public WorkingShiftDto getActive() {
        if (!existsActiveWorkingShift())
            throw new WorkingShiftException(HttpStatus.BAD_REQUEST, "Нет активных рабочих смен.");

        return workingShiftDtoMapper.apply(workingShiftRepository.findByIsEndedFalse());
    }

    public boolean isEmployeeOnShift(int pinCode) {
        return workingShiftRepository.existsByIsEndedFalseAndEmployees_PinCode(pinCode);
    }

    public void closeWorkingShift() {
        if (!existsActiveWorkingShift()) return;

        WorkingShift ws = workingShiftRepository.findByIsEndedFalse();
        ws.setEnded(true);
        ws.setEndShift(LocalDateTime.now());

        workingShiftRepository.save(ws);
    }

    public void addOrRemoveEmployeeInShift(Employee employee) {
        if (!existsActiveWorkingShift())
            throw new WorkingShiftException(HttpStatus.BAD_REQUEST, "Нет активных рабочих смен.");

        WorkingShift ws = workingShiftRepository.findByIsEndedFalse();
        if (ws.getEmployees().contains(employee)) {
            ws.getEmployees().remove(employee);
        } else {
            ws.getEmployees().add(employee);
        }

        workingShiftRepository.save(ws);
    }

    public boolean existsActiveWorkingShift() {
        return workingShiftRepository.existsByIsEndedFalse();
    }
}
