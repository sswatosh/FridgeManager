package dev.sswatosh.fridgemanager.repository;

import dev.sswatosh.fridgemanager.domain.Fridge;
import dev.sswatosh.fridgemanager.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class FridgeDAO {
    Logger logger = LoggerFactory.getLogger(FridgeDAO.class);

    private final JDBCHelper jdbcHelper;

    @Inject
    public FridgeDAO(JDBCHelper jdbcHelper) {
        this.jdbcHelper = jdbcHelper;
    }

    private static final String SELECT_FRIDGE_BY_ID =
        "SELECT * FROM fridge WHERE id = ?";

    private static final String SELECT_ALL_FRIDGES =
        "SELECT * FROM fridge";

    private static final String INSERT_FRIDGE =
        "INSERT INTO fridge (name) values (?)";

    private static final String UPDATE_FRIDGE =
        "UPDATE fridge SET " +
        "name = ? " +
        "WHERE id = ? ";

    final Function<ResultSet, Fridge> parseFridgeFromResultSet = resultSet -> {
        try {
            long resultId = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Fridge(resultId, name);
        }
        catch (SQLException e) {
            logger.error("Failed to parse ResultSet when selecting fridge by id", e);
            throw new DatabaseException("Failed to parse ResultSet", e);
        }
    };

    public Fridge getFridgeById(long id) {
        return jdbcHelper.selectById(SELECT_FRIDGE_BY_ID, id, parseFridgeFromResultSet);
    }

    public List<Fridge> getAllFridges() {
        return jdbcHelper.selectAll(SELECT_ALL_FRIDGES, parseFridgeFromResultSet);
    }

    public long createFridge(String name) {
        long newId = jdbcHelper.insert(INSERT_FRIDGE, List.of(name));
        logger.info("New fridge created. Id: {}, Name: {}", newId, name);
        return newId;
    }

    public void updateFridge(long id, String name) {
        jdbcHelper.update(UPDATE_FRIDGE, List.of(name), id);
        logger.info("Fridge updated. Id: {}, Name: {}", id, name);
    }
}
