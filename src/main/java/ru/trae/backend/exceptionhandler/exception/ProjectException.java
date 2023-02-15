package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProjectException extends AbstractException {
    public ProjectException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
