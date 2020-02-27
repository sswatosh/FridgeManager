package dev.sswatosh.fridgemanager.handlers;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class FridgesHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.render("list of fridges"))
            .post(() -> ctx.render("add a fridge"))
        );
    }
}
