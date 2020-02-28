package dev.sswatosh.fridgemanager.handlers;

import dev.sswatosh.fridgemanager.controllers.EntityChecker;
import dev.sswatosh.fridgemanager.controllers.ItemController;
import dev.sswatosh.fridgemanager.controllers.PathIdParser;
import dev.sswatosh.fridgemanager.domain.ItemUpdateRequest;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class ItemHandler implements Handler {

    private final ItemController itemController;
    private final PathIdParser pathIdParser;
    private final EntityChecker entityChecker;

    @Inject
    public ItemHandler(
        ItemController itemController,
        PathIdParser pathIdParser,
        EntityChecker entityChecker
    ) {
        this.itemController = itemController;
        this.pathIdParser = pathIdParser;
        this.entityChecker = entityChecker;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        long fridgeId = getFridgeId(ctx);
        entityChecker.checkFridge(fridgeId);

        ctx.byMethod(method -> method
            .get(() -> ctx.render(json(itemController.getItem(getItemId(ctx), fridgeId))))
            .patch(() -> ctx.parse(fromJson(ItemUpdateRequest.class))
                .then(request -> {
                    itemController.updateItem(getItemId(ctx), fridgeId, request);
                    sendNoContent(ctx);
                })
            )
            .delete(() -> {
                itemController.deleteItem(getItemId(ctx), fridgeId);
                sendNoContent(ctx);
            })
        );
    }

    private long getFridgeId(Context ctx) {
        return pathIdParser.parse(ctx, Path.FRIDGES.id);
    }

    private long getItemId(Context ctx) {
        return pathIdParser.parse(ctx, Path.ITEMS.id);
    }

    private void sendNoContent(Context ctx) {
        ctx.getResponse().status(Status.NO_CONTENT);
        ctx.getResponse().send();
    }
}
