package org.scientificcenter.service;

import org.scientificcenter.dto.MagazineDto;
import org.scientificcenter.model.Magazine;

import java.util.List;

public interface MagazineService {

    List<Magazine> findAll();

    Magazine findByIssn(String issn);

    Magazine save(Magazine magazine);

    List<MagazineDto> findByMainEditor(String editor);
}