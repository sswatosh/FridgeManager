package dev.sswatosh.fridgemanager.handlers;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class FridgeHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.render("details for fridge " + ctx.getAllPathTokens().get("fridgeId")))
            .patch(() -> ctx.render("update fridge " + ctx.getAllPathTokens().get("fridgeId")))
        );
    }
}
