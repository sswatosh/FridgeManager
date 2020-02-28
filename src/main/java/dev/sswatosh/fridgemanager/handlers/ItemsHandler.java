package dev.sswatosh.fridgemanager.handlers;

import dev.sswatosh.fridgemanager.auth.AuthUtil;
import dev.sswatosh.fridgemanager.auth.Permissions;
import dev.sswatosh.fridgemanager.controllers.EntityChecker;
import dev.sswatosh.fridgemanager.controllers.ItemController;
import dev.sswatosh.fridgemanager.controllers.PathIdParser;
import dev.sswatosh.fridgemanager.domain.ItemUpdateRequest;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class ItemsHandler implements Handler {

    private final ItemController itemController;
    private final PathIdParser pathIdParser;
    private final EntityChecker entityChecker;

    @Inject
    public ItemsHandler(
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
            .get(() -> ctx.insert(
                AuthUtil.requirePermissions(Permissions.VIEW_ITEMS),
                context -> context.render(json(itemController.getItems(getFridgeId(ctx))))
            ))
            .post(() -> ctx.insert(
                AuthUtil.requirePermissions(Permissions.ADD_EDIT_ITEMS),
                context -> context.parse(fromJson(ItemUpdateRequest.class))
                                  .then(request -> ctx.render(json(itemController.addItem(getFridgeId(ctx), request))))
            ))
        );
    }

    private long getFridgeId(Context ctx) {
        return pathIdParser.parse(ctx, Path.FRIDGES.id);
    }
}
