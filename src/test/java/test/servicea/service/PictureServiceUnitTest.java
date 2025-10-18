package test.servicea.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;
import test.servicea.repository.PictureRepository;
import test.servicea.service.impl.PictureServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PictureServiceUnitTest {

  @Mock
  private PictureRepository pictureRepository;

  @InjectMocks
  private PictureServiceImpl pictureService;

  @Test
  void createPicture_callsSaveAndReturnsPicture() {
    PictureDto dto = new PictureDto("Name", 2, 15.5, "desc");
    Picture saved = new Picture("Name", 2, 15.5, "desc");
    when(pictureRepository.save(any(Picture.class))).thenReturn(saved);

    Picture result = pictureService.createPicture(dto);

    assertNotNull(result);
    assertEquals("Name", result.getName());
    assertEquals(2, result.getStock());
    assertEquals(15.5, result.getPrice());
    verify(pictureRepository, times(1)).save(any(Picture.class));
  }

  @Test
  void getAllPictures_returnsList() {
    Picture p1 = new Picture("A", 1, 10.0, "a");
    Picture p2 = new Picture("B", 2, 20.0, "b");
    when(pictureRepository.findAll()).thenReturn(List.of(p1, p2));

    List<Picture> all = pictureService.getAllPictures(false);

    assertNotNull(all);
    assertEquals(2, all.size());
    verify(pictureRepository, times(1)).findAll();
  }

  @Test
  void getAllPictures_empty_returnsEmptyList() {
    when(pictureRepository.findAll()).thenReturn(List.of());

    List<Picture> all = pictureService.getAllPictures(false);

    assertNotNull(all);
    assertTrue(all.isEmpty());
  }

  @Test
  void getPictureById_found_returnsPicture() {
    Picture p = new Picture("Found", 1, 5.0, "f");
    when(pictureRepository.findById(1)).thenReturn(Optional.of(p));

    Picture result = pictureService.getPictureById(1);

    assertNotNull(result);
    assertEquals("Found", result.getName());
    verify(pictureRepository, times(1)).findById(1);
  }

  @Test
  void getPictureById_notFound_returnsNull() {
    when(pictureRepository.findById(999)).thenReturn(Optional.empty());

    Picture result = pictureService.getPictureById(999);

    assertNull(result);
  }

  @Test
  void updatePictureById_updatesFieldsWhenPresent() {
    Picture existing = new Picture("Old", 1, 10.0, "old");
    when(pictureRepository.findById(10)).thenReturn(Optional.of(existing));
    when(pictureRepository.save(any(Picture.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PictureDto update = new PictureDto("New", 3, 30.0, "new desc");
    Picture result = pictureService.updatePictureById(10, update);

    assertNotNull(result);
    assertEquals("New", result.getName());
    assertEquals(3, result.getStock());
    assertEquals(30.0, result.getPrice());
    assertEquals("new desc", result.getDescription());
    verify(pictureRepository, times(1)).findById(10);
    verify(pictureRepository, times(1)).save(existing);
  }

  @Test
  void updatePictureById_whenNotFound_returnsNull() {
    when(pictureRepository.findById(12345)).thenReturn(Optional.empty());

    PictureDto update = new PictureDto("X", 0, 0.0, "x");
    Picture result = pictureService.updatePictureById(12345, update);

    assertNull(result);
    verify(pictureRepository, times(1)).findById(12345);
    verify(pictureRepository, times(0)).save(any());
  }

  @Test
  void createPicture_null_throwsNullPointerException() {
    assertThrows(ResponseStatusException.class, () -> pictureService.createPicture(null));
  }

  @Test
  void createPicture_zeroValues_savesSuccessfully() {
    PictureDto dto = new PictureDto("Zero", 0, 0.0, "zero");
    Picture saved = new Picture("Zero", 0, 0.0, "zero");
    when(pictureRepository.save(any(Picture.class))).thenReturn(saved);

    Picture result = pictureService.createPicture(dto);

    assertNotNull(result);
    assertEquals(0, result.getStock());
    assertEquals(0.0, result.getPrice());
    verify(pictureRepository).save(any(Picture.class));
  }

  @Test
  void createPicture_negativeValues_savesAndReturnsNegativeValues() {
    PictureDto dto = new PictureDto("Neg", -5, -99.9, "neg");
    Picture saved = new Picture("Neg", -5, -99.9, "neg");
    when(pictureRepository.save(any(Picture.class))).thenReturn(saved);

    Picture result = pictureService.createPicture(dto);

    assertNotNull(result);
    assertEquals(-5, result.getStock());
    assertEquals(-99.9, result.getPrice());
    verify(pictureRepository).save(any(Picture.class));
  }

  @Test
  void updatePictureById_nullDto_throwsNullPointerException() {
    Picture existing = new Picture("Existing", 1, 1.0, "ex");
    when(pictureRepository.findById(2)).thenReturn(Optional.of(existing));

    assertThrows(NullPointerException.class, () -> pictureService.updatePictureById(2, null));
    verify(pictureRepository).findById(2);
  }

  @Test
  void getAllPictures_repositoryThrows_runtimePropagates() {
    when(pictureRepository.findAll()).thenThrow(new RuntimeException("db error"));
    assertThrows(RuntimeException.class, () -> pictureService.getAllPictures(false));
    verify(pictureRepository).findAll();
  }

  @Test
  void createPicture_whenRepositorySaveThrows_exceptionPropagates() {
    PictureDto dto = new PictureDto("WillFail", 1, 1.0, "fail");
    when(pictureRepository.save(any(Picture.class))).thenThrow(new RuntimeException("save failed"));

    assertThrows(RuntimeException.class, () -> pictureService.createPicture(dto));
    verify(pictureRepository).save(any(Picture.class));
  }

}
