package org.scientificcenter.dto;

import lombok.*;
import org.scientificcenter.model.Location;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AddCoauthorDto implements Serializable {

    private static final long serialVersionUID = 4861484732549047657L;
    private String name;
    private String email;
    private Location location;
    private Boolean wantMoreCoauthors;
}