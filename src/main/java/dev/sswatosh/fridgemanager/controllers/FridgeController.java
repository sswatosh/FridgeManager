package dev.sswatosh.fridgemanager.controllers;

import com.codahale.metrics.MetricRegistry;
import dev.sswatosh.fridgemanager.domain.Fridge;
import dev.sswatosh.fridgemanager.domain.FridgeUpdateRequest;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import dev.sswatosh.fridgemanager.metrics.Metrics;
import dev.sswatosh.fridgemanager.repository.FridgeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class FridgeController {
    Logger logger = LoggerFactory.getLogger(FridgeController.class);

    private final FridgeDAO fridgeDAO;
    private final MetricRegistry metricRegistry;

    @Inject
    public FridgeController(
        FridgeDAO fridgeDAO,
        MetricRegistry metricRegistry
    ) {
        this.fridgeDAO = fridgeDAO;
        this.metricRegistry = metricRegistry;
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

        long newId = fridgeDAO.createFridge(request.getName());
        Fridge fridge = new Fridge(newId, request.getName());

        logger.info("Fridge added. Id: {}", newId);
        metricRegistry.counter(Metrics.FRIDGE_COUNT).inc();
        return fridge;
    }

    public void updateFridge(long id, FridgeUpdateRequest request) {
        Fridge currentFridge = fridgeDAO.getFridgeById(id);

        String updatedName = request.getName() != null ?
            request.getName():
            currentFridge.getName();

        fridgeDAO.updateFridge(id, updatedName);
        logger.info("Fridge updated. Id: {}", id);
    }
}
