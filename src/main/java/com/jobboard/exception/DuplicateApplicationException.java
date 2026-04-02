package com.jobboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateApplicationException extends RuntimeException {

    public DuplicateApplicationException(Long jobId) {
        super("You have already applied to job with id: " + jobId);
    }
}
