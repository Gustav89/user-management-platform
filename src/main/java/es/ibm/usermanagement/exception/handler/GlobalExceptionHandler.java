package es.ibm.usermanagement.exception.handler;


import es.ibm.usermanagement.dto.ErrorResponse;
import es.ibm.usermanagement.exception.custom.AsyncTaskFailureException;
import es.ibm.usermanagement.exception.custom.InvalidParamException;
import es.ibm.usermanagement.exception.custom.UserAlreadyExistsException;
import es.ibm.usermanagement.exception.custom.UserNotFoundExeption;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex) {
        log.error(String.format("Enter in %s handler",ex.getClass()));

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(AsyncTaskFailureException.class)
    public ResponseEntity<ErrorResponse> handleAsyncTaskFailureException(AsyncTaskFailureException ex) {
        log.error(String.format("Enter in %s handler",ex.getClass()));
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundExeption.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundExeption(UserNotFoundExeption ex) {
       log.error(String.format("Enter in %s handler",ex.getClass()));
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParamException(InvalidParamException ex) {
        log.error(String.format("Enter in %s handler",ex.getClass()));
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                String.format("Invalid %s", ex.getParameter())
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error(String.format("Enter in %s handler",ex.getClass()));
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                String.format("Invalid %s", ex.getBindingResult().getFieldErrors().get(0).getField())
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
