package org.scientificcenter.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountVerificationDto implements Serializable {

    private static final long serialVersionUID = -5155521924432566408L;
    private String activationCode;
    private String username;
}