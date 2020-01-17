package org.scientificcenter.dto;

import lombok.*;
import org.scientificcenter.model.Location;
import org.scientificcenter.model.ScientificArea;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegistrationUserDto implements Serializable {

    private static final long serialVersionUID = -7221117729585890536L;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String title;
    private Location location;
    private List<ScientificArea> scientificAreas;
    private Boolean reviewer;
}