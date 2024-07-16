package net.lotfi.ems.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
class CustomControllerAdvice {

    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<ErrorResponse> handleCustomErrorExceptions(
            Exception e
    ) {
        // casting the generic Exception e to CustomErrorException
        CustomErrorException customErrorException = (CustomErrorException) e;

        HttpStatus status = customErrorException.getStatus();

        return new ResponseEntity<>(
                new ErrorResponse(
                        status,
                        customErrorException.getMessage(),
                        customErrorException.getData()
                ),
                status
        );
    }

}