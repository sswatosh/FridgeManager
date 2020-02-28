package dev.sswatosh.fridgemanager.repository;

import dev.sswatosh.fridgemanager.domain.Fridge;
import dev.sswatosh.fridgemanager.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FridgeDAOTest {

    @Mock
    private JDBCHelper jdbcHelper;

    @InjectMocks
    private FridgeDAO fridgeDAO;

    @Test
    public void testResultMapper() throws Exception {
        ResultSet results = mock(ResultSet.class);
        when(results.getLong("id"))
            .thenReturn(123L);
        when(results.getString("name"))
            .thenReturn("Kitchen");

        Fridge result = fridgeDAO.parseFridgeFromResultSet.apply(results);

        assertThat(result.getId(), equalTo(123L));
        assertThat(result.getName(), equalTo("Kitchen"));
    }

    @Test
    public void testResultMapperError() throws Exception {
        ResultSet results = mock(ResultSet.class);
        when(results.getLong("id"))
            .thenThrow(new SQLException());

        assertThrows(
            DatabaseException.class,
            () -> fridgeDAO.parseFridgeFromResultSet.apply(results)
        );
    }
}
