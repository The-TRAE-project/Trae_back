package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.NewTypeWorkDto;
import ru.trae.backend.dto.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.exceptionhandler.exception.TypeWorkException;
import ru.trae.backend.repository.TypeWorkRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeWorkService {
    private final TypeWorkRepository typeWorkRepository;

    public void saveNewTypeWork(NewTypeWorkDto dto) {
        TypeWork tw = new TypeWork();
        tw.setEmployees(new ArrayList<>());
        tw.setOperations(new ArrayList<>());
        tw.setName(dto.name());

        typeWorkRepository.save(tw);
    }

    public TypeWork getTypeWorkById(long id) {
        return typeWorkRepository.findById(id).orElseThrow(
                () -> new TypeWorkException(HttpStatus.NOT_FOUND, "Вид работы с ID " + id + " не найден")
        );
    }

    public List<TypeWorkDto> getTypes() {
        return typeWorkRepository.findAll()
                .stream()
                .map(t -> new TypeWorkDto(t.getId(), t.getName()))
                .toList();
    }

    public boolean existsTypeByName(String name) {
        return typeWorkRepository.existsByName(name);
    }
}
