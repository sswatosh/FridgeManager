package dev.sswatosh.fridgemanager;

import dev.sswatosh.fridgemanager.config.MainModule;
import dev.sswatosh.fridgemanager.handlers.HelloNameHandler;
import dev.sswatosh.fridgemanager.handlers.HelloWorldHandler;
import ratpack.dropwizard.metrics.DropwizardMetricsModule;
import ratpack.guice.Guice;
import ratpack.handling.RequestLogger;
import ratpack.server.RatpackServer;

public class Main {
    public static void main(String... args) throws Exception {
        RatpackServer.start(server -> server
                .registry(Guice.registry(bindings ->
                        bindings.module(MainModule.class)
                                .module(new DropwizardMetricsModule(),
                                        c -> c.jvmMetrics(true)
                                              .jmx()
                                )
                ))
                .handlers(chain -> chain
                        .all(RequestLogger.ncsa())
                        .get(HelloWorldHandler.class)
                        .get(":name", HelloNameHandler.class)
                )
        );
    }
}
