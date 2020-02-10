package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {

    private String merchantId;
    private String merchantName;
    private String username;
    private String item;
    private Double price;
    private String currency;
    private String description;
    private String returnUrl;
    private String processInstanceId;
}