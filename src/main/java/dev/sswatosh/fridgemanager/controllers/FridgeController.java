package dev.sswatosh.fridgemanager.controllers;

import dev.sswatosh.fridgemanager.domain.Fridge;
import dev.sswatosh.fridgemanager.domain.FridgeUpdateRequest;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import dev.sswatosh.fridgemanager.repository.FridgeDAO;

import javax.inject.Inject;
import java.util.List;

public class FridgeController {

    private final FridgeDAO fridgeDAO;

    @Inject
    public FridgeController(FridgeDAO fridgeDAO) {
        this.fridgeDAO = fridgeDAO;
    }

    public List<Fridge> getFridges() {
        return fridgeDAO.getAllFridges();
    }

    public Fridge getFridge(long id) {
        return fridgeDAO.getFridgeById(id);
    }

    public Fridge addFridge(FridgeUpdateRequest request) throws ValidationException {
        if (request.getName() == null) {
            throw new ValidationException("missing 'name' field");
        }

        long newFridgeId = fridgeDAO.createFridge(request.getName());
        return new Fridge(newFridgeId, request.getName());
    }

    public void updateFridge(long id, FridgeUpdateRequest request) {
        Fridge currentFridge = fridgeDAO.getFridgeById(id);

        String updatedName = request.getName() != null ?
            request.getName():
            currentFridge.getName();

        fridgeDAO.updateFridge(id, updatedName);
    }
}
