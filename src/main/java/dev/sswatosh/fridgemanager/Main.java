package dev.sswatosh.fridgemanager;

import dev.sswatosh.fridgemanager.config.MainModule;
import dev.sswatosh.fridgemanager.handlers.FridgeHandler;
import dev.sswatosh.fridgemanager.handlers.FridgesHandler;
import dev.sswatosh.fridgemanager.handlers.ItemHandler;
import dev.sswatosh.fridgemanager.handlers.ItemsHandler;
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
                .prefix("api", apiChain -> apiChain
                    .prefix("fridges", fridgesChain -> fridgesChain
                        .prefix(":fridgeId", fridgeIdChain -> fridgeIdChain
                            .prefix("items", itemsChain -> itemsChain
                                .prefix(":itemId", itemIdChain -> itemIdChain
                                    .all(ItemHandler.class)
                                )
                                .all(ItemsHandler.class)
                            )
                            .all(FridgeHandler.class)
                        )
                        .all(FridgesHandler.class)
                    )
                )
            )
        );
    }
}
