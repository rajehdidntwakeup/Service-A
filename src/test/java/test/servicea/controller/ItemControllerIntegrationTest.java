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
                .andExpect(jsonPath("$.name", is("Test Item")))
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
        ItemDto itemDto = new ItemDto("Old Item", 5, 50.0, "Old Description");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andReturn().getResponse().getContentAsString();

        Item createdItem = objectMapper.readValue(response, Item.class);

        ItemDto updatedDto = new ItemDto("Updated Item", 20, 200.0, "Updated Description");

        mockMvc.perform(put("/api/inventory/" + createdItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Item")))
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
}
