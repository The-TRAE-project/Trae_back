package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayloadPieceException extends AbstractException {

	public PayloadPieceException(HttpStatus status, String errorMessage) {
		super(status, errorMessage);
	}

}
