package dev.sswatosh.fridgemanager.repository;

import dev.sswatosh.fridgemanager.exceptions.DatabaseException;
import dev.sswatosh.fridgemanager.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JDBCHelperTest {
    private static final String TEST_QUERY = "QUERY";
    private static final long TEST_ID = 1234L;
    private static final String TEST_MAPPER_RESULT = "result";
    private static final Function<ResultSet, String> TEST_RESULT_MAPPER = resultSet -> TEST_MAPPER_RESULT;

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private JDBCHelper jdbcHelper;

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResults;

    @BeforeEach
    public void setup() throws Exception {
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection())
            .thenReturn(connection);
        this.mockConnection = connection;

        this.mockStatement = mock(PreparedStatement.class);

        this.mockResults = mock(ResultSet.class);
    }

    @Test
    public void testSelectById() throws Exception {
        mockBasicStatement();
        mockResultSet();
        when(mockResults.next())
            .thenReturn(true);

        String result = jdbcHelper.selectById(TEST_QUERY, TEST_ID, TEST_RESULT_MAPPER);

        checkStatementQuery(TEST_QUERY);
        verify(mockStatement, times(1))
            .setLong(1,TEST_ID);
        assertThat(result, equalTo(TEST_MAPPER_RESULT));
    }

    @Test
    public void testSelectByIdNoResult() throws Exception {
        mockBasicStatement();
        mockResultSet();
        when(mockResults.next())
            .thenReturn(false);

        assertThrows(
            NotFoundException.class,
            () -> jdbcHelper.selectById(TEST_QUERY, TEST_ID, TEST_RESULT_MAPPER)
        );
    }

    @Test
    public void testSelectByIdSQLException() throws Exception {
        mockBasicStatement();
        when(mockStatement.executeQuery())
            .thenThrow(new SQLException("error"));

        assertThrows(
            DatabaseException.class,
            () -> jdbcHelper.selectById(TEST_QUERY, TEST_ID, TEST_RESULT_MAPPER)
        );
    }

    @Test
    public void testSelectAll() throws Exception {
        mockBasicStatement();
        mockResultSet();
        when(mockResults.next())
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false);

        List<String> result = jdbcHelper.selectAll(TEST_QUERY, TEST_RESULT_MAPPER);

        checkStatementQuery(TEST_QUERY);
        assertThat(result, equalTo(List.of(TEST_MAPPER_RESULT, TEST_MAPPER_RESULT)));
    }

    @Test
    public void testSelectAllNoResults() throws Exception {
        mockBasicStatement();
        mockResultSet();
        when(mockResults.next())
            .thenReturn(false);

        List<String> result = jdbcHelper.selectAll(TEST_QUERY, TEST_RESULT_MAPPER);

        checkStatementQuery(TEST_QUERY);
        assertThat(result, equalTo(List.of()));
    }

    @Test
    public void testSelectAllSQLException() throws Exception {
        mockBasicStatement();
        when(mockStatement.executeQuery())
            .thenThrow(new SQLException("error"));

        assertThrows(
            DatabaseException.class,
            () -> jdbcHelper.selectAll(TEST_QUERY, TEST_RESULT_MAPPER)
        );
    }

    @Test
    public void testInsert() throws Exception {
        mockInsertStatement();
        when(mockStatement.executeUpdate())
            .thenReturn(1);
        mockGoodGeneratedKey();

        Long result = jdbcHelper.insert(TEST_QUERY, List.of(22, "bb"));

        verify(mockStatement, times(1))
            .setObject(1, 22);
        verify(mockStatement, times(1))
            .setObject(2, "bb");
        assertThat(result, equalTo(TEST_ID));
    }

    @Test
    public void testInsertNoAffectedRows() throws Exception {
        mockInsertStatement();
        when(mockStatement.executeUpdate())
            .thenReturn(0);

        assertThrows(
            DatabaseException.class,
            () -> jdbcHelper.insert(TEST_QUERY, List.of())
        );
    }

    @Test
    public void testInsertNoGeneratedKey() throws Exception {
        mockInsertStatement();
        when(mockStatement.executeUpdate())
            .thenReturn(1);
        mockNoGeneratedKey();

        assertThrows(
            DatabaseException.class,
            () -> jdbcHelper.insert(TEST_QUERY, List.of())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        mockBasicStatement();
        when(mockStatement.executeUpdate())
            .thenReturn(1);

        jdbcHelper.update(TEST_QUERY, List.of(22, "bb"), TEST_ID);

        verify(mockStatement, times(1))
            .setObject(1, 22);
        verify(mockStatement, times(1))
            .setObject(2, "bb");
        verify(mockStatement, times(1))
            .setLong(3, TEST_ID);
    }

    @Test
    public void testUpdateNoRowsAffected() throws Exception {
        mockBasicStatement();
        when(mockStatement.executeUpdate())
            .thenThrow(new SQLException());

        assertThrows(
            DatabaseException.class,
            () -> jdbcHelper.update(TEST_QUERY, List.of(), TEST_ID)
        );
    }

    private void mockBasicStatement() throws Exception {
        when(mockConnection.prepareStatement(anyString()))
            .thenReturn(mockStatement);
    }

    private void mockResultSet() throws Exception {
        when(mockStatement.executeQuery())
            .thenReturn(mockResults);
    }

    private void mockInsertStatement() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(mockStatement);
    }

    private void mockGoodGeneratedKey() throws SQLException {
        when(mockStatement.getGeneratedKeys())
            .thenReturn(mockResults);
        when(mockResults.next())
            .thenReturn(true);
        when(mockResults.getLong(1))
            .thenReturn(TEST_ID);
    }

    private void mockNoGeneratedKey() throws SQLException {
        when(mockStatement.getGeneratedKeys())
            .thenReturn(mockResults);
        when(mockResults.next())
            .thenReturn(false);
    }

    private void checkStatementQuery(String expectedQuery) throws Exception {
        verify(mockConnection, times(1))
            .prepareStatement(expectedQuery);
    }

    private void checkInsertStatementQuery(String expectedQuery) throws Exception {
        verify(mockConnection, times(1))
            .prepareStatement(expectedQuery, Statement.RETURN_GENERATED_KEYS);
    }
}