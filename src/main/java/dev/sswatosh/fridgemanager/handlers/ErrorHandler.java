package dev.sswatosh.fridgemanager.handlers;

import dev.sswatosh.fridgemanager.exceptions.NotFoundException;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import ratpack.error.ServerErrorHandler;
import ratpack.handling.Context;
import ratpack.http.Status;

import java.util.Map;

import static ratpack.jackson.Jackson.json;

public class ErrorHandler implements ServerErrorHandler {

    @Override
    public void error(Context context, Throwable throwable) throws Exception {
        if (throwable instanceof ValidationException) {
            context.getResponse().status(Status.BAD_REQUEST);
        } else if (throwable instanceof NotFoundException) {
            context.getResponse().status(Status.NOT_FOUND);
        } else {
            context.getResponse().status(Status.INTERNAL_SERVER_ERROR);
        }

        context.render(json(Map.of(
            "error", throwable.getClass().getName(),
            "message", throwable.getMessage()
        )));
    }
}
