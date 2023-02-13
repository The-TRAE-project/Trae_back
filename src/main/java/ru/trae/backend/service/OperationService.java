package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.repository.OperationRepository;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;

}
