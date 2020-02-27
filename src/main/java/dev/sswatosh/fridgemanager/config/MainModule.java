package dev.sswatosh.fridgemanager.config;

import com.google.inject.AbstractModule;
import dev.sswatosh.fridgemanager.handlers.HelloNameHandler;
import dev.sswatosh.fridgemanager.handlers.HelloWorldHandler;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HelloWorldHandler.class);
        bind(HelloNameHandler.class);
    }
}
