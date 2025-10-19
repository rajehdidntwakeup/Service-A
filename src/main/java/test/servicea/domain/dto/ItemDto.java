package test.servicea.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for Item.
 */
public class ItemDto {

  @NotBlank(message = "Name is mandatory.")
  private String name;
  @Min(value = 0, message = "Stock cannot be negative.")
  private int stock;
  @Min(value = 0, message = "Price cannot be negative.")
  private double price;
  @NotBlank(message = "Description is mandatory")
  private String description;

  /**
   * Creates an empty ItemDto instance.
   */
  public ItemDto() {
  }

  /**
   * Creates an ItemDto with the provided attributes.
   *
   * @param name        the name of the item
   * @param stock       the available stock of the item
   * @param price       the price of the item
   * @param description a description of the item
   */
  public ItemDto(String name, int stock, double price, String description) {
    this.name = name;
    this.stock = stock;
    this.price = price;
    this.description = description;
  }

  /**
   * Retrieves the name of the item.
   *
   * @return the name of the item as a String.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the item.
   *
   * @param name the name to set, represented as a String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the stock quantity of the item.
   *
   * @return the stock quantity as an integer.
   */
  public int getStock() {
    return stock;
  }

  /**
   * Sets the stock quantity of the item.
   *
   * @param stock the stock quantity to set, represented as an integer
   */
  public void setStock(int stock) {
    this.stock = stock;
  }

  /**
   * Retrieves the price of the item.
   * @return the price of the item as a double
   */
  public double getPrice() {
    return price;
  }

  /**
   * Sets the price of the item.
   * @return the price of the item as a double
   */
  public String getDescription() {
    return description;
  }
}
