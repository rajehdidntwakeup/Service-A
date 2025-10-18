package test.servicea.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import test.servicea.domain.Picture;
import test.servicea.domain.dto.PictureDto;
import test.servicea.repository.PictureRepository;
import test.servicea.service.PictureService;

/**
 * Implementation of PictureService.
 */
@Service
public class PictureServiceImpl implements PictureService {

  private final PictureRepository pictureRepository;

  /**
   * Constructs a PictureServiceImpl with the provided repository.
   *
   * @param pictureRepository the repository used to access and persist pictures
   */
  public PictureServiceImpl(PictureRepository pictureRepository) {
    this.pictureRepository = pictureRepository;
  }

  @Override
  public Picture createPicture(PictureDto pictureDto) {
    if (pictureDto == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Picture cannot be null");
    }
    Picture picture =
        new Picture(pictureDto.getName(), pictureDto.getStock(), pictureDto.getPrice(), pictureDto.getDescription());
    pictureRepository.save(picture);
    return picture;
  }

  @Override
  public List<Picture> getAllPictures(boolean multiCatalog) {
    List<Picture> pictures = pictureRepository.findAll();
    if (!pictures.isEmpty()) {
      return pictures;
    }
    return List.of();
  }

  @Override
  public Picture getPictureById(int id) {
    Optional<Picture> picture = pictureRepository.findById(id);
    return picture.orElse(null);
  }

  @Override
  public Picture updatePictureById(int id, PictureDto pictureDto) {
    Optional<Picture> picture = pictureRepository.findById(id);
    if (picture.isPresent()) {
      Picture pictureToUpdate = picture.get();
      pictureToUpdate.setName(pictureDto.getName());
      pictureToUpdate.setStock(pictureDto.getStock());
      pictureToUpdate.setPrice(pictureDto.getPrice());
      pictureToUpdate.setDescription(pictureDto.getDescription());
      pictureRepository.save(pictureToUpdate);
      return pictureToUpdate;
    }
    return null;
  }
}
