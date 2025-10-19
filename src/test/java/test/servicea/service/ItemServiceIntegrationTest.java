package test.servicea.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Test
    public void testCreateItem() {
        ItemDto itemDto = new ItemDto("Test Item", 10, 100.0, "Test Description");
        Item createdItem = itemService.createItem(itemDto);

        assertNotNull(createdItem);
        assertEquals("Test Item", createdItem.getName());
        assertEquals(10, createdItem.getStock());
        assertEquals(100.0, createdItem.getPrice());
        assertEquals("Test Description", createdItem.getDescription());
    }

    @Test
    public void testGetAllItems() {
        ItemDto itemDto1 = new ItemDto("Item 1", 5, 50.0, "Description 1");
        ItemDto itemDto2 = new ItemDto("Item 2", 15, 150.0, "Description 2");
        itemService.createItem(itemDto1);
        itemService.createItem(itemDto2);

        List<Item> items = itemService.getAllItems(false);

        assertEquals(2, items.size());
    }

    @Test
    public void testGetItemById() {
        ItemDto itemDto = new ItemDto("Test Item", 10, 100.0, "Test Description");
        Item createdItem = itemService.createItem(itemDto);

        Item fetchedItem = itemService.getItemById(createdItem.getId());

        assertNotNull(fetchedItem);
        assertEquals(createdItem.getId(), fetchedItem.getId());
    }

    @Test
    public void testUpdateItemById() {
        ItemDto itemDto = new ItemDto("Old Item", 5, 50.0, "Old Description");
        Item createdItem = itemService.createItem(itemDto);

        ItemDto updatedDto = new ItemDto("Updated Item", 20, 200.0, "Updated Description");
        Item updatedItem = itemService.updateItemById(createdItem.getId(), updatedDto);

        assertNotNull(updatedItem);
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals(20, updatedItem.getStock());
        assertEquals(200.0, updatedItem.getPrice());
        assertEquals("Updated Description", updatedItem.getDescription());
    }

    @Test
    public void testGetItemByInvalidId() {
        Item item = itemService.getItemById(-1);
        assertNull(item);
    }

    @Test
    public void testUpdateNonExistentItem() {
        ItemDto updatedDto = new ItemDto("Updated Item", 20, 200.0, "Updated Description");
        Item updatedItem = itemService.updateItemById(9999, updatedDto);
        assertNull(updatedItem);
    }

    @Test
    public void testGetAllItemsWhenEmpty() {
        List<Item> items = itemService.getAllItems(false);
        assertTrue(items.isEmpty());
    }
}
