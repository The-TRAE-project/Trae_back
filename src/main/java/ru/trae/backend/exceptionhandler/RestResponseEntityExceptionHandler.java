package ru.trae.backend.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.trae.backend.exceptionhandler.exception.AbstractException;
import ru.trae.backend.exceptionhandler.exception.EmployeeException;
import ru.trae.backend.exceptionhandler.exception.WorkShiftingException;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmployeeException.class)
    protected ResponseEntity<Response> handleException(EmployeeException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(WorkShiftingException.class)
    protected ResponseEntity<Response> handleException(WorkShiftingException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }


    private Response buildResponse(AbstractException e) {
        return Response.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(e.getMessage())
                .status(e.getStatus())
                .build();
    }
}
