package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WorkingShiftException extends AbstractException {

	public WorkingShiftException(HttpStatus status, String errorMessage) {
		super(status, errorMessage);
	}

}
