package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scientificcenter.security.UserState;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationSubProcessResponseDto {

    private String superProcessInstanceId;
    private UserState userState;
}