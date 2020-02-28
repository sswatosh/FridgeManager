package dev.sswatosh.fridgemanager.handlers;

import dev.sswatosh.fridgemanager.auth.AuthUtil;
import dev.sswatosh.fridgemanager.auth.Permissions;
import dev.sswatosh.fridgemanager.controllers.FridgeController;
import dev.sswatosh.fridgemanager.controllers.PathIdParser;
import dev.sswatosh.fridgemanager.domain.FridgeUpdateRequest;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;

import javax.inject.Inject;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class FridgeHandler implements Handler {

    private final FridgeController fridgeController;
    private final PathIdParser pathIdParser;

    @Inject
    public FridgeHandler(
        FridgeController fridgeController,
        PathIdParser pathIdParser
    ) {
        this.fridgeController = fridgeController;
        this.pathIdParser = pathIdParser;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.insert(
                AuthUtil.requirePermissions(Permissions.VIEW_FRIDGES),
                context -> context.render(json(fridgeController.getFridge(getFridgeId(ctx))))
            ))
            .patch(() -> ctx.insert(
                AuthUtil.requirePermissions(Permissions.ADD_EDIT_FRIDGES),
                context -> context.parse(fromJson(FridgeUpdateRequest.class))
                                  .then(request -> {
                                      fridgeController.updateFridge(getFridgeId(ctx), request);
                                      ctx.getResponse().status(Status.NO_CONTENT);
                                      ctx.getResponse().send();
                                  })
            ))
        );
    }

    private long getFridgeId(Context ctx) {
        return pathIdParser.parse(ctx, Path.FRIDGES.id);
    }
}
