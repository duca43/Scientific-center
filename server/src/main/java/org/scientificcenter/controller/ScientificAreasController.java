package org.scientificcenter.controller;

import org.scientificcenter.model.ScientificArea;
import org.scientificcenter.service.ScientificAreasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/scientific_areas")
public class ScientificAreasController {

    private final ScientificAreasService scientificAreasService;

    @Autowired
    public ScientificAreasController(final ScientificAreasService scientificAreasService) {
        this.scientificAreasService = scientificAreasService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScientificArea>> getScientificAreas() {
        return ResponseEntity.ok(this.scientificAreasService.findAll());
    }
}