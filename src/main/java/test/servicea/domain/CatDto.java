package test.servicea.domain;

/**
 * Data Transfer Object for Cat.
 */
public class CatDto {

  private String name;
  private String color;
  private int age;

  /**
   * Creates an empty CatDto instance.
   */
  public CatDto() {
  }

  /**
   * Creates a CatDto with the provided attributes.
   *
   * @param name  the cat's name
   * @param color the cat's color
   * @param age   the cat's age
   */
  public CatDto(String name, String color, int age) {
    this.name = name;
    this.color = color;
    this.age = age;
  }

  /**
   * Retrieves the name of the cat.
   *
   * @return the name of the cat as a String.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the cat.
   *
   * @param name the name to set, represented as a String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the color of the cat.
   *
   * @return the color of the cat as a String.
   */
  public String getColor() {
    return color;
  }

  /**
   * Sets the color of the cat.
   *
   * @param color the color to set, represented as a String
   */
  public void setColor(String color) {
    this.color = color;
  }

  /**
   * Retrieves the age of the cat.
   *
   * @return the age of the cat as an integer.
   */
  public int getAge() {
    return age;
  }

  /**
   * Sets the age of the cat.
   *
   * @param age the age to set, represented as an integer
   */
  public void setAge(int age) {
    this.age = age;
  }
}
