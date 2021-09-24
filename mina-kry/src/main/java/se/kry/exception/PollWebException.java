package se.kry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PollWebException extends ResponseStatusException {
    public PollWebException(HttpStatus statusCode, String reason) {
        super(statusCode, reason);
    }
}
