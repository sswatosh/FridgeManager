package dev.sswatosh.fridgemanager.handlers;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class HelloNameHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.render("Hello " + ctx.getPathTokens().get("name") + "!");
    }
}
