package org.eck.exceptions;

public abstract class LocomotiveException extends RuntimeException {
    private static final long serialVersionUID = 3460152608323575829L;

    public abstract int code();

    public LocomotiveException(String message) {
        super(message);
    }
}
