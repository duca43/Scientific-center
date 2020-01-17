package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicUserInfoDto implements Serializable {

    private static final long serialVersionUID = -3912454939039844520L;
    private Long id;
    private String name;

    @Override
    public String toString() {
        return this.name;
    }
}