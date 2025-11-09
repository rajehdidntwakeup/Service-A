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

    @Test
    public void testGetItemByIdAndName_Found() {
        ItemDto itemDto = new ItemDto("Special Item", 7, 77.7, "Special Desc");
        Item createdItem = itemService.createItem(itemDto);

        Item fetched = itemService.getItemByIdAndName(createdItem.getId(), "Special Item");

        assertNotNull(fetched);
        assertEquals(createdItem.getId(), fetched.getId());
        assertEquals("Special Item", fetched.getName());
        assertEquals(7, fetched.getStock());
        assertEquals(77.7, fetched.getPrice());
        assertEquals("Special Desc", fetched.getDescription());
    }

    @Test
    public void testGetItemByIdAndName_NotFoundByName() {
        ItemDto itemDto = new ItemDto("Correct Name", 3, 33.3, "Desc");
        Item createdItem = itemService.createItem(itemDto);

        Item fetched = itemService.getItemByIdAndName(createdItem.getId(), "Wrong Name");

        assertNull(fetched);
    }

    @Test
    public void testGetItemByIdAndName_NotFoundById() {
        ItemDto itemDto = new ItemDto("Existing Name", 1, 10.0, "D");
        Item created = itemService.createItem(itemDto);

        Item fetched = itemService.getItemByIdAndName(999999, created.getName());

        assertNull(fetched);
    }

    @Test
    public void testGetItemByIdAndName_MismatchedIdExistingName() {
        ItemDto dtoA = new ItemDto("Alpha", 2, 20.0, "A");
        ItemDto dtoB = new ItemDto("Beta", 3, 30.0, "B");
        Item itemA = itemService.createItem(dtoA);
        Item itemB = itemService.createItem(dtoB);

        // Use A's id with B's name -> should not match
        Item fetched = itemService.getItemByIdAndName(itemA.getId(), itemB.getName());

        assertNull(fetched);
    }

    @Test
    public void testGetItemByIdAndName_EmptyNameProvided() {
        ItemDto itemDto = new ItemDto("NonEmpty", 1, 10.0, "D");
        Item created = itemService.createItem(itemDto);

        Item fetched = itemService.getItemByIdAndName(created.getId(), "");

        assertNull(fetched);
    }

    @Test
    public void testGetItemByIdAndName_WhitespaceNotTrimmed() {
        ItemDto itemDto = new ItemDto("TrimMe", 1, 10.0, "D");
        Item created = itemService.createItem(itemDto);

        Item fetched = itemService.getItemByIdAndName(created.getId(), " TrimMe ");

        assertNull(fetched);
    }

    @Test
    public void testGetItemByIdAndName_CaseSensitivity() {
        ItemDto itemDto = new ItemDto("Case Name", 1, 10.0, "D");
        Item created = itemService.createItem(itemDto);

        Item fetchedMismatch = itemService.getItemByIdAndName(created.getId(), "case name");
        assertNull(fetchedMismatch);

        Item fetchedExact = itemService.getItemByIdAndName(created.getId(), "Case Name");
        assertNotNull(fetchedExact);
        assertEquals(created.getId(), fetchedExact.getId());
    }

    @Test
    public void testGetItemByIdAndName_SpecialCharactersAndUnicode() {
        String special = "Café ☕ #1";
        ItemDto itemDto = new ItemDto(special, 2, 12.5, "Unicode");
        Item created = itemService.createItem(itemDto);

        Item fetchedExact = itemService.getItemByIdAndName(created.getId(), special);
        assertNotNull(fetchedExact);
        assertEquals(special, fetchedExact.getName());

        Item fetchedDifferent = itemService.getItemByIdAndName(created.getId(), "Cafe #1");
        assertNull(fetchedDifferent);
    }

    @Test
    public void testGetItemByIdAndName_LargeNonExistentId() {
        ItemDto itemDto = new ItemDto("LargeID", 1, 10.0, "D");
        Item created = itemService.createItem(itemDto);

        Item fetched = itemService.getItemByIdAndName(Integer.MAX_VALUE, created.getName());
        assertNull(fetched);
    }
}
