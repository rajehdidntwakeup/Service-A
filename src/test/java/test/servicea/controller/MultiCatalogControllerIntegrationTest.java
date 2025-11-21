package test.servicea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import test.servicea.domain.dto.ItemDto;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class MultiCatalogControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("getAll without multi-catalog param returns only locally created items")
  void getAll_singleCatalog_defaultParam() throws Exception {
    // Record initial size
    int initialSize = mockMvc.perform(get("/api/inventory"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsByteArray().length; // length is not item count; will not be used directly

    // Create two local items
    ItemDto item1 = new ItemDto("Local A", 3, 9.99, "A desc");
    ItemDto item2 = new ItemDto("Local B", 7, 19.99, "B desc");

    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item2)))
        .andExpect(status().isCreated());

    // Fetch all without multi-catalog (defaults to false)
    mockMvc.perform(get("/api/inventory"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", hasItems("Local A", "Local B")));
  }

  @Test
  @DisplayName("getAll with multi-catalog=true succeeds even if external services fail, returning local items")
  void getAll_multiCatalog_true_handlesExternalFailures() throws Exception {
    // Create one local item
    ItemDto local = new ItemDto("Local Only", 5, 5.55, "Local desc");
    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(local)))
        .andExpect(status().isCreated());

    // Call with multi-catalog=true; external URLs in application-test may be unreachable during tests
    // ItemServiceImpl is designed to swallow exceptions from external calls and continue
    mockMvc.perform(get("/api/inventory").param("multi-catalog", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(empty())))
        .andExpect(jsonPath("$[*].name", hasItem("Local Only")));
  }

  @Test
  @DisplayName("explicit multi-catalog=false behaves the same as default (no param)")
  void getAll_explicitFalse_equalsDefault() throws Exception {
    ItemDto item = new ItemDto("ExplicitFalse_Item", 2, 2.22, "explicit false case");
    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isCreated());

    String defaultResp = mockMvc.perform(get("/api/inventory"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", hasItem("ExplicitFalse_Item")))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String explicitFalseResp = mockMvc.perform(get("/api/inventory").param("multi-catalog", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", hasItem("ExplicitFalse_Item")))
        .andReturn()
        .getResponse()
        .getContentAsString();

    // Since test properties don't configure external.inventory entries,
    // multi-catalog=true/false should not change the payload relative to default=false.
    org.junit.jupiter.api.Assertions.assertEquals(defaultResp, explicitFalseResp);
  }

  @Test
  @DisplayName("invalid multi-catalog value (non-boolean) yields 400 Bad Request")
  void getAll_invalidMultiCatalogValue_returnsBadRequest() throws Exception {
    mockMvc.perform(get("/api/inventory").param("multi-catalog", "notABoolean"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("empty multi-catalog param defaults to false and returns 200")
  void getAll_emptyMultiCatalogValue_defaultsToFalse() throws Exception {
    // Create a local item to make the response non-empty and assert defaulting works
    ItemDto item = new ItemDto("EmptyParam_DefaultsFalse", 1, 1.11, "empty param");
    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isCreated());

    String defaultResp = mockMvc.perform(get("/api/inventory"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String emptyParamResp = mockMvc.perform(get("/api/inventory").param("multi-catalog", ""))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    org.junit.jupiter.api.Assertions.assertEquals(defaultResp, emptyParamResp);
  }

  @Test
  @DisplayName("multi-catalog accepts different boolean casings (TRUE)")
  void getAll_multiCatalog_true_uppercaseAccepted() throws Exception {
    ItemDto item = new ItemDto("UpperTrue_Item", 3, 3.33, "uppercase TRUE");
    mockMvc.perform(post("/api/inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/inventory").param("multi-catalog", "TRUE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", hasItem("UpperTrue_Item")));
  }
}
