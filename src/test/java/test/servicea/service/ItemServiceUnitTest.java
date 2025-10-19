package test.servicea.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;
import test.servicea.repository.ItemRepository;
import test.servicea.service.impl.ItemServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemServiceImpl itemService;

  @Test
  void createItem_callsSaveAndReturnsItem() {
    ItemDto dto = new ItemDto("Name", 2, 15.5, "desc");
    Item saved = new Item("Name", 2, 15.5, "desc");
    when(itemRepository.save(any(Item.class))).thenReturn(saved);

    Item result = itemService.createItem(dto);

    assertNotNull(result);
    assertEquals("Name", result.getName());
    assertEquals(2, result.getStock());
    assertEquals(15.5, result.getPrice());
    verify(itemRepository, times(1)).save(any(Item.class));
  }

  @Test
  void getAllItems_returnsList() {
    Item p1 = new Item("A", 1, 10.0, "a");
    Item p2 = new Item("B", 2, 20.0, "b");
    when(itemRepository.findAll()).thenReturn(List.of(p1, p2));

    List<Item> all = itemService.getAllItems(false);

    assertNotNull(all);
    assertEquals(2, all.size());
    verify(itemRepository, times(1)).findAll();
  }

  @Test
  void getAllItems_empty_returnsEmptyList() {
    when(itemRepository.findAll()).thenReturn(List.of());

    List<Item> all = itemService.getAllItems(false);

    assertNotNull(all);
    assertTrue(all.isEmpty());
  }

  @Test
  void getItemById_found_returnsItem() {
    Item p = new Item("Found", 1, 5.0, "f");
    when(itemRepository.findById(1)).thenReturn(Optional.of(p));

    Item result = itemService.getItemById(1);

    assertNotNull(result);
    assertEquals("Found", result.getName());
    verify(itemRepository, times(1)).findById(1);
  }

  @Test
  void getItemById_notFound_returnsNull() {
    when(itemRepository.findById(999)).thenReturn(Optional.empty());

    Item result = itemService.getItemById(999);

    assertNull(result);
  }

  @Test
  void updateItemById_updatesFieldsWhenPresent() {
    Item existing = new Item("Old", 1, 10.0, "old");
    when(itemRepository.findById(10)).thenReturn(Optional.of(existing));
    when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ItemDto update = new ItemDto("New", 3, 30.0, "new desc");
    Item result = itemService.updateItemById(10, update);

    assertNotNull(result);
    assertEquals("New", result.getName());
    assertEquals(3, result.getStock());
    assertEquals(30.0, result.getPrice());
    assertEquals("new desc", result.getDescription());
    verify(itemRepository, times(1)).findById(10);
    verify(itemRepository, times(1)).save(existing);
  }

  @Test
  void updateItemById_whenNotFound_returnsNull() {
    when(itemRepository.findById(12345)).thenReturn(Optional.empty());

    ItemDto update = new ItemDto("X", 0, 0.0, "x");
    Item result = itemService.updateItemById(12345, update);

    assertNull(result);
    verify(itemRepository, times(1)).findById(12345);
    verify(itemRepository, times(0)).save(any());
  }

  @Test
  void createItem_null_throwsNullPointerException() {
    assertThrows(ResponseStatusException.class, () -> itemService.createItem(null));
  }

  @Test
  void createItem_zeroValues_savesSuccessfully() {
    ItemDto dto = new ItemDto("Zero", 0, 0.0, "zero");
    Item saved = new Item("Zero", 0, 0.0, "zero");
    when(itemRepository.save(any(Item.class))).thenReturn(saved);

    Item result = itemService.createItem(dto);

    assertNotNull(result);
    assertEquals(0, result.getStock());
    assertEquals(0.0, result.getPrice());
    verify(itemRepository).save(any(Item.class));
  }

  @Test
  void createItem_negativeValues_savesAndReturnsNegativeValues() {
    ItemDto dto = new ItemDto("Neg", -5, -99.9, "neg");
    Item saved = new Item("Neg", -5, -99.9, "neg");
    when(itemRepository.save(any(Item.class))).thenReturn(saved);

    Item result = itemService.createItem(dto);

    assertNotNull(result);
    assertEquals(-5, result.getStock());
    assertEquals(-99.9, result.getPrice());
    verify(itemRepository).save(any(Item.class));
  }

  @Test
  void updateItemById_nullDto_throwsNullPointerException() {
    Item existing = new Item("Existing", 1, 1.0, "ex");
    when(itemRepository.findById(2)).thenReturn(Optional.of(existing));

    assertThrows(NullPointerException.class, () -> itemService.updateItemById(2, null));
    verify(itemRepository).findById(2);
  }

  @Test
  void getAllItems_repositoryThrows_runtimePropagates() {
    when(itemRepository.findAll()).thenThrow(new RuntimeException("db error"));
    assertThrows(RuntimeException.class, () -> itemService.getAllItems(false));
    verify(itemRepository).findAll();
  }

  @Test
  void createItem_whenRepositorySaveThrows_exceptionPropagates() {
    ItemDto dto = new ItemDto("WillFail", 1, 1.0, "fail");
    when(itemRepository.save(any(Item.class))).thenThrow(new RuntimeException("save failed"));

    assertThrows(RuntimeException.class, () -> itemService.createItem(dto));
    verify(itemRepository).save(any(Item.class));
  }

}
