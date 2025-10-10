package test.servicea.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for Picture.
 */
public class PictureDto {

  @NotBlank(message = "Name is mandatory.")
  private String name;
  @Min(value = 0, message = "Stock cannot be negative.")
  private int stock;
  @Min(value = 0, message = "Price cannot be negative.")
  private double price;
  @NotBlank(message = "Description is mandatory")
  private String description;

  /**
   * Creates an empty PictureDto instance.
   */
  public PictureDto() {
  }

  /**
   * Creates a PictureDto with the provided attributes.
   *
   * @param name        the name of the picture
   * @param stock       the available stock of the picture
   * @param price       the price of the picture
   * @param description a description of the picture
   */
  public PictureDto(String name, int stock, double price, String description) {
    this.name = name;
    this.stock = stock;
    this.price = price;
    this.description = description;
  }

  /**
   * Retrieves the name of the picture.
   *
   * @return the name of the picture as a String.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the picture.
   *
   * @param name the name to set, represented as a String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the stock quantity of the picture.
   *
   * @return the stock quantity as an integer.
   */
  public int getStock() {
    return stock;
  }

  /**
   * Sets the stock quantity of the picture.
   *
   * @param stock the stock quantity to set, represented as an integer
   */
  public void setStock(int stock) {
    this.stock = stock;
  }

  /**
   * Retrieves the price of the picture.
   * @return the price of the picture as a double
   */
  public double getPrice() {
    return price;
  }

  /**
   * Sets the price of the picture.
   * @return the price of the picture as a double
   */
  public String getDescription() {
    return description;
  }
}
