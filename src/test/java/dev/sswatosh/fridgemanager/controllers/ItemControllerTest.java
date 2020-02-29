package dev.sswatosh.fridgemanager.controllers;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import dev.sswatosh.fridgemanager.domain.Item;
import dev.sswatosh.fridgemanager.domain.ItemType;
import dev.sswatosh.fridgemanager.domain.ItemUpdateRequest;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import dev.sswatosh.fridgemanager.metrics.Metrics;
import dev.sswatosh.fridgemanager.repository.ItemDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private static final long TEST_ITEM_ID = 5;
    private static final long TEST_FRIDGE_ID = 10;
    private static final String TEST_ITEM_NAME = "New Coke";

    @Mock
    private ItemDAO itemDAO;

    @Mock
    private MetricRegistry metricRegistry;

    @InjectMocks
    private ItemController itemController;

    @Test
    public void testGetItems() {
        Item item1 = mock(Item.class);
        Item item2 = mock(Item.class);
        when(itemDAO.getAllItemsInFridge(TEST_FRIDGE_ID))
            .thenReturn(List.of(item1, item2));

        List<Item> result = itemController.getItems(TEST_FRIDGE_ID);

        assertThat(result, equalTo(List.of(item1, item2)));
    }

    @Test
    public void testGetItem() {
        Item item1 = mock(Item.class);
        when(itemDAO.getItemById(TEST_ITEM_ID, TEST_FRIDGE_ID))
            .thenReturn(item1);

        Item result = itemController.getItem(TEST_ITEM_ID, TEST_FRIDGE_ID);

        assertThat(result, equalTo(item1));
    }

    @Test
    public void testAddItem() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setType(ItemType.SODA);
        request.setName(TEST_ITEM_NAME);
        mockNoSodas();
        when(itemDAO.createItem(TEST_FRIDGE_ID, ItemType.SODA, TEST_ITEM_NAME))
            .thenReturn(TEST_ITEM_ID);
        Counter mockCounter = mock(Counter.class);
        when(metricRegistry.counter(Metrics.ITEM_COUNT))
            .thenReturn(mockCounter);

        Item result = itemController.addItem(TEST_FRIDGE_ID, request);

        assertThat(result.getId(), equalTo(TEST_ITEM_ID));
        assertThat(result.getFridgeId(), equalTo(TEST_FRIDGE_ID));
        assertThat(result.getType(), equalTo(ItemType.SODA));
        assertThat(result.getName(), equalTo(TEST_ITEM_NAME));
        verify(mockCounter, times(1))
            .inc();
    }

    @Test
    public void testAddItemNoType() {
        ItemUpdateRequest request = new ItemUpdateRequest();

        assertThrows(
            ValidationException.class,
            () -> itemController.addItem(TEST_FRIDGE_ID, request)
        );
    }

    @Test
    public void testAddItemNoName() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setType(ItemType.EGGS);

        assertThrows(
            ValidationException.class,
            () -> itemController.addItem(TEST_FRIDGE_ID, request)
        );
    }

    @Test
    public void testAddItemTooManySodas() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setType(ItemType.SODA);
        request.setName(TEST_ITEM_NAME);
        when(itemDAO.getGetSodaCountInFridge(TEST_FRIDGE_ID))
            .thenReturn(ItemController.MAX_SODAS_PER_FRIDGE);

        assertThrows(
            ValidationException.class,
            () -> itemController.addItem(TEST_FRIDGE_ID, request)
        );
    }

    @Test
    public void testUpdateItem() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setType(ItemType.SODA);
        request.setName(TEST_ITEM_NAME);
        mockNoSodas();
        when(itemDAO.getItemById(TEST_ITEM_ID, TEST_FRIDGE_ID))
            .thenReturn(new Item(TEST_ITEM_ID, TEST_FRIDGE_ID, ItemType.MEAT, "Steak"));

        itemController.updateItem(TEST_ITEM_ID, TEST_FRIDGE_ID, request);

        verify(itemDAO, times(1))
            .updateItem(TEST_ITEM_ID, TEST_FRIDGE_ID, ItemType.SODA, TEST_ITEM_NAME);
    }

    @Test
    public void testUpdateItemNoChanges() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        when(itemDAO.getItemById(TEST_ITEM_ID, TEST_FRIDGE_ID))
            .thenReturn(new Item(TEST_ITEM_ID, TEST_FRIDGE_ID, ItemType.MEAT, "Steak"));

        itemController.updateItem(TEST_ITEM_ID, TEST_FRIDGE_ID, request);

        verify(itemDAO, times(1))
            .updateItem(TEST_ITEM_ID, TEST_FRIDGE_ID, ItemType.MEAT, "Steak");
    }

    @Test
    public void testUpdateItemTooManySodas() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setType(ItemType.SODA);
        request.setName(TEST_ITEM_NAME);
        when(itemDAO.getItemById(TEST_ITEM_ID, TEST_FRIDGE_ID))
            .thenReturn(new Item(TEST_ITEM_ID, TEST_FRIDGE_ID, ItemType.MEAT, "Steak"));
        when(itemDAO.getGetSodaCountInFridge(TEST_FRIDGE_ID))
            .thenReturn(ItemController.MAX_SODAS_PER_FRIDGE);

        assertThrows(
            ValidationException.class,
            () -> itemController.updateItem(TEST_ITEM_ID, TEST_FRIDGE_ID, request)
        );
    }

    @Test
    public void testDeleteItem() {
        Counter mockCounter = mock(Counter.class);
        when(metricRegistry.counter(Metrics.ITEM_COUNT))
            .thenReturn(mockCounter);

        itemController.deleteItem(TEST_ITEM_ID, TEST_FRIDGE_ID);

        verify(itemDAO, times(1))
            .deleteItem(TEST_ITEM_ID, TEST_FRIDGE_ID);
        verify(mockCounter, times(1))
            .dec();
    }

    private void mockNoSodas() {
        when(itemDAO.getGetSodaCountInFridge(anyLong()))
            .thenReturn(0);
    }
}