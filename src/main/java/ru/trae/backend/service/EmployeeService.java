package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.ShortEmployeeDto;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.dto.mapper.EmployeeDtoMapper;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.EmployeeException;
import ru.trae.backend.repository.EmployeeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeDtoMapper employeeDtoMapper;
    private final WorkingShiftService workingShiftService;
    private final TimeControlService timeControlService;

    public void saveNewEmployee(EmployeeDto dto) {
        Employee e = new Employee();
        e.setFirstName(dto.firstName());
        e.setMiddleName(dto.middleName());
        e.setLastName(dto.lastName());
        e.setPhone(dto.phone());
        e.setPinCode(dto.pinCode());

        employeeRepository.save(e);
    }

    public Employee getEmployeeById(long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new EmployeeException(HttpStatus.NOT_FOUND, "Работник с ID " + id + " не найден"));
    }

    public EmployeeDto getEmpDtoById(long id) {
        return employeeDtoMapper.apply(getEmployeeById(id));
    }

    public ShortEmployeeDto getShortDtoEmpById(long id) {
        Employee e = getEmployeeById(id);
        return new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName());
    }

    public ShortEmployeeDto checkInEmployee(int pin) {
        Optional<Employee> employee = employeeRepository.findByPinCode(pin);

        if (employee.isEmpty())
            throw new EmployeeException(HttpStatus.NOT_FOUND, "Работник с пинкодом " + pin + " не найден");
        if (!workingShiftService.employeeOnShift(true, employee.get().getId()))
            workingShiftService.arrivalEmployeeOnShift(employee.get());

        return new ShortEmployeeDto(employee.get().getId(), employee.get().getFirstName(), employee.get().getLastName());
    }

    public ShortEmployeeDto departureEmployee(long id) {
        Employee e = getEmployeeById(id);

        if (workingShiftService.employeeOnShift(true, e.getId()))
            timeControlService.updateTimeControlForDeparture(id, LocalDateTime.now());

        return new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName());
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeDtoMapper)
                .toList();
    }

    public boolean existsEmpByPinCode(int pinCode) {
        return employeeRepository.existsByPinCode(pinCode);
    }

    public void checkAvailablePinCode(int pinCode) {
        if (existsEmpByPinCode(pinCode))
            throw new EmployeeException(HttpStatus.CONFLICT, "Пинкод: " + pinCode + " уже используется");
    }

}
