package test.servicea.controller;


import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;
import test.servicea.service.PictureService;

/**
 * Controller for managing Picture-related API endpoints.
 * Handles HTTP requests and responses for operations on Picture entities.
 */
@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*")
public class PictureController {

  private final PictureService pictureService;

  /**
   * Constructs a PictureController with the provided service.
   *
   * @param pictureService the service used to access and persist pictures
   */
  public PictureController(PictureService pictureService) {
    this.pictureService = pictureService;
  }

  /**
   * Creates a new picture resource based on the provided information.
   *
   * @param pictureDto the data transfer object containing details to create a new picture
   * @return a ResponseEntity containing the created Picture and an HTTP status of 201 (Created)
   */
  @PostMapping
  public ResponseEntity<Picture> createPicture(@Valid @RequestBody PictureDto pictureDto) {
    Picture picture = pictureService.createPicture(pictureDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(picture);
  }

  /**
   * Retrieves a list of all pictures, optionally across multiple catalogs, based on the provided parameter.
   *
   * @param multiCatalog a boolean flag indicating whether to retrieve pictures
   *                     from multiple catalogs (true) or a single catalog (false);
   *                     defaults to false if not specified
   * @return a ResponseEntity containing a list of Picture objects along with an HTTP status of 200 (OK)
   */
  @GetMapping
  public ResponseEntity<List<Picture>> getAllPictures(
      @RequestParam(name = "multi-catalog", required = false, defaultValue = "false") boolean multiCatalog
  ) {
    List<Picture> pictures = pictureService.getAllPictures(multiCatalog);
    return ResponseEntity.ok(pictures);
  }

  /**
   * Retrieves a picture resource by its unique identifier.
   *
   * @param id the unique identifier of the picture to retrieve
   * @return a ResponseEntity containing the Picture if found with an HTTP status of 200 (OK),
   *         or an HTTP status of 404 (Not Found) if the picture does not exist
   */
  @GetMapping("/{id}")
  public ResponseEntity<Picture> getPictureById(@PathVariable int id) {
    Picture picture = pictureService.getPictureById(id);
    if (picture == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(picture);
  }

  /**
   * Updates an existing picture resource identified by its unique identifier
   * using the data provided in the PictureDto object.
   *
   * @param id the unique identifier of the picture to update
   * @param pictureDto the data transfer object containing updated details for the picture
   * @return a ResponseEntity containing the updated Picture with an HTTP status of 200 (OK)
   *         if the update is successful, or an HTTP status of 404 (Not Found)
   *         if the picture with the specified ID does not exist
   */
  @PutMapping("/{id}")
  public ResponseEntity<Picture> updatePictureById(@PathVariable int id, @Valid @RequestBody PictureDto pictureDto) {
    Picture picture = pictureService.updatePictureById(id, pictureDto);
    if (picture == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(picture);
  }
}
