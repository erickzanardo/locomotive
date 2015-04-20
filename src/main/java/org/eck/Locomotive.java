package org.eck;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eck.path.RouteParser;

public class Locomotive {
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    private int port;
    private List<String> routeWagons = new ArrayList<String>();
    private Map<String, Map<String, Wagon>> wagons = new HashMap<>();
    private Channel ch;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public Locomotive(int port) {
        super();
        this.port = port;
    }

    public void get(String url, Wagon wagon) {
        addWagon(GET, url, wagon);
    }

    public void put(String url, Wagon wagon) {
        addWagon(PUT, url, wagon);
    }

    public void post(String url, Wagon wagon) {
        addWagon(POST, url, wagon);
    }

    public void delete(String url, Wagon wagon) {
        addWagon(DELETE, url, wagon);
    }

    public String getUriPattern(String uri) {
        for (String route : routeWagons) {
            if (RouteParser.macthes(route, uri)) {
                return route;
            }
        }
        return null;
    }

    public Wagon getWagon(String method, String url) {
        if (wagons.get(method) != null) {
            return wagons.get(method).get(url);
        }
        return null;
    }

    private void addWagon(String method, String url, Wagon wagon) {
        if (wagons.get(method) == null) {
            wagons.put(method, new HashMap<String, Wagon>());
        }
        if (url.contains(":")) {
            routeWagons.add(url);
        }
        wagons.get(method).put(url, wagon);
    }

    public void shutdown() {
        try {
            ch.close().sync();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void boot() {
        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new LocomotiveInitializer(this));

            ch = b.bind(port).sync().channel();

            System.out.println(
                    "Open your web browser and navigate to http://127.0.0.1:" + port+ '/'
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Locomotive locomotive = new Locomotive(8080);
        locomotive.get("/user", (req, resp) -> {
            resp.append("Hello sire");
        });
        locomotive.boot();
    }
}
