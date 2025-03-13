package net.cabezudo.orders.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestOrder {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotNull
  private Long customerId;

  @NotNull
  private Long productId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String productDescription;

  @NotNull
  @Positive
  private Integer quantity;

  @NotNull
  private BigDecimal price;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String status;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime createdAt;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime updatedAt;
}
