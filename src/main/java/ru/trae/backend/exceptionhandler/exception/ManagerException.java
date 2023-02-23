package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ManagerException extends AbstractException {

	public ManagerException(HttpStatus status, String errorMessage) {
		super(status, errorMessage);
	}

}
