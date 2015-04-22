package org.eck.middlewares;

import org.eck.LocomotiveRequestWrapper;
import org.eck.LocomotiveResponseWrapper;

public interface LocomotiveMiddleware {
    public abstract void execute(LocomotiveRequestWrapper req,
            LocomotiveResponseWrapper resp);
}
