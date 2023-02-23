package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractException extends RuntimeException {

	private final HttpStatus status;

	public AbstractException(HttpStatus status, String errorMessage) {
		super(errorMessage);
		this.status = status;
	}

}
