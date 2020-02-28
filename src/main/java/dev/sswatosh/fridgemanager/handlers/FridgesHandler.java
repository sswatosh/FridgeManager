package dev.sswatosh.fridgemanager.handlers;

import dev.sswatosh.fridgemanager.auth.AuthUtil;
import dev.sswatosh.fridgemanager.auth.Permissions;
import dev.sswatosh.fridgemanager.controllers.FridgeController;
import dev.sswatosh.fridgemanager.domain.FridgeUpdateRequest;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;

import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

public class FridgesHandler implements Handler {

    private final FridgeController fridgeController;

    @Inject
    public FridgesHandler(FridgeController fridgeController) {
        this.fridgeController = fridgeController;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
            .get(() -> ctx.insert(
                AuthUtil.requirePermissions(Permissions.VIEW_FRIDGES),
                context -> context.render(json(fridgeController.getFridges()))
            ))
            .post(() -> ctx.insert(
                AuthUtil.requirePermissions(Permissions.ADD_EDIT_FRIDGES),
                context -> context.parse(fromJson(FridgeUpdateRequest.class))
                                  .then(request -> ctx.render(json(fridgeController.addFridge(request))))
            ))
        );
    }
}
