package dev.sswatosh.fridgemanager.repository;

import dev.sswatosh.fridgemanager.exceptions.DatabaseException;
import dev.sswatosh.fridgemanager.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class JDBCHelper {
    Logger logger = LoggerFactory.getLogger(JDBCHelper.class);

    protected final DataSource dataSource;

    @Inject
    public JDBCHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected <T> T selectOne(String query, List<Object> arguments, Function<ResultSet, T> resultMapper) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement select = connection.prepareStatement(query);
            setArguments(select, arguments);
            ResultSet results = select.executeQuery();
            if (results.next()) {
                return resultMapper.apply(results);
            } else {
                throw new NotFoundException("Entity not found");
            }
        } catch (SQLException e) {
            logger.error("Select failed", e);
            throw new DatabaseException("Failed to execute select", e);
        }
    }

    protected <T> List<T> selectAll(String query, List<Object> arguments, Function<ResultSet, T> resultMapper) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement select = connection.prepareStatement(query);
            setArguments(select, arguments);

            ResultSet results = select.executeQuery();
            List<T> resultList = new LinkedList<T>();
            while (results.next()) {
                resultList.add(resultMapper.apply(results));
            }
            return resultList;
        } catch (SQLException e) {
            logger.error("Select failed", e);
            throw new DatabaseException("Failed to execute select", e);
        }
    }

    protected long insert(String query, List<Object> arguments) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement insert = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setArguments(insert, arguments);

            int affectedRows = insert.executeUpdate();
            if (affectedRows != 1) {
                logger.error("Failed to insert row. Query: {}", query);
                throw new DatabaseException("Failed to execute insert");
            }

            ResultSet generatedKeys = insert.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                logger.error("Failed to insert row. Query: {}", query);
                throw new DatabaseException("Failed to get generated key after insert");
            }
        } catch (SQLException e) {
            logger.error("Insert failed", e);
            throw new DatabaseException("Failed to execute insert", e);
        }
    }

    protected void update(String query, List<Object> arguments) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement update = connection.prepareStatement(query);
            setArguments(update, arguments);

            int affectedRows = update.executeUpdate();
            if (affectedRows != 1) {
                logger.error("Failed to update row. Query: {}", query);
                throw new DatabaseException("Failed to execute update");
            }
        } catch (SQLException e) {
            logger.error("Update failed", e);
            throw new DatabaseException("Failed to execute update", e);
        }
    }

    private void setArguments(PreparedStatement statement, List<Object> arguments) throws SQLException {
        for (int i = 0; i < arguments.size(); i++) {
            statement.setObject(i+1, arguments.get(i));
        }
    }
}
