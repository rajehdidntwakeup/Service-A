package test.servicea.service.converter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import test.servicea.domain.dto.ExternalInventory;

/**
 * Configuration properties class used to load external inventory system details
 * from the application configuration file.
 * This class binds the configuration properties with the prefix "external.inventory"
 * to a map of external inventory systems, where the key is the name of the system
 * and the value is an {@link ExternalInventory} object containing details about that system.
 * The properties are typically used in scenarios where multiple external inventory
 * or catalog services need to be integrated with the application, as seen in
 * classes like {@code ItemServiceImpl}.
 * Usage:
 * - Define the external inventory configurations in the application's properties.
 * - Use this class to retrieve and manipulate the configured external inventory data.
 * This class is marked as a Spring {@code @Component}, enabling it to be injected into
 * other Spring-managed beans such as service layers.
 */
@Component
@ConfigurationProperties(prefix = "external.inventory")
public class ConversionProperties {

  private Map<String, ExternalInventory> externalInventory = new HashMap<>();

  /**
   * Retrieves a map of external inventory systems.
   * The map contains the name of the external inventory system as the key
   * and an {@link ExternalInventory} object as the value, representing
   * the details of each external inventory system.
   *
   * @return a map where the key is a String representing the name of the external
   *         inventory system and the value is an {@link ExternalInventory} object
   *         containing its details.
   */
  public Map<String, ExternalInventory> getExternalInventory() {
    return externalInventory;
  }

  /**
   * Sets the external inventory systems details.
   * This method accepts a map where the key represents the name of the external
   * inventory system, and the value is an {@link ExternalInventory} object, which
   * contains the details of that system.
   *
   * @param externalInventory a map with keys as the names of external inventory systems
   *                          and values as {@link ExternalInventory} objects
   *                          containing their details.
   */
  public void setExternalInventory(Map<String, ExternalInventory> externalInventory) {
    this.externalInventory = externalInventory;
  }
}
