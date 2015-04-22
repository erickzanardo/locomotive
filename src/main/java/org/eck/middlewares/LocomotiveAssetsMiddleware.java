package org.eck.middlewares;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eck.Locomotive;
import org.eck.LocomotiveRequestWrapper;
import org.eck.LocomotiveResponseWrapper;

public class LocomotiveAssetsMiddleware implements LocomotiveMiddleware {

    private String assetsFolder;

    public LocomotiveAssetsMiddleware(String assetsFolder) {
        super();
        this.assetsFolder = assetsFolder;
    }

    @Override
    public void execute(Locomotive locomotive, LocomotiveRequestWrapper req,
            LocomotiveResponseWrapper resp) {

        InputStream resourceAsStream = LocomotiveAssetsMiddleware.class
                .getClassLoader().getResourceAsStream(assetsFolder + req.uri());
        if (resourceAsStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    resourceAsStream));
            StringBuilder out = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                resp.append(out.toString());
                req.processed();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
