package org.scientificcenter.dto;

import lombok.*;
import org.scientificcenter.model.ScientificArea;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MagazineDto implements Serializable {

    private static final long serialVersionUID = -6868471237619720238L;
    private Long id;
    private String issn;
    private String name;
    private String payment;
    private Boolean enabled;
    private Boolean chosenEditorsAndReviewers;
    private Boolean requestedChanges;
    private String merchantId;
    private Boolean enabledAsMerchant;
    private Double membershipPrice;
    private String membershipCurrency;
    private List<ScientificArea> scientificAreas;
}