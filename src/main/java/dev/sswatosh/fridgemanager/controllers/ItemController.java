package dev.sswatosh.fridgemanager.controllers;

import com.codahale.metrics.MetricRegistry;
import dev.sswatosh.fridgemanager.domain.Item;
import dev.sswatosh.fridgemanager.domain.ItemType;
import dev.sswatosh.fridgemanager.domain.ItemUpdateRequest;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import dev.sswatosh.fridgemanager.metrics.Metrics;
import dev.sswatosh.fridgemanager.repository.ItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.api.Nullable;

import javax.inject.Inject;
import java.util.List;

public class ItemController {
    Logger logger = LoggerFactory.getLogger(ItemController.class);

    static final int MAX_SODAS_PER_FRIDGE = 12;

    private final ItemDAO itemDAO;
    private final MetricRegistry metricRegistry;

    @Inject
    public ItemController(
        ItemDAO itemDAO,
        MetricRegistry metricRegistry
    ) {
        this.itemDAO = itemDAO;
        this.metricRegistry = metricRegistry;
    }

    public List<Item> getItems(long fridgeId) {
        return itemDAO.getAllItemsInFridge(fridgeId);
    }

    public Item getItem(long itemId, long fridgeId) {
        return itemDAO.getItemById(itemId, fridgeId);
    }

    public Item addItem(long fridgeId, ItemUpdateRequest request) {
        if (request.getType() == null) {
            throw new ValidationException("'type' field is required'");
        } else if (request.getName() == null) {
            throw new ValidationException("'name' field is required'");
        }

        if (request.getType() == ItemType.SODA) {
            checkSodaCountBeforeNewSoda(fridgeId);
        }

        long newId = itemDAO.createItem(fridgeId, request.getType(), request.getName());
        Item item = new Item(newId, fridgeId, request.getType(), request.getName());

        logger.info("Item added. id: {}, fridgeId: {}", newId, fridgeId);
        metricRegistry.counter(Metrics.ITEM_COUNT).inc();
        return item;
    }

    public void updateItem(long itemId, long fridgeId, ItemUpdateRequest request) {
        Item currentItem = itemDAO.getItemById(itemId, fridgeId);

        if (request.getType() == ItemType.SODA && currentItem.getType() != ItemType.SODA) {
            checkSodaCountBeforeNewSoda(fridgeId);
        }

        ItemType type = getOriginalOrUpdated(currentItem.getType(), request.getType());
        String name = getOriginalOrUpdated(currentItem.getName(), request.getName());

        itemDAO.updateItem(itemId, fridgeId, type, name);
        logger.info("Item updated. id: {}, fridgeId: {}", itemId, fridgeId);
    }

    public void deleteItem(long itemId, long fridgeId) {
        itemDAO.deleteItem(itemId, fridgeId);
        logger.info("Item deleted. id: {}, fridgeId: {}", itemId, fridgeId);
        metricRegistry.counter(Metrics.ITEM_COUNT).dec();
    }

    private <T> T getOriginalOrUpdated(T original, @Nullable T updated) {
        return updated != null ? updated : original;
    }

    private void checkSodaCountBeforeNewSoda(long fridgeId) {
        int sodaCount = itemDAO.getGetSodaCountInFridge(fridgeId);
        if (sodaCount >= MAX_SODAS_PER_FRIDGE) {
            throw new ValidationException("Fridge already contains the maximum number of sodas");
        }
    }
}
