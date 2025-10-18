package test.servicea.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class PictureServiceIntegrationTest {

    @Autowired
    private PictureService pictureService;

    @Test
    public void testCreatePicture() {
        PictureDto pictureDto = new PictureDto("Test Picture", 10, 100.0, "Test Description");
        Picture createdPicture = pictureService.createPicture(pictureDto);

        assertNotNull(createdPicture);
        assertEquals("Test Picture", createdPicture.getName());
        assertEquals(10, createdPicture.getStock());
        assertEquals(100.0, createdPicture.getPrice());
        assertEquals("Test Description", createdPicture.getDescription());
    }

    @Test
    public void testGetAllPictures() {
        PictureDto pictureDto1 = new PictureDto("Picture 1", 5, 50.0, "Description 1");
        PictureDto pictureDto2 = new PictureDto("Picture 2", 15, 150.0, "Description 2");
        pictureService.createPicture(pictureDto1);
        pictureService.createPicture(pictureDto2);

        List<Picture> pictures = pictureService.getAllPictures(false);

        assertEquals(2, pictures.size());
    }

    @Test
    public void testGetPictureById() {
        PictureDto pictureDto = new PictureDto("Test Picture", 10, 100.0, "Test Description");
        Picture createdPicture = pictureService.createPicture(pictureDto);

        Picture fetchedPicture = pictureService.getPictureById(createdPicture.getId());

        assertNotNull(fetchedPicture);
        assertEquals(createdPicture.getId(), fetchedPicture.getId());
    }

    @Test
    public void testUpdatePictureById() {
        PictureDto pictureDto = new PictureDto("Old Picture", 5, 50.0, "Old Description");
        Picture createdPicture = pictureService.createPicture(pictureDto);

        PictureDto updatedDto = new PictureDto("Updated Picture", 20, 200.0, "Updated Description");
        Picture updatedPicture = pictureService.updatePictureById(createdPicture.getId(), updatedDto);

        assertNotNull(updatedPicture);
        assertEquals("Updated Picture", updatedPicture.getName());
        assertEquals(20, updatedPicture.getStock());
        assertEquals(200.0, updatedPicture.getPrice());
        assertEquals("Updated Description", updatedPicture.getDescription());
    }

    @Test
    public void testGetPictureByInvalidId() {
        Picture picture = pictureService.getPictureById(-1);
        assertNull(picture);
    }

    @Test
    public void testUpdateNonExistentPicture() {
        PictureDto updatedDto = new PictureDto("Updated Picture", 20, 200.0, "Updated Description");
        Picture updatedPicture = pictureService.updatePictureById(9999, updatedDto);
        assertNull(updatedPicture);
    }

    @Test
    public void testGetAllPicturesWhenEmpty() {
        List<Picture> pictures = pictureService.getAllPictures(false);
        assertTrue(pictures.isEmpty());
    }
}
