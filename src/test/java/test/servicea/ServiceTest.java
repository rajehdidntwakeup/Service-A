package test.servicea;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import test.servicea.controller.CatController;
import test.servicea.domain.Cat;
import test.servicea.domain.CatDto;
import test.servicea.repository.CatRepository;

public class ServiceTest {



    @Mock
    public CatRepository catRepository;

    @InjectMocks
    public CatController catController = new CatController(catRepository);

    @Test
    public void test() {
        CatDto catDto = new CatDto("name", "color", 1);

        Mockito.when(catRepository.save(new Cat(catDto.getName(), catDto.getColor(), catDto.getAge()))).thenReturn(new Cat());
        catController.saveCat(catDto);
    }

}
