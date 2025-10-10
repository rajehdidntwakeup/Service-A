package test.servicea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.servicea.domain.Picture;

/**
 * Spring Data repository for Picture entities.
 */
@Repository
public interface PictureRepository extends JpaRepository<Picture, Integer> {
}
