package ru.trae.backend.exceptionhandler.exception;

import org.springframework.http.HttpStatus;

public class CustomJWTVerificationException extends AbstractException {

	public CustomJWTVerificationException(HttpStatus status, String errorMessage) {
		super(status, errorMessage);
	}

}
