package org.scientificcenter.repository;

import org.scientificcenter.model.Coauthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoauthorRepository extends JpaRepository<Coauthor, Long> {
}