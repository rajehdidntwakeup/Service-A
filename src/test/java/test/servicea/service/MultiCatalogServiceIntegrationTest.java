package test.servicea.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import test.servicea.domain.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@Transactional
@TestPropertySource(
    locations = "classpath:application-test.properties",
    properties = {
        // Provide the correct properties binding for ConversionProperties used by ItemServiceImpl
        // Two external catalogs on ports 8082 and 8083
        "external.inventory.externalInventory.b=B,http://localhost:8082/api/inventory",
        "external.inventory.externalInventory.c=C,http://localhost:8083/api/inventory"
    }
)
public class MultiCatalogServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUpMockServer() {
        // ItemServiceImpl holds a private RestTemplate built from RestTemplateBuilder.
        // We obtain it via reflection, then bind MockRestServiceServer to it.
        Object restTemplateObj = ReflectionTestUtils.getField(itemService, "restTemplate");
        assertNotNull(restTemplateObj, "RestTemplate should be initialized in ItemServiceImpl");
        RestTemplate restTemplate = (RestTemplate) restTemplateObj;
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @AfterEach
    void tearDownMockServer() {
        if (mockServer != null) {
            mockServer.verify();
        }
    }

    @Test
    void getAllItems_withoutMultiCatalog_returnsOnlyLocal() {
        // Arrange local items
        itemService.createItem(new test.servicea.domain.dto.ItemDto("Local A", 5, 10.0, "LA"));
        itemService.createItem(new test.servicea.domain.dto.ItemDto("Local B", 2, 20.0, "LB"));

        // Act
        List<Item> items = itemService.getAllItems(false);

        // Assert
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Local A")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Local B")));
    }

    @Test
    void getAllItems_withMultiCatalog_aggregatesLocalAndExternal() {
        // Arrange local
        itemService.createItem(new test.servicea.domain.dto.ItemDto("Local A", 5, 10.0, "LA"));

        // Stub external inventories
        String inventoryBUrl = "http://localhost:8082/api/inventory";
        String inventoryCUrl = "http://localhost:8083/api/inventory";
        String externalBJson = """
            [
              {"name":"ExtB-1","stock":3,"price":11.0,"description":"B1"},
              {"name":"ExtB-2","stock":6,"price":12.5,"description":"B2"}
            ]""";
        String externalCJson = """
            [
              {"name":"ExtC-1","stock":7,"price":21.0,"description":"C1"}
            ]""";

        mockServer.expect(ExpectedCount.once(), requestTo(inventoryBUrl))
                .andRespond(withSuccess(externalBJson, MediaType.APPLICATION_JSON));
        mockServer.expect(ExpectedCount.once(), requestTo(inventoryCUrl))
                .andRespond(withSuccess(externalCJson, MediaType.APPLICATION_JSON));

        // Act
        List<Item> items = itemService.getAllItems(true);

        // Assert: 1 local + 2 (B) + 1 (C) = 4
        assertEquals(4, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Local A")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("ExtB-1")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("ExtB-2")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("ExtC-1")));
    }

    @Test
    void getAllItems_withMultiCatalog_handlesOneExternalFailure() {
        // Arrange local
        itemService.createItem(new test.servicea.domain.dto.ItemDto("Local X", 1, 1.0, "LX"));

        String inventoryBUrl = "http://localhost:8082/api/inventory";
        String inventoryCUrl = "http://localhost:8083/api/inventory";

        // B fails
        mockServer.expect(ExpectedCount.once(), requestTo(inventoryBUrl))
                .andRespond(withServerError());
        // C succeeds with one item
        String externalCJson = "[ {\"name\":\"ExtC-only\",\"stock\":9,\"price\":99.0,\"description\":\"C-only\"} ]";
        mockServer.expect(ExpectedCount.once(), requestTo(inventoryCUrl))
                .andRespond(withSuccess(externalCJson, MediaType.APPLICATION_JSON));

        // Act
        List<Item> items = itemService.getAllItems(true);

        // Assert: should include local and the successful external items; failure is swallowed
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Local X")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("ExtC-only")));
    }

    @Test
    void getAllItems_withMultiCatalog_andEmptyDb_returnsOnlyExternal() {
        // No local items created
        String inventoryBUrl = "http://localhost:8082/api/inventory";
        String inventoryCUrl = "http://localhost:8083/api/inventory";

        String externalBJson = "[ {\"name\":\"EB\",\"stock\":1,\"price\":1.5,\"description\":\"eb\"} ]";
        String externalCJson = "[ {\"name\":\"EC\",\"stock\":2,\"price\":2.5,\"description\":\"ec\"} ]";

        mockServer.expect(ExpectedCount.once(), requestTo(inventoryBUrl))
                .andRespond(withSuccess(externalBJson, MediaType.APPLICATION_JSON));
        mockServer.expect(ExpectedCount.once(), requestTo(inventoryCUrl))
                .andRespond(withSuccess(externalCJson, MediaType.APPLICATION_JSON));

        // Act
        List<Item> items = itemService.getAllItems(true);

        // Assert: only external 2 entries
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("EB")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("EC")));
    }
}
