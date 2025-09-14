package test.servicea;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import test.servicea.controller.CatController;
import test.servicea.domain.Cat;
import test.servicea.domain.CatDto;
import test.servicea.repository.CatRepository;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    public CatRepository catRepository;

    @InjectMocks
    public CatController catController;

    @Test
    public void test() {
        CatDto catDto = new CatDto("name", "color", 1);

        Mockito.when(catRepository.save(Mockito.any(Cat.class))).thenReturn(new Cat());
        ResponseEntity<String> response = catController.saveCat(catDto);
        assert response.getStatusCode().is2xxSuccessful();
    }

}
