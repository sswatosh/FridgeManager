package dev.sswatosh.fridgemanager.handlers;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class ItemHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.render("details for item " + ctx.getAllPathTokens().get("itemId") + " in fridge " + ctx.getAllPathTokens().get("fridgeId")))
            .patch(() -> ctx.render("update item " + ctx.getAllPathTokens().get("itemId") + " in fridge " + ctx.getAllPathTokens().get("fridgeId")))
            .delete(() -> ctx.render("delete item " + ctx.getAllPathTokens().get("itemId") + " in fridge " + ctx.getAllPathTokens().get("fridgeId")))
        );
    }
}
