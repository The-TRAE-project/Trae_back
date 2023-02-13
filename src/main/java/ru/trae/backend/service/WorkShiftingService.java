package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.repository.WorkShiftingRepository;

@Service
@RequiredArgsConstructor
public class WorkShiftingService {
    private final WorkShiftingRepository workShiftingRepository;

}
