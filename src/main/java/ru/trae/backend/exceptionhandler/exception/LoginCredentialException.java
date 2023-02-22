package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class LoginCredentialException extends AbstractException {

    public LoginCredentialException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
