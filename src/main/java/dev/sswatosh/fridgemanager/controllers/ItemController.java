package dev.sswatosh.fridgemanager.controllers;

import dev.sswatosh.fridgemanager.domain.Item;
import dev.sswatosh.fridgemanager.domain.ItemType;
import dev.sswatosh.fridgemanager.domain.ItemUpdateRequest;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import dev.sswatosh.fridgemanager.repository.FridgeDAO;
import dev.sswatosh.fridgemanager.repository.ItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.api.Nullable;

import javax.inject.Inject;
import java.util.List;

public class ItemController {
    Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemDAO itemDAO;
    private final FridgeDAO fridgeDAO;

    @Inject
    public ItemController(
        ItemDAO itemDAO,
        FridgeDAO fridgeDAO
    ) {
        this.itemDAO = itemDAO;
        this.fridgeDAO = fridgeDAO;
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

        long newId = itemDAO.createItem(fridgeId, request.getType(), request.getName());
        Item item = new Item(newId, fridgeId, request.getType(), request.getName());

        logger.info("Item added. id: {}, fridgeId: {}", newId, fridgeId);
        return item;
    }

    public void updateItem(long itemId, long fridgeId, ItemUpdateRequest request) {
        Item currentItem = itemDAO.getItemById(itemId, fridgeId);

        ItemType type = getOriginalOrUpdated(currentItem.getType(), request.getType());
        String name = getOriginalOrUpdated(currentItem.getName(), request.getName());

        logger.info("Item updated. id: {}, fridgeId: {}", itemId, fridgeId);
        itemDAO.updateItem(itemId, fridgeId, type, name);
    }

    public void deleteItem(long itemId, long fridgeId) {
        logger.info("Item deleted. id: {}, fridgeId: {}", itemId, fridgeId);
        itemDAO.deleteItem(itemId, fridgeId);
    }

    private <T> T getOriginalOrUpdated(T original, @Nullable T updated) {
        return updated != null ? updated : original;
    }
}
