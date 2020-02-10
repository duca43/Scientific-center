package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeginProcessForSubmittingTextDto {

    private FormFieldsDto formFieldsDto;
    private Boolean userExists;
}