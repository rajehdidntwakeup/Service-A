package test.servicea.service.converter;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import test.servicea.domain.dto.ExternalInventory;


/**
 * A Spring Converter implementation that converts a String to an ExternalInventory object.
 * The input String is expected to be in a specific format, typically "name,url",
 * where "name" represents the name of the external inventory system and "url" is
 * the associated URL. This class parses the input and creates an instance of
 * {@link ExternalInventory} based on the provided data.
 * This converter is specifically registered as a Spring bean using the
 * {@code @Component} and {@code @ConfigurationPropertiesBinding} annotations,
 * enabling type conversion in Spring's configuration binding process.
 * Features:
 * - Parses the String input based on a comma (,) as the delimiter.
 * - Verifies that the resulting data includes exactly two parts: name and URL.
 * - Throws {@code IllegalArgumentException} if the format of the input String
 *   does not match the expected format.
 * Usage:
 * This class is used in scenarios where configuration properties or other String data
 * need to be converted into {@link ExternalInventory} objects for further processing
 * in the application.
 */
@Component
@ConfigurationPropertiesBinding
public class ExternalInventoryConverter implements Converter<String, ExternalInventory> {

  /**
   * Converts a given String in the format "name,url" into an instance of {@link ExternalInventory}.
   * The method splits the input String by a comma (,) delimiter and assigns the resulting
   * parts to the name and URL of the ExternalInventory object.
   *
   * @param source the input String in the format "name,url" containing the name and URL of
   *               the external inventory system
   * @return an instance of {@link ExternalInventory} initialized with the name and URL
   *         extracted from the input String
   * @throws IllegalArgumentException if the input String does not contain exactly two parts
   *                                  separated by a comma
   */
  @Override
  public ExternalInventory convert(String source) {
    String[] data = source.split(",");
    if (data.length == 2) {
      return new ExternalInventory(data[0], data[1]);
    }
    throw new IllegalArgumentException("Invalid external service format: " + source);
  }
}
