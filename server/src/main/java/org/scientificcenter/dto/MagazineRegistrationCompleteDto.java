package org.scientificcenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MagazineRegistrationCompleteDto {

    private String merchantId;
    private String editorUsername;
}