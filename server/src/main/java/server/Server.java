package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        //create javalin object, takes in config (http response and request object), if path matches file name in
        // web will return contents of that file
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
