package com.jobboard.service;

import com.jobboard.enums.ApplicationStatus;
import com.jobboard.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Service;

@Service
public class StatusTransitionValidator {

    public void validate(ApplicationStatus current, ApplicationStatus target) {
        if (!current.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(current, target);
        }
    }
}
