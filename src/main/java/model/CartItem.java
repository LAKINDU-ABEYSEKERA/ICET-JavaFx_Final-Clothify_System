package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private String productId;
    private String productName;
    private Integer qty;
    private Double unitPrice;
    private Double total;
}