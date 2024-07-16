package net.lotfi.ems.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
// src/main/java/exceptions/CustomErrorException.java

public class CustomErrorException extends RuntimeException {
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    private Object data = null;

    public CustomErrorException() {
        super();
    }
    public CustomErrorException(
            String message
    ) {
        super(message);
    }

    public CustomErrorException(
            HttpStatus status,
            String message
    ) {
        this(message);
        this.status = status;
    }

    public CustomErrorException(
            HttpStatus status,
            String message,
            Object data
    ) {
        this(
                status,
                message
        );
        this.data = data;
    }


    public HttpStatus getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setData(Object data) {
        this.data = data;
    }
}