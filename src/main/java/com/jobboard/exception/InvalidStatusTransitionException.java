package com.jobboard.exception;

import com.jobboard.enums.ApplicationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(ApplicationStatus from, ApplicationStatus to) {
        super("Invalid status transition: " + from + " → " + to);
    }
}
