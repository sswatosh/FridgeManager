package dev.sswatosh.fridgemanager;

import dev.sswatosh.fridgemanager.config.MainModule;
import dev.sswatosh.fridgemanager.handlers.FridgeHandler;
import dev.sswatosh.fridgemanager.handlers.FridgesHandler;
import dev.sswatosh.fridgemanager.handlers.ItemHandler;
import dev.sswatosh.fridgemanager.handlers.ItemsHandler;
import dev.sswatosh.fridgemanager.handlers.Path;
import ratpack.dropwizard.metrics.DropwizardMetricsModule;
import ratpack.guice.Guice;
import ratpack.handling.RequestLogger;
import ratpack.hikari.HikariModule;
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
                        .module(HikariModule.class, config -> {
                            config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
                            config.addDataSourceProperty("URL", "jdbc:h2:mem:fridgeManager;INIT=RUNSCRIPT FROM 'classpath:/schema.sql'");
                        })
            ))
            .handlers(chain -> chain
                .all(RequestLogger.ncsa())
                .prefix("api", apiChain -> apiChain
                    .prefix(Path.FRIDGES.collection, fridgesChain -> fridgesChain
                        .prefix(Path.FRIDGES.idPrefix, fridgeIdChain -> fridgeIdChain
                            .prefix(Path.ITEMS.collection, itemsChain -> itemsChain
                                .prefix(Path.ITEMS.idPrefix, itemIdChain -> itemIdChain
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
