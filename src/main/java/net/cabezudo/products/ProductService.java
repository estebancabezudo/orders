package net.cabezudo.products;

import net.cabezudo.products.exceptions.ProductWebServiceException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductService {
  @Cacheable(value = "productCache", key = "#productId")
  public String getDescription(Long productId) throws ProductWebServiceException {
    String url = "https://cabezudo.dev/api/v1/products/" + productId;

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody().getDescription();
    } else {
      throw new ProductWebServiceException("Error calling product service: " + response.getStatusCode());
    }
  }

  public boolean areAvailable(Long productId, Integer quantity) throws ProductWebServiceException {
    String url = "https://cabezudo.dev/api/v1/products/" + productId;

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody().getQuantity() > quantity;
    } else {
      throw new ProductWebServiceException("Error calling product service: " + response.getStatusCode());
    }
  }
}
