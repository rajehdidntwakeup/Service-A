package test.servicea.domain;

import lombok.Data;

@Data
public class CatDto {

    private String name;
    private String color;
    private int age;

    public CatDto() {
    }

    public CatDto(String name, String color, int age) {
        this.name = name;
        this.color = color;
        this.age = age;
    }
}
