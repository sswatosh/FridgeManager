package dev.sswatosh.fridgemanager.handlers;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class ItemsHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.render("list items in fridge " + ctx.getAllPathTokens().get(Path.FRIDGES.id)))
            .post(() -> ctx.render("add item to fridge " + ctx.getAllPathTokens().get(Path.FRIDGES.id)))
        );
    }
}
