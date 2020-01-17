package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.repository.ScientificAreaRepository;
import org.scientificcenter.service.ScientificAreasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ScientificAreasServiceImpl implements ScientificAreasService {

    private final ScientificAreaRepository scientificAreaRepository;

    @Autowired
    public ScientificAreasServiceImpl(final ScientificAreaRepository scientificAreaRepository) {
        this.scientificAreaRepository = scientificAreaRepository;
    }

    @Override
    public List<ScientificArea> findAll() {
        return this.scientificAreaRepository.findAll();
    }
}
