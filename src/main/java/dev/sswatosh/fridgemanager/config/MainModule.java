package dev.sswatosh.fridgemanager.config;

import com.google.inject.AbstractModule;
import dev.sswatosh.fridgemanager.handlers.FridgeHandler;
import dev.sswatosh.fridgemanager.handlers.FridgesHandler;
import dev.sswatosh.fridgemanager.handlers.ItemHandler;
import dev.sswatosh.fridgemanager.handlers.ItemsHandler;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FridgesHandler.class);
        bind(FridgeHandler.class);
        bind(ItemsHandler.class);
        bind(ItemHandler.class);
    }
}
