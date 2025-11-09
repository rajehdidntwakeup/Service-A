package test.servicea.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;
import test.servicea.service.ItemService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ItemController}.
 * <p>
 *    These tests instantiate the controller directly and mock the underlying
 *    ItemService to verify controller behavior (HTTP status codes and returned bodies).
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class ItemControllerUnitTest {

  @Mock
  private ItemService itemService;

  private ItemController controller;

  @BeforeEach
  void setUp() {
    controller = new ItemController(itemService);
  }

  @Test
  void createItem_happyPath_returnsCreatedAndBody() {
    ItemDto dto = new ItemDto("Mona Lisa", 1, 100.0, "Famous painting");
    Item created = new Item("Mona Lisa", 1, 100.0, "Famous painting");

    when(itemService.createItem(dto)).thenReturn(created);

    ResponseEntity<Item> response = controller.createItem(dto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertSame(created, response.getBody());
    verify(itemService, times(1)).createItem(dto);
  }

  @Test
  void createItem_serviceThrows_propagatesException() {
    ItemDto dto = new ItemDto("Mona Lisa", 1, 100.0, "Famous painting");
    when(itemService.createItem(dto)).thenThrow(new RuntimeException("DB down"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.createItem(dto));
    assertEquals("DB down", ex.getMessage());
    verify(itemService).createItem(dto);
  }

  @Test
  void createItem_nullInput_throwsNpe() {
    when(itemService.createItem(null)).thenThrow(new NullPointerException("dto is null"));

    NullPointerException ex = assertThrows(NullPointerException.class, () -> controller.createItem(null));
    assertEquals("dto is null", ex.getMessage());
    verify(itemService).createItem(null);
  }

  @Test
  void getAllItems_returnsList() {
    Item p1 = new Item("A", 2, 10.0, "a");
    Item p2 = new Item("B", 3, 20.0, "b");
    List<Item> list = Arrays.asList(p1, p2);

    when(itemService.getAllItems(false)).thenReturn(list);

    ResponseEntity<List<Item>> response = controller.getAll(false);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals(list, response.getBody());
    verify(itemService).getAllItems(false);
  }

  @Test
  void getAllItems_empty_returnsOkEmpty() {
    when(itemService.getAllItems(false)).thenReturn(Collections.emptyList());

    ResponseEntity<List<Item>> response = controller.getAll(false);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(itemService).getAllItems(false);
  }

  @Test
  void getItemById_found_returnsOk() {
    Item p = new Item("Found", 1, 5.0, "found");
    when(itemService.getItemById(5)).thenReturn(p);

    ResponseEntity<Item> response = controller.getItemById(5);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(p, response.getBody());
    verify(itemService).getItemById(5);
  }

  @Test
  void getItemById_notFound_returns404() {
    when(itemService.getItemById(7)).thenReturn(null);

    ResponseEntity<Item> response = controller.getItemById(7);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(itemService).getItemById(7);
  }

  @Test
  void updateItemById_found_returnsOk() {
    ItemDto dto = new ItemDto("Updated", 4, 40.0, "updated");
    Item updated = new Item("Updated", 4, 40.0, "updated");
    when(itemService.updateItemById(9, dto)).thenReturn(updated);

    ResponseEntity<Item> response = controller.updateItemById(9, dto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(updated, response.getBody());
    verify(itemService).updateItemById(9, dto);
  }

  @Test
  void updateItemById_notFound_returns404() {
    ItemDto dto = new ItemDto("Updated", 4, 40.0, "updated");
    when(itemService.updateItemById(99, dto)).thenReturn(null);

    ResponseEntity<Item> response = controller.updateItemById(99, dto);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(itemService).updateItemById(99, dto);
  }

  // ------------------ Tests for getItemByIdAndName ------------------

  @Test
  void getItemByIdAndName_found_returnsOk() {
    int id = 42;
    String name = "Combo Item";
    Item item = new Item(name, 7, 77.7, "desc");
    when(itemService.getItemByIdAndName(id, name)).thenReturn(item);

    ResponseEntity<Item> response = controller.getItemByIdAndName(id, name);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(item, response.getBody());
    verify(itemService, times(1)).getItemByIdAndName(id, name);
  }

  @Test
  void getItemByIdAndName_notFound_returns404() {
    int id = 5;
    String name = "Exact Name";
    when(itemService.getItemByIdAndName(id, name)).thenReturn(null);

    ResponseEntity<Item> response = controller.getItemByIdAndName(id, name);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(itemService).getItemByIdAndName(id, name);
  }

  @Test
  void getItemByIdAndName_serviceThrows_propagatesException() {
    int id = 1;
    String name = "AnyName";
    when(itemService.getItemByIdAndName(id, name)).thenThrow(new RuntimeException("Service failure"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getItemByIdAndName(id, name));
    assertEquals("Service failure", ex.getMessage());
    verify(itemService).getItemByIdAndName(id, name);
  }

  @Test
  void getItemByIdAndName_emptyName_forwardedAndReturns404() {
    int id = 10;
    String name = ""; // empty string should be forwarded as-is
    when(itemService.getItemByIdAndName(id, name)).thenReturn(null);

    ResponseEntity<Item> response = controller.getItemByIdAndName(id, name);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(itemService).getItemByIdAndName(id, name);
  }
}
