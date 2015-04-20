package org.eck;

public class LocomotiveResponseWrapper {
    private StringBuilder buff = new StringBuilder();
    private Integer status;

    public void append(String value) {
        buff.append(value);
    }

    public String toString() {
        return buff.toString();
    }

    public Integer status() {
        return status;
    }

    public void status(int status) {
        this.status = status;
    };
}
