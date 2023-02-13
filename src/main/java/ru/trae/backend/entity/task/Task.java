package ru.trae.backend.entity.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Task {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private int period;
    private boolean isEnded;
}
