package test.servicea.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto("Test Item", 10, 100.0, "Test Description");

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Service-A: Test Item")))
                .andExpect(jsonPath("$.stock", is(10)))
                .andExpect(jsonPath("$.price", is(100.0)))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    public void testGetAllItems() throws Exception {
        ItemDto itemDto1 = new ItemDto("Item 1", 5, 50.0, "Description 1");
        ItemDto itemDto2 = new ItemDto("Item 2", 15, 150.0, "Description 2");

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto1)));

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto2)));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    public void testGetItemById() throws Exception {
        ItemDto itemDto = new ItemDto("Test Item", 10, 100.0, "Test Description");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/inventory/" + createdItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdItem.getId())));
    }

    @Test
    public void testUpdateItemById() throws Exception {
        ItemDto itemDto = new ItemDto("Service-A: Old Item", 5, 50.0, "Old Description");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        ItemDto updatedDto = new ItemDto("Service-A: Updated Item", 20, 200.0, "Updated Description");

        mockMvc.perform(put("/api/inventory/" + createdItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Service-A: Updated Item")))
                .andExpect(jsonPath("$.stock", is(20)))
                .andExpect(jsonPath("$.price", is(200.0)))
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    public void testCreateItemWithInvalidData() throws Exception {
        // Test with an empty name
        ItemDto emptyNameDto = new ItemDto("", 10, 100.0, "Description");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDto)))
                .andExpect(status().isBadRequest());

        // Test with negative stock
        ItemDto negativeStockDto = new ItemDto("Item", -10, 100.0, "Description");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativeStockDto)))
                .andExpect(status().isBadRequest());

        // Test with a negative price
        ItemDto negativePriceDto = new ItemDto("Item", 10, -100.0, "Description");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativePriceDto)))
                .andExpect(status().isBadRequest());

        // Test with null description
        ItemDto nullDescriptionDto = new ItemDto("Item", 10, 100.0, null);
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullDescriptionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetItemByInvalidId() throws Exception {
        // Test with non-existent ID
        mockMvc.perform(get("/api/inventory/9999"))
                .andExpect(status().isNotFound());

        // Test with negative ID
        mockMvc.perform(get("/api/inventory/fakeId"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateItemWithInvalidData() throws Exception {
        // Test updating non-existent Item
        ItemDto updatedDto = new ItemDto("Updated Item", 20, 200.0, "Updated Description");
        mockMvc.perform(put("/api/inventory/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());

        // Test updating with invalid data
        ItemDto invalidDto = new ItemDto("", -10, -100.0, null);
        mockMvc.perform(put("/api/inventory/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetItemByIdAndName_Success() throws Exception {
        ItemDto itemDto = new ItemDto("Combo Item", 7, 77.7, "Combo Desc");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/inventory/" + createdItem.getId() + "/itemname/" + createdItem.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdItem.getId())))
                .andExpect(jsonPath("$.name", is(createdItem.getName())))
                .andExpect(jsonPath("$.stock", is(createdItem.getStock())))
                .andExpect(jsonPath("$.price", is(createdItem.getPrice())));
    }

    @Test
    public void testGetItemByIdAndName_NotFound_WrongName() throws Exception {
        ItemDto itemDto = new ItemDto("Exact Name", 3, 30.0, "Desc");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/inventory/" + createdItem.getId() + "/itemname/" + "Wrong Name"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetItemByIdAndName_BadRequest_InvalidId() throws Exception {
        mockMvc.perform(get("/api/inventory/fake/itemname/AnyName"))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testGetItemByIdAndName_SpacesAndEncoding_Success() throws Exception {
        ItemDto itemDto = new ItemDto("Item With Space", 5, 12.34, "Has spaces in name");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/inventory/{id}/itemname/{name}", createdItem.getId(), createdItem.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(createdItem.getId())))
            .andExpect(jsonPath("$.name", is(createdItem.getName())));
    }

    @Test
    public void testGetItemByIdAndName_UnicodeName_Success() throws Exception {
        String unicodeName = "Service-A: Café ☕️";
        ItemDto itemDto = new ItemDto(unicodeName, 2, 9.99, "Unicode name");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/inventory/{id}/itemname/{name}", createdItem.getId(), unicodeName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(createdItem.getId())))
            .andExpect(jsonPath("$.name", is(unicodeName)));
    }

    @Test
    public void testGetItemByIdAndName_CaseMismatch_NotFound() throws Exception {
        ItemDto itemDto = new ItemDto("Service-A: CaseSensitive", 1, 1.0, "case test");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);
        String wrongCase = createdItem.getName().toLowerCase();

        mockMvc.perform(get("/api/inventory/" + createdItem.getId() + "/itemname/" + wrongCase))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetItemByIdAndName_LeadingTrailingSpaces_NotFound() throws Exception {
        ItemDto itemDto = new ItemDto("Service-A: TrimTest", 4, 44.0, "trim test");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);
        String withSpaces = " " + createdItem.getName() + " ";

        mockMvc.perform(get("/api/inventory/" + createdItem.getId() + "/itemname/" + withSpaces))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetItemByIdAndName_ZeroId_NotFound() throws Exception {
        // Ensure there is at least one item with this name to avoid name-not-found masking id handling
        ItemDto itemDto = new ItemDto("ZeroIdName", 1, 1.0, "");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)));

        String encoded = java.net.URLEncoder.encode("ZeroIdName", java.nio.charset.StandardCharsets.UTF_8);
        mockMvc.perform(get("/api/inventory/0/itemname/" + encoded))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetItemByIdAndName_NegativeId_NotFound() throws Exception {
        ItemDto itemDto = new ItemDto("NegativeIdName", 1, 1.0, "");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)));

        String encoded = java.net.URLEncoder.encode("NegativeIdName", java.nio.charset.StandardCharsets.UTF_8);
        mockMvc.perform(get("/api/inventory/-1/itemname/" + encoded))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetItemByIdAndName_VeryLongName_Success() throws Exception {
        String longName = "Service-A: " + "A".repeat(241);
        ItemDto itemDto = new ItemDto(longName, 8, 88.0, "long name");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/inventory/" + createdItem.getId() + "/itemname/" + longName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(longName)));
    }

    @Test
    public void testUpdateItemByIdAndName_Success_UpdatesAndPersists() throws Exception {
        // Arrange: create an item
        ItemDto original = new ItemDto("Service-A: OriginalCtrl", 5, 50.0, "orig desc");
        String createdJson = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(original)))
            .andReturn().getResponse().getContentAsString();
        Item created = objectMapper.readValue(createdJson, Item.class);

        // Act: update via controller using id+name (also change the name)
        ItemDto update = new ItemDto("Service-A: RenamedCtrl", 7, 77.7, "updated");
        mockMvc.perform(put("/api/inventory/{id}/itemname/{name}", created.getId(), created.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(created.getId())))
            .andExpect(jsonPath("$.name", is("Service-A: RenamedCtrl")))
            .andExpect(jsonPath("$.stock", is(7)))
            .andExpect(jsonPath("$.price", is(77.7)))
            .andExpect(jsonPath("$.description", is("updated")));

        // Assert persistence by fetching by id
        mockMvc.perform(get("/api/inventory/{id}", created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Service-A: RenamedCtrl")))
            .andExpect(jsonPath("$.stock", is(7)))
            .andExpect(jsonPath("$.price", is(77.7)))
            .andExpect(jsonPath("$.description", is("updated")));

        // Old name should no longer match, new name should match
        mockMvc.perform(get("/api/inventory/{id}/itemname/{name}", created.getId(), "Service-A: OriginalCtrl"))
            .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/inventory/{id}/itemname/{name}", created.getId(), "Service-A: RenamedCtrl"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(created.getId())));
    }

    @Test
    public void testUpdateItemByIdAndName_NotFound_ReturnsNotFound_andUnchanged() throws Exception {
        // Non-existent combo
        ItemDto update = new ItemDto("X", 1, 1.0, "x");
        mockMvc.perform(put("/api/inventory/{id}/itemname/{name}", 999999, "Nope")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isNotFound());

        // Create a real item
        ItemDto dto = new ItemDto("RealCtrl", 2, 2.0, "r");
        String createdJson = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andReturn().getResponse().getContentAsString();
        Item created = objectMapper.readValue(createdJson, Item.class);

        // Use wrong name for existing id -> 404
        mockMvc.perform(put("/api/inventory/{id}/itemname/{name}", created.getId(), "Wrong")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isNotFound());

        // Ensure original item unchanged
        mockMvc.perform(get("/api/inventory/{id}", created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Service-A: RealCtrl")))
            .andExpect(jsonPath("$.stock", is(2)))
            .andExpect(jsonPath("$.price", is(2.0)))
            .andExpect(jsonPath("$.description", is("r")));
    }

    @Test
    public void testUpdateItemByIdAndName_InvalidBody_BadRequest_andUnchanged() throws Exception {
        // Arrange: create an item
        ItemDto dto = new ItemDto("KeepCtrl", 3, 30.0, "k");
        String createdJson = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andReturn().getResponse().getContentAsString();
        Item created = objectMapper.readValue(createdJson, Item.class);

        // Invalid body (violates @Valid constraints)
        ItemDto invalid = new ItemDto("", -1, -5.0, "");
        mockMvc.perform(put("/api/inventory/{id}/itemname/{name}", created.getId(), created.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.message", is("Validation failed")));

        // Verify DB unchanged
        mockMvc.perform(get("/api/inventory/{id}", created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Service-A: KeepCtrl")))
            .andExpect(jsonPath("$.stock", is(3)))
            .andExpect(jsonPath("$.price", is(30.0)))
            .andExpect(jsonPath("$.description", is("k")));
    }
}