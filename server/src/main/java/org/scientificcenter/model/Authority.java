package org.scientificcenter.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Authority implements GrantedAuthority {

    private static final long serialVersionUID = 2223272055515777434L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Getter(AccessLevel.NONE)
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }
}