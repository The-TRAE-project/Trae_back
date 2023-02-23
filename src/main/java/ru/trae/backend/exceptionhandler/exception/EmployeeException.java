package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmployeeException extends AbstractException {
    public EmployeeException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
