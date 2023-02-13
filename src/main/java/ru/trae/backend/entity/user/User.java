package ru.trae.backend.entity.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class User {
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer phone;

}
