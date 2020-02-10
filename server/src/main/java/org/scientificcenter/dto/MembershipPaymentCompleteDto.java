package org.scientificcenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MembershipPaymentCompleteDto {

    private String merchantOrderId;
    private String authorUsername;
}