package org.scientificcenter.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MembershipPriceDto implements Serializable {

    private static final long serialVersionUID = 9003608229860524232L;

    private String issn;
    private Double price;
    private String currency;
}