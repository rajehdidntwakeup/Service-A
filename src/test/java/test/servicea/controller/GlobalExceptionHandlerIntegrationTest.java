package test.servicea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import test.servicea.domain.Item;
import test.servicea.domain.dto.ItemDto;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class GlobalExceptionHandlerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testValidationErrorStructureOnCreate_withMultipleErrors() throws Exception {
    // Arrange: invalid DTO that triggers all four field validations
    ItemDto invalid = new ItemDto("", -1, -5.0, "");

    // Act + Assert
    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Bad Request")))
        .andExpect(jsonPath("$.message", is("Validation failed")))
        .andExpect(jsonPath("$.errors.name", is("Name is mandatory.")))
        .andExpect(jsonPath("$.errors.stock", is("Stock cannot be negative.")))
        .andExpect(jsonPath("$.errors.price", is("Price cannot be negative.")))
        .andExpect(jsonPath("$.errors.description", is("Description is mandatory")));
  }

  @Test
  void testValidationErrorStructureOnUpdate_withMultipleErrors() throws Exception {
    // First create a valid item to update
    ItemDto valid = new ItemDto("Valid Name", 10, 20.0, "Valid Description");
    String createResponse = mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(valid)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    Item created = objectMapper.readValue(createResponse, Item.class);

    // Now attempt to update with invalid data (trigger all field validations)
    ItemDto invalid = new ItemDto("", -2, -10.0, "");

    mockMvc.perform(put("/api/inventory/" + created.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Bad Request")))
        .andExpect(jsonPath("$.message", is("Validation failed")))
        .andExpect(jsonPath("$.errors.name", is("Name is mandatory.")))
        .andExpect(jsonPath("$.errors.stock", is("Stock cannot be negative.")))
        .andExpect(jsonPath("$.errors.price", is("Price cannot be negative.")))
        .andExpect(jsonPath("$.errors.description", is("Description is mandatory")));
  }
}
