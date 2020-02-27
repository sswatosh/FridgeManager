package dev.sswatosh.fridgemanager.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class HelloWorldHandler implements Handler {
    Logger logger = LoggerFactory.getLogger(HelloWorldHandler.class);

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.render("Hello World!");
        logger.info("Hello logs!");
    }
}
