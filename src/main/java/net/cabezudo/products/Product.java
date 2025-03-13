package net.cabezudo.products;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Product implements Serializable {
  private Long id;
  private String description;
  private Integer quantity;
}
