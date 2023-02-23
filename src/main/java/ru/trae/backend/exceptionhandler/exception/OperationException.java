package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OperationException extends AbstractException {
    public OperationException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
