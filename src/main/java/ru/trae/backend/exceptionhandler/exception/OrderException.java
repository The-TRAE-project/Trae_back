package ru.trae.backend.exceptionhandler.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderException extends AbstractException {

	public OrderException(HttpStatus status, String errorMessage) {
		super(status, errorMessage);
	}

}
