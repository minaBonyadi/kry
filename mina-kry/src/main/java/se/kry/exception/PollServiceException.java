package se.kry.exception;

import se.kry.enumeration.Status;

public class PollServiceException extends RuntimeException{

    public PollServiceException(String message) {
        super(message);
    }

    public PollServiceException(Status status) {
        super(String.valueOf(status));
    }
}
