package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.mapper.ManagerDtoMapper;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.repository.ManagerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final ManagerDtoMapper managerDtoMapper;

    public Manager saveNewManager(ManagerRegisterDto dto) {
        Manager m = new Manager();
        m.setFirstName(dto.firstName());
        m.setMiddleName(dto.middleName());
        m.setLastName(dto.lastName());
        m.setPhone(dto.phone());
        m.setEmail(dto.email());
        m.setUsername(dto.username());
        m.setPassword(dto.password());
        m.setProjects(new ArrayList<>());
        m.setOrders(new ArrayList<>());

        return managerRepository.save(m);
    }

    public Manager getManagerById(long managerId) {
        return managerRepository.findById(managerId).orElseThrow(
                () -> new ManagerException(HttpStatus.NOT_FOUND, "Менеджер с " + managerId + " не найден"));
    }

    public List<ManagerDto> getAllManagers() {
        return managerRepository.findAll()
                .stream()
                .map(managerDtoMapper)
                .toList();
    }

    public ManagerDto convertFromManager(Manager manager) {
        return managerDtoMapper.apply(manager);
    }

    public boolean existsManagerByEmail(String email) {
        return managerRepository.existsByEmailIgnoreCase(email);
    }

    public boolean existsManagerByUsername(String username) {
        return managerRepository.existsByUsernameIgnoreCase(username);
    }

    public void checkAvailableEmail(String email) {
        if (existsManagerByEmail(email))
            throw new ManagerException(HttpStatus.CONFLICT, "Email: " + email + " уже используется");
    }

    public void checkAvailableUsername(String username) {
        if (existsManagerByUsername(username))
            throw new ManagerException(HttpStatus.CONFLICT, "Username: " + username + " уже используется");
    }

}
