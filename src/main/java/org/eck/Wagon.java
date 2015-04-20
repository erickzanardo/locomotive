package org.eck;

public interface Wagon {
    abstract void process(LocomotiveRequestWrapper req,
            LocomotiveResponseWrapper resp);
}
