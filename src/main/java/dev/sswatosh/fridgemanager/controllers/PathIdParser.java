package dev.sswatosh.fridgemanager.controllers;

import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathIdParser {
    Logger logger = LoggerFactory.getLogger(PathIdParser.class);

    public long parse(String pathToken) {
        if (pathToken == null) {
            throw new NullPointerException("Path token not found");
        }

        try {
            return Long.parseLong(pathToken);
        } catch (NumberFormatException e) {
            logger.error("Failed to parse id from path token", e);
            throw new ValidationException("Failed to parse id from path token", e);
        }
    }
}
