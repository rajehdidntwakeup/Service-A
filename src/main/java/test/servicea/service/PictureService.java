package test.servicea.service;

import java.util.List;

import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;

/**
 * Service interface for Picture entities.
 */
public interface PictureService {

  /**
   * Creates a Picture entity.
   *
   * @param pictureDto the Picture to create
   * @return the created Picture entity
   * @throws ResponseStatusException if the provided PictureDto is null
   */
  Picture createPicture(PictureDto pictureDto);

  /**
   * Retrieves all Picture entities.
   *
   * @return a list of Picture entities
   */
  List<Picture> getAllPictures();

  /**
   * Retrieves a Picture entity by its unique ID.
   *
   * @param id the unique ID of the Picture to retrieve
   * @return the Picture entity with the specified ID, or null if no Picture with that ID exists
   */
  Picture getPictureById(int id);

  /**
   * Updates an existing Picture entity.
   *
   * @param id the unique ID of the Picture to update
   * @param pictureDto the Picture to update with
   * @return the updated Picture entity
   */
  Picture updatePictureById(int id, PictureDto pictureDto);


}
