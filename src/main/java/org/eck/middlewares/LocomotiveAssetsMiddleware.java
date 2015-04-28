package org.eck.middlewares;

import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.eck.LocomotiveRequestWrapper;
import org.eck.LocomotiveResponseWrapper;

public class LocomotiveAssetsMiddleware implements LocomotiveMiddleware {

    private String assetsFolder;

    public LocomotiveAssetsMiddleware(String assetsFolder) {
        super();
        this.assetsFolder = assetsFolder;
    }

    @Override
    public void execute(LocomotiveRequestWrapper req,
            LocomotiveResponseWrapper resp) {

        try {
            URL resource = LocomotiveAssetsMiddleware.class.getClassLoader()
                    .getResource(assetsFolder + req.uri());

            if (resource != null) {
                req.processed();
                URI uri = resource.toURI();
                Path path = Paths.get(uri);

                final AsynchronousFileChannel channel = AsynchronousFileChannel
                        .open(path);
                final ByteBuffer buffer = ByteBuffer.allocate(100000);

                channel.read(buffer, 0, buffer,
                        new CompletionHandler<Integer, ByteBuffer>() {
                            public void completed(Integer result,
                                    ByteBuffer attachment) {
                                byte[] array = attachment.array();
                                resp.send(new String(Arrays.copyOf(array, result)));
                            }

                            public void failed(Throwable exception,
                                    ByteBuffer attachment) {
                                resp.status(500);
                                resp.send(exception.getMessage());
                            }
                        });

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
