package org.eck.exceptions;

public class GenericLocomotiveException extends LocomotiveException {
    private static final long serialVersionUID = 1013318712034921671L;

    private int code;

    public GenericLocomotiveException(int code, String message) {
        super(message);
        this.code = code;
    }

    @Override
    public int code() {
        return code;
    }

}
