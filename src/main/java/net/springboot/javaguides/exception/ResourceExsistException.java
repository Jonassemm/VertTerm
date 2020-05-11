package net.springboot.javaguides.exception;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus
public class ResourceExsistException extends RuntimeException {

    private static final long serialVersionUID = 1 ;

    public ResourceExsistException(String message) {
        super(message);
    }
   
    public ResourceExsistException(String message, Throwable throwable) {
        super(message, throwable);
    }
}