package by.saxophonyx.app.server.contracts.exceptions;

import java.lang.Exception;

public class GeneralServerException extends Exception {
    public GeneralServerException() {
        super();
    }

    public GeneralServerException(String message) {
        super(message);
    }
}
