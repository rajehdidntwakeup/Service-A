package test.servicea.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PictureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePicture() throws Exception {
        PictureDto pictureDto = new PictureDto("Test Picture", 10, 100.0, "Test Description");

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pictureDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Picture")))
                .andExpect(jsonPath("$.stock", is(10)))
                .andExpect(jsonPath("$.price", is(100.0)))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    public void testGetAllPictures() throws Exception {
        PictureDto pictureDto1 = new PictureDto("Picture 1", 5, 50.0, "Description 1");
        PictureDto pictureDto2 = new PictureDto("Picture 2", 15, 150.0, "Description 2");

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pictureDto1)));

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pictureDto2)));

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    public void testGetPictureById() throws Exception {
        PictureDto pictureDto = new PictureDto("Test Picture", 10, 100.0, "Test Description");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pictureDto)))
                .andReturn().getResponse().getContentAsString();

        Picture createdPicture = objectMapper.readValue(response, Picture.class);

        mockMvc.perform(get("/api/inventory/" + createdPicture.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdPicture.getId())));
    }

    @Test
    public void testUpdatePictureById() throws Exception {
        PictureDto pictureDto = new PictureDto("Old Picture", 5, 50.0, "Old Description");

        String response = mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pictureDto)))
                .andReturn().getResponse().getContentAsString();

        Picture createdPicture = objectMapper.readValue(response, Picture.class);

        PictureDto updatedDto = new PictureDto("Updated Picture", 20, 200.0, "Updated Description");

        mockMvc.perform(put("/api/inventory/" + createdPicture.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Picture")))
                .andExpect(jsonPath("$.stock", is(20)))
                .andExpect(jsonPath("$.price", is(200.0)))
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    public void testCreatePictureWithInvalidData() throws Exception {
        // Test with empty name
        PictureDto emptyNameDto = new PictureDto("", 10, 100.0, "Description");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDto)))
                .andExpect(status().isBadRequest());

        // Test with negative stock
        PictureDto negativeStockDto = new PictureDto("Picture", -10, 100.0, "Description");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativeStockDto)))
                .andExpect(status().isBadRequest());

        // Test with negative price
        PictureDto negativePriceDto = new PictureDto("Picture", 10, -100.0, "Description");
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativePriceDto)))
                .andExpect(status().isBadRequest());

        // Test with null description
        PictureDto nullDescriptionDto = new PictureDto("Picture", 10, 100.0, null);
        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullDescriptionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetPictureByInvalidId() throws Exception {
        // Test with non-existent ID
        mockMvc.perform(get("/api/inventory/9999"))
                .andExpect(status().isNotFound());

        // Test with negative ID
        mockMvc.perform(get("/api/inventory/fakeId"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePictureWithInvalidData() throws Exception {
        // Test updating non-existent picture
        PictureDto updatedDto = new PictureDto("Updated Picture", 20, 200.0, "Updated Description");
        mockMvc.perform(put("/api/inventory/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());

        // Test updating with invalid data
        PictureDto invalidDto = new PictureDto("", -10, -100.0, null);
        mockMvc.perform(put("/api/inventory/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
