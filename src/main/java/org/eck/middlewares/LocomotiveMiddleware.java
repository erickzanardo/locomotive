package org.eck.middlewares;

import org.eck.Locomotive;
import org.eck.LocomotiveRequestWrapper;
import org.eck.LocomotiveResponseWrapper;

public interface LocomotiveMiddleware {
    public abstract void execute(Locomotive locomotive, LocomotiveRequestWrapper req,
            LocomotiveResponseWrapper resp);
}
