package test.servicea.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.servicea.domain.Item;
import test.servicea.service.ItemService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Focused unit tests for ItemController#getAll endpoint, verifying behavior
 * with the multi-catalog flag in isolation from the web layer.
 */
@ExtendWith(MockitoExtension.class)
public class MultiCatalogControllerUnitTest {

  @Mock
  private ItemService itemService;

  private ItemController controller;

  @BeforeEach
  void setUp() {
    controller = new ItemController(itemService);
  }

  @Test
  void getAll_multiCatalogTrue_returnsList() {
    Item i1 = new Item("A1", 5, 11.0, "a1");
    Item i2 = new Item("B1", 6, 21.0, "b1");
    List<Item> list = Arrays.asList(i1, i2);

    when(itemService.getAllItems(true)).thenReturn(list);

    ResponseEntity<List<Item>> response = controller.getAll(true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals(list, response.getBody());
    verify(itemService).getAllItems(true);
  }

  @Test
  void getAll_multiCatalogFalse_returnsEmptyList() {
    when(itemService.getAllItems(false)).thenReturn(Collections.emptyList());

    ResponseEntity<List<Item>> response = controller.getAll(false);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(itemService).getAllItems(false);
  }

  @Test
  void getAll_multiCatalogTrue_serviceReturnsNull_returnsOkWithNullBody() {
    when(itemService.getAllItems(true)).thenReturn(null);

    ResponseEntity<List<Item>> response = controller.getAll(true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody());
    verify(itemService).getAllItems(true);
  }
  
  @Test
  void getAll_multiCatalogTrue_serviceThrows_propagatesException() {
    when(itemService.getAllItems(true)).thenThrow(new RuntimeException("boom"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getAll(true));
    assertEquals("boom", ex.getMessage());
    verify(itemService).getAllItems(true);
    verifyNoMoreInteractions(itemService);
  }

  @Test
  void getAll_multiCatalogFalse_serviceThrows_propagatesException() {
    when(itemService.getAllItems(false)).thenThrow(new IllegalStateException("db down"));

    IllegalStateException ex = assertThrows(IllegalStateException.class, () -> controller.getAll(false));
    assertEquals("db down", ex.getMessage());
    verify(itemService).getAllItems(false);
    verifyNoMoreInteractions(itemService);
  }

  @Test
  void getAll_serviceReturnsListContainingNulls_preservesNullsInBody() {
    List<Item> listWithNull = Arrays.asList(new Item("X", 1, 1.0, "x"), null);
    when(itemService.getAllItems(true)).thenReturn(listWithNull);

    ResponseEntity<List<Item>> response = controller.getAll(true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertSame(listWithNull, response.getBody());
    assertNull(response.getBody().get(1));
    verify(itemService).getAllItems(true);
    verifyNoMoreInteractions(itemService);
  }

  @Test
  void getAll_invokesServiceExactlyOnce_noOtherInteractions() {
    when(itemService.getAllItems(false)).thenReturn(Collections.emptyList());

    ResponseEntity<List<Item>> response = controller.getAll(false);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(itemService, times(1)).getAllItems(false);
    verifyNoMoreInteractions(itemService);
  }
  
  
}
