package org.scientificcenter.model;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails {

    private static final long serialVersionUID = -4902592777322346460L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String email;

    @Column
    private String title;

    @ManyToOne
    private Location location;

    @ManyToMany
    private Set<ScientificArea> scientificAreas;

    @ManyToMany(fetch = FetchType.EAGER)
    @Getter(AccessLevel.NONE)
    private Set<Authority> authorities;

    @Column
    @Getter(AccessLevel.NONE)
    private Boolean enabled;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Collection<Authority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}