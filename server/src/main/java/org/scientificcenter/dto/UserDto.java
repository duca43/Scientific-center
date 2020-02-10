package org.scientificcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scientificcenter.model.Authority;
import org.scientificcenter.model.Location;
import org.scientificcenter.model.ScientificArea;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String title;
    private Location location;
    private Set<ScientificArea> scientificAreas;
    private Set<Authority> authorities;
    private Boolean enabled;
}