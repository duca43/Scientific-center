package org.scientificcenter.repository;

import org.scientificcenter.model.Authority;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findAllByAuthoritiesNotContains(Authority authority);

    List<User> findDistinctByUsernameNotAndAuthoritiesContainsAndScientificAreasIsIn(String username, Authority authority, List<ScientificArea> scientificAreas);
}