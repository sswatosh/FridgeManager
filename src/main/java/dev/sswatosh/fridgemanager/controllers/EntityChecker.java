package dev.sswatosh.fridgemanager.controllers;

import dev.sswatosh.fridgemanager.repository.FridgeDAO;

import javax.inject.Inject;

public class EntityChecker {

    private final FridgeDAO fridgeDAO;

    @Inject
    public EntityChecker(FridgeDAO fridgeDAO) {
        this.fridgeDAO = fridgeDAO;
    }

    public void checkFridge(long fridgeId) {
        fridgeDAO.getFridgeById(fridgeId);
    }
}
