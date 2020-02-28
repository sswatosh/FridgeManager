package dev.sswatosh.fridgemanager.repository;

import dev.sswatosh.fridgemanager.domain.Item;
import dev.sswatosh.fridgemanager.domain.ItemType;
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
class ItemDAOTest {

    @Mock
    private JDBCHelper jdbcHelper;

    @InjectMocks
    private ItemDAO itemDAO;

    @Test
    public void testResultMapper() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id"))
            .thenReturn(123L);
        when(resultSet.getLong("fridge_id"))
            .thenReturn(234L);
        when(resultSet.getString("type"))
            .thenReturn("SODA");
        when(resultSet.getString("name"))
            .thenReturn("itemName");

        Item item = itemDAO.parseItemFromResultSet.apply(resultSet);

        assertThat(item.getId(), equalTo(123L));
        assertThat(item.getFridgeId(), equalTo(234L));
        assertThat(item.getType(), equalTo(ItemType.SODA));
        assertThat(item.getName(), equalTo("itemName"));
    }

    @Test
    public void testResultMapperSQLException() throws Exception {

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id"))
            .thenThrow(new SQLException());

        assertThrows(
            DatabaseException.class,
            () -> itemDAO.parseItemFromResultSet.apply(resultSet)
        );
    }
}
