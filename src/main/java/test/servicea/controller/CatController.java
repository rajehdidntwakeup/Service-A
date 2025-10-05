package test.servicea.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.servicea.domain.Cat;
import test.servicea.domain.CatDto;
import test.servicea.repository.CatRepository;

/**
 * REST controller providing endpoints to manage Cat entities.
 */
@RestController
@RequestMapping("/cats")
@CrossOrigin(origins = "http://localhost:8081", allowedHeaders = "*")
public class CatController {

  private final CatRepository catRepository;

  /**
   * Constructs a CatController with the provided repository.
   *
   * @param catRepository repository used to access and persist cats
   */
  public CatController(CatRepository catRepository) {
    this.catRepository = catRepository;
  }

  /**
   * Saves a new cat based on the provided CatDto data and persists it into the database.
   *
   * @param catDto the data transfer object containing cat details such as name, color, and age
   * @return a ResponseEntity containing a success or error message. If the operation is successful,
   *     a 200 OK response with the message "Saved" is returned;
   *     otherwise, a 500 Internal Server Error response with the message "Error" is returned.
   */
  @PostMapping(value = "/save")
  public ResponseEntity<String> saveCat(@RequestBody CatDto catDto) {
    try {
      catRepository.save(new Cat(catDto.getName(), catDto.getColor(), catDto.getAge()));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().body("Error");
    }
    return ResponseEntity.ok("Saved");
  }

  /**
   * Retrieves a list of all cats from the database.
   *
   * @return a ResponseEntity containing a list of Cat objects, wrapped in an HTTP 200 OK response.
   */
  @GetMapping(value = "/all")
  public ResponseEntity<List<Cat>> getAllCats() {
    List<Cat> cats = catRepository.findAll();
    return ResponseEntity.ok(cats);
  }

  /**
   * Retrieves a specific cat from the database based on its unique ID.
   *
   * @param id the unique identifier of the cat to be retrieved
   * @return a ResponseEntity containing the Cat object and an HTTP 200 OK response if the cat is found,
   *     or an HTTP 404 Not Found response if the cat does not exist in the database
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity<Cat> getCatById(@PathVariable int id) {
    Cat cat = catRepository.findById(id).orElse(null);
    if (cat == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(cat);
  }

}
