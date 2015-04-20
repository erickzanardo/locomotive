package org.eck;

import java.util.HashMap;
import java.util.Map;

public class LocomotiveResponseWrapper {
    private StringBuilder buff = new StringBuilder();
    private Integer status;
    private Map<String, String> headers = new HashMap<String, String>();

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
    }

    public Map<String, String> headers() {
        return headers;
    }

    public void contentType(String contentType) {
        headers.put("Content-Type", contentType);
    };
}
