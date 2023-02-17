package ru.trae.backend.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.trae.backend.exceptionhandler.exception.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmployeeException.class)
    protected ResponseEntity<Response> handleException(EmployeeException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(ManagerException.class)
    protected ResponseEntity<Response> handleException(ManagerException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(WorkingShiftException.class)
    protected ResponseEntity<Response> handleException(WorkingShiftException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(ProjectException.class)
    protected ResponseEntity<Response> handleException(ProjectException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(OrderException.class)
    protected ResponseEntity<Response> handleException(OrderException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(OperationException.class)
    protected ResponseEntity<Response> handleException(OperationException e) {
        return new ResponseEntity<>(buildResponse(e), e.getStatus());
    }

    @ExceptionHandler(TypeWorkException.class)
    protected ResponseEntity<Response> handleException(TypeWorkException e) {
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
