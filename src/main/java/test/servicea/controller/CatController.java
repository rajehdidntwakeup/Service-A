package test.servicea.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.servicea.domain.Cat;
import test.servicea.domain.CatDto;
import test.servicea.repository.CatRepository;

import java.util.List;

@RestController
@RequestMapping("/cats")
@CrossOrigin(origins = "http://localhost:8081", allowedHeaders = "*")
public class CatController {

    CatRepository catRepository;

    public CatController(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

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

    @GetMapping(value = "/all")
    public ResponseEntity<List<Cat>> getAllCats() {
        List<Cat> cats = catRepository.findAll();
        return ResponseEntity.ok(cats);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Cat> getCatById(@PathVariable int id) {
        Cat cat = catRepository.findById(id).orElse(null);
        if (cat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cat);
    }

}
