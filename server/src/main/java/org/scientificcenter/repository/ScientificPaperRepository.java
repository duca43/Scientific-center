package org.scientificcenter.repository;

import org.scientificcenter.model.ScientificPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScientificPaperRepository extends JpaRepository<ScientificPaper, Long> {

    List<ScientificPaper> findAllByAuthor_Username(String username);
}