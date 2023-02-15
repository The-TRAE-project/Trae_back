package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.repository.ManagerRepository;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managerRepository;

    public Manager getManager(long managerId) {
        return managerRepository.findById(managerId).orElseThrow(
                () -> new ManagerException(HttpStatus.NOT_FOUND, "Менеджер с " + managerId + " не найден"));
    }
}
