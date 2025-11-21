package test.servicea.domain.dto;

/**
 * Represents an external inventory system containing a name and URL.
 * The `ExternalInventory` class is typically used to represent external catalog
 * services and their connection details.
 */
public class ExternalInventory {
  private String name;
  private String url;


  /**
   * Constructs an instance of ExternalInventory with the provided name and URL.
   *
   * @param name the name of the external inventory system
   * @param url  the URL of the external inventory system
   */
  public ExternalInventory(String name, String url) {
    this.name = name;
    this.url = url;
  }

  /**
   * Retrieves the name of the external inventory system.
   *
   * @return the name of the external inventory system as a String.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the external inventory system.
   *
   * @param name the name to set, represented as a String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retrieves the URL of the external inventory system.
   *
   * @return the URL of the external inventory system as a String.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the URL of the external inventory system.
   *
   * @param url the URL to set, represented as a String
   */
  public void setUrl(String url) {
    this.url = url;
  }
}
