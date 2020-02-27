package dev.sswatosh.fridgemanager;

import dev.sswatosh.fridgemanager.config.MainModule;
import dev.sswatosh.fridgemanager.handlers.HelloNameHandler;
import dev.sswatosh.fridgemanager.handlers.HelloWorldHandler;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

public class Main {
    public static void main(String... args) throws Exception {
        RatpackServer.start(server -> server
                .registry(Guice.registry(bindings -> bindings.module(MainModule.class)))
                .handlers(chain -> chain
                        .get(HelloWorldHandler.class)
                        .get(":name", HelloNameHandler.class)
                )
        );
    }
}
