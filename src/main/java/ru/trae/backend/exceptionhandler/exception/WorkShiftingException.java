package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WorkShiftingException extends AbstractException {
    public WorkShiftingException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
