package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.exceptionhandler.exception.TypeWorkException;
import ru.trae.backend.repository.TypeWorkRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeWorkService {
    private final TypeWorkRepository typeWorkRepository;

    public TypeWork saveNewTypeWork(NewTypeWorkDto dto) {
        TypeWork tw = new TypeWork();
        tw.setOperations(new ArrayList<>());
        tw.setName(dto.name());

        return typeWorkRepository.save(tw);
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
        return typeWorkRepository.existsByNameIgnoreCase(name);
    }

    public void checkAvailableByName(String name) {
        if (existsTypeByName(name))
            throw new TypeWorkException(HttpStatus.CONFLICT, "Вид работ с названием " + name + " уже существует");
    }
}
