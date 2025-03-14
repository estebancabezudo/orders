package net.cabezudo.orders.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cabezudo.Application;
import net.cabezudo.caches.RedisConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {

  @TestConfiguration
  static class TestCacheConfig {
    @Bean
    @Primary
    public CacheManager cacheManager() {
      return new ConcurrentMapCacheManager("productCache", "ordersCache");
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testOrderNotFound() throws Exception {
    String token = getToken();
    int orderId = 15;
    mockMvc.perform(get("/orders/{orderId}", orderId)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Entity with id " + orderId + " NOT FOUND"));
  }

  @Test
  void testPostOrderAndListCustomerOrders() throws Exception {
    postOrder(1L);
    postOrder(1L);
    postOrder(2L);

    String token = getToken();
    mockMvc.perform(get("/orders/1")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("OK"))
        .andExpect(jsonPath("$.message").value("Order retrieved successfully."))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.customerId").value(1))
        .andExpect(jsonPath("$.data.productId").value(1))
        .andExpect(jsonPath("$.data.quantity").value(1))
        .andExpect(jsonPath("$.data.price").value(10.00));

    mockMvc.perform(patch("/orders/{orderId}/status", 1)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"status\":\"PENDINGx\" }"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_STATUS"))
        .andExpect(jsonPath("$.message").value("Invalid status 'PENDINGx', must be one of [ PENDING, SHIPPED, DELIVERED ]"));

    mockMvc.perform(patch("/orders/{orderId}/status", 1)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"status\":\"PENDING\" }"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("OK"))
        .andExpect(jsonPath("$.message").value("Order status updated"));

    mockMvc.perform(patch("/orders/{orderId}/status", 1)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"status\":\"SHIPPED\" }"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("OK"))
        .andExpect(jsonPath("$.message").value("Order status updated"));

    mockMvc.perform(patch("/orders/{orderId}/status", 1)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"status\":\"PENDING\" }"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_STATUS"))
        .andExpect(jsonPath("$.message").value("You cannot change from SHIPPED to PENDING"));

    mockMvc.perform(patch("/orders/{orderId}/status", 1)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ }"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_STATUS"))
        .andExpect(jsonPath("$.message").value("Status is required, pick one of [ PENDING, SHIPPED, DELIVERED ]"));
  }

  private String postOrder(Long customerId) throws Exception {
    String token = getToken();

    RestOrder order = new RestOrder();
    order.setCustomerId(customerId);
    order.setProductId(1L);
    order.setQuantity(1);
    order.setPrice(new BigDecimal(10));
    String orderJson = objectMapper.writeValueAsString(order);

    mockMvc.perform(post("/orders")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("OK"))
        .andExpect(jsonPath("$.message").value("Order added successfully."));
    return orderJson;
  }

  @Test
  void testPostOrderWithZeroQuantity() throws Exception {
    String token = getToken();

    RestOrder order = new RestOrder();
    order.setCustomerId(1L);
    order.setProductId(1L);
    order.setQuantity(0);
    order.setPrice(new BigDecimal(10));
    String orderJson = objectMapper.writeValueAsString(order);

    mockMvc.perform(post("/orders")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
        .andExpect(jsonPath("$.message").value("The quantity property MUST NOT be 0"));
  }

  @Test
  void testGetOrdersByCustomerId() throws Exception {
    String token = getToken();
    int customerId = 1;
    mockMvc.perform(get("/orders/customer/{customerId}", customerId)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("OK"))
        .andExpect(jsonPath("$.message").value("The list of orders was successfully retrieved for customer: " + customerId));
  }

  private String getToken() throws Exception {
    MvcResult loginResult = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"username\", \"password\": \"password\" }"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.token").exists())
        .andReturn();

    return loginResult.getResponse().getContentAsString().replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
  }
}
