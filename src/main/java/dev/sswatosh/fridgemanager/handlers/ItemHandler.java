package dev.sswatosh.fridgemanager.handlers;

import ratpack.handling.Context;
import ratpack.handling.Handler;

public class ItemHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.render("details for item " + ctx.getAllPathTokens().get(Path.ITEMS.id) + " in fridge " + ctx.getAllPathTokens().get(Path.FRIDGES.id)))
            .patch(() -> ctx.render("update item " + ctx.getAllPathTokens().get(Path.ITEMS.id) + " in fridge " + ctx.getAllPathTokens().get(Path.FRIDGES.id)))
            .delete(() -> ctx.render("delete item " + ctx.getAllPathTokens().get(Path.ITEMS.id) + " in fridge " + ctx.getAllPathTokens().get(Path.FRIDGES.id)))
        );
    }
}
