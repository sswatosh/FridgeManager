package dev.sswatosh.fridgemanager.controllers;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import dev.sswatosh.fridgemanager.domain.Fridge;
import dev.sswatosh.fridgemanager.domain.FridgeUpdateRequest;
import dev.sswatosh.fridgemanager.exceptions.ValidationException;
import dev.sswatosh.fridgemanager.metrics.Metrics;
import dev.sswatosh.fridgemanager.repository.FridgeDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FridgeControllerTest {

    @Mock
    private FridgeDAO fridgeDAO;

    @Mock
    private MetricRegistry metricRegistry;

    @InjectMocks
    private FridgeController fridgeController;

    @Test
    public void testGetFridges() {
        Fridge fridge1 = mock(Fridge.class);
        Fridge fridge2 = mock(Fridge.class);
        when(fridgeDAO.getAllFridges())
            .thenReturn(List.of(fridge1, fridge2));

        List<Fridge> allFridges = fridgeController.getFridges();

        assertThat(allFridges, hasItems(fridge1, fridge2));
    }

    @Test
    public void testAddFridge() {
        Counter mockCounter = mock(Counter.class);
        when(metricRegistry.counter(Metrics.FRIDGE_COUNT))
            .thenReturn(mockCounter);
        when(fridgeDAO.createFridge("fridgeName"))
            .thenReturn(100L);

        FridgeUpdateRequest request = new FridgeUpdateRequest();
        request.setName("fridgeName");
        Fridge result = fridgeController.addFridge(request);

        assertThat(result.getId(), equalTo(100L));
        assertThat(result.getName(), equalTo("fridgeName"));
        verify(mockCounter, times(1))
            .inc();
    }

    @Test
    public void testAddFridgeValidationFailure() {
        when(fridgeDAO.createFridge("fridgeName"))
            .thenThrow(new ValidationException("no name"));

        FridgeUpdateRequest request = new FridgeUpdateRequest();
        request.setName("fridgeName");
        assertThrows(
            ValidationException.class,
            () -> fridgeController.addFridge(request)
        );
    }
}
