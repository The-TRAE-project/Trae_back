package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TypeWorkException extends AbstractException {

	public TypeWorkException(HttpStatus status, String errorMessage) {
		super(status, errorMessage);
	}

}
