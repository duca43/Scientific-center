package org.scientificcenter.service;

import org.scientificcenter.model.ScientificArea;

import java.util.List;

public interface ScientificAreasService {

    List<ScientificArea> findAll();

    ScientificArea findById(Long id);
}
