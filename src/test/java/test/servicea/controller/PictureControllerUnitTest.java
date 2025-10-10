package test.servicea.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;
import test.servicea.service.PictureService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link test.servicea.controller.PictureController}.
 * <p>
 *    These tests instantiate the controller directly and mock the underlying
 *    PictureService to verify controller behavior (HTTP status codes and returned bodies).
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class PictureControllerUnitTest {

  @Mock
  private PictureService pictureService;

  private PictureController controller;

  @BeforeEach
  void setUp() {
    controller = new PictureController(pictureService);
  }

  @Test
  void createPicture_happyPath_returnsCreatedAndBody() {
    PictureDto dto = new PictureDto("Mona Lisa", 1, 100.0, "Famous painting");
    Picture created = new Picture("Mona Lisa", 1, 100.0, "Famous painting");

    when(pictureService.createPicture(dto)).thenReturn(created);

    ResponseEntity<Picture> response = controller.createPicture(dto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertSame(created, response.getBody());
    verify(pictureService, times(1)).createPicture(dto);
  }

  @Test
  void createPicture_serviceThrows_propagatesException() {
    PictureDto dto = new PictureDto("Mona Lisa", 1, 100.0, "Famous painting");
    when(pictureService.createPicture(dto)).thenThrow(new RuntimeException("DB down"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.createPicture(dto));
    assertEquals("DB down", ex.getMessage());
    verify(pictureService).createPicture(dto);
  }

  @Test
  void createPicture_nullInput_throwsNpe() {
    when(pictureService.createPicture(null)).thenThrow(new NullPointerException("dto is null"));

    NullPointerException ex = assertThrows(NullPointerException.class, () -> controller.createPicture(null));
    assertEquals("dto is null", ex.getMessage());
    verify(pictureService).createPicture(null);
  }

  @Test
  void getAllPictures_returnsList() {
    Picture p1 = new Picture("A", 2, 10.0, "a");
    Picture p2 = new Picture("B", 3, 20.0, "b");
    List<Picture> list = Arrays.asList(p1, p2);

    when(pictureService.getAllPictures()).thenReturn(list);

    ResponseEntity<List<Picture>> response = controller.getAllPictures();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals(list, response.getBody());
    verify(pictureService).getAllPictures();
  }

  @Test
  void getAllPictures_empty_returnsOkEmpty() {
    when(pictureService.getAllPictures()).thenReturn(Collections.emptyList());

    ResponseEntity<List<Picture>> response = controller.getAllPictures();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(pictureService).getAllPictures();
  }

  @Test
  void getPictureById_found_returnsOk() {
    Picture p = new Picture("Found", 1, 5.0, "found");
    when(pictureService.getPictureById(5)).thenReturn(p);

    ResponseEntity<Picture> response = controller.getPictureById(5);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(p, response.getBody());
    verify(pictureService).getPictureById(5);
  }

  @Test
  void getPictureById_notFound_returns404() {
    when(pictureService.getPictureById(7)).thenReturn(null);

    ResponseEntity<Picture> response = controller.getPictureById(7);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(pictureService).getPictureById(7);
  }

  @Test
  void updatePictureById_found_returnsOk() {
    PictureDto dto = new PictureDto("Updated", 4, 40.0, "updated");
    Picture updated = new Picture("Updated", 4, 40.0, "updated");
    when(pictureService.updatePictureById(9, dto)).thenReturn(updated);

    ResponseEntity<Picture> response = controller.updatePictureById(9, dto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(updated, response.getBody());
    verify(pictureService).updatePictureById(9, dto);
  }

  @Test
  void updatePictureById_notFound_returns404() {
    PictureDto dto = new PictureDto("Updated", 4, 40.0, "updated");
    when(pictureService.updatePictureById(99, dto)).thenReturn(null);

    ResponseEntity<Picture> response = controller.updatePictureById(99, dto);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(pictureService).updatePictureById(99, dto);
  }
}
