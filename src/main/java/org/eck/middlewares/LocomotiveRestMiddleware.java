package org.eck.middlewares;

import org.eck.Locomotive;
import org.eck.LocomotiveRequestWrapper;
import org.eck.LocomotiveResponseWrapper;
import org.eck.Wagon;

public class LocomotiveRestMiddleware implements LocomotiveMiddleware {

    private Locomotive locomotive;

    public LocomotiveRestMiddleware(Locomotive locomotive) {
        super();
        this.locomotive = locomotive;
    }

    @Override
    public void execute(LocomotiveRequestWrapper req,
            LocomotiveResponseWrapper resp) {

        String uri = req.uri();
        String pattern = req.pattern();
        String method = req.method();
        Wagon wagon = locomotive.getWagon(method, pattern != null ? pattern
                : uri);

        if (wagon != null) {
            wagon.process(req, resp);
            req.processed();
        }
    }
}
