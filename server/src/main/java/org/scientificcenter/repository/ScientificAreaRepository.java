package org.scientificcenter.repository;

import org.scientificcenter.model.ScientificArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScientificAreaRepository extends JpaRepository<ScientificArea, Long> {
}