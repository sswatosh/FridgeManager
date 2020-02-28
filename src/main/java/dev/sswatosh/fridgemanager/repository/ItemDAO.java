package dev.sswatosh.fridgemanager.repository;

import dev.sswatosh.fridgemanager.domain.Item;
import dev.sswatosh.fridgemanager.domain.ItemType;
import dev.sswatosh.fridgemanager.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class ItemDAO {
    Logger logger = LoggerFactory.getLogger(ItemDAO.class);

    private final JDBCHelper jdbcHelper;

    @Inject
    public ItemDAO(JDBCHelper jdbcHelper) {
        this.jdbcHelper = jdbcHelper;
    }

    private static final String SELECT_ITEM_BY_ID =
        "SELECT * FROM item WHERE id = ? AND fridge_id = ?";

    private static final String SELECT_ALL_ITEMS_IN_FRIDGE =
        "SELECT * FROM item WHERE fridge_id = ?";

    private static final String SELECT_ALL_ITEMS_OF_TYPE_IN_FRIDGE =
        "SELECT * FROM item WHERE type = ? AND fridge_id = ?";

    private static final String INSERT_ITEM =
        "INSERT INTO item (fridge_id, type, name) " +
        "VALUES (?, ?, ?)";

    private static final String UPDATE_ITEM =
        "UPDATE item SET " +
        "type = ?, " +
        "name = ? " +
        "WHERE id = ? AND fridge_id = ?";

    private static final String DELETE_ITEM =
        "DELETE FROM item WHERE id = ? AND fridge_id = ?";

    final Function<ResultSet, Item> parseItemFromResultSet = resultSet -> {
        try {
            long id = resultSet.getLong("id");
            long fridgeId = resultSet.getLong("fridge_id");
            ItemType type = ItemType.valueOf(resultSet.getString("type"));
            String name = resultSet.getString("name");
            return new Item(id, fridgeId, type, name);
        }
        catch (SQLException e) {
            logger.error("Failed to parse ResultSet when selecting item by id", e);
            throw new DatabaseException("Failed to parse ResultSet", e);
        }
    };

    public Item getItemById(long id, long fridgeId) {
        return jdbcHelper.selectOne(SELECT_ITEM_BY_ID, List.of(id, fridgeId), parseItemFromResultSet);
    }

    public List<Item> getAllItemsInFridge(long fridgeId) {
        return jdbcHelper.selectAll(SELECT_ALL_ITEMS_IN_FRIDGE, List.of(fridgeId), parseItemFromResultSet);
    }

    public long createItem(long fridgeId, ItemType type, String name) {
        return jdbcHelper.insert(INSERT_ITEM, List.of(fridgeId, type.name(), name));
    }

    public void updateItem(long id, long fridgeId, ItemType type, String name) {
        jdbcHelper.update(UPDATE_ITEM, List.of(type.name(), name, id, fridgeId));
    }

    public void deleteItem(long id, long fridgeId) {
        jdbcHelper.update(DELETE_ITEM, List.of(id, fridgeId));
    }
}
