package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.AddCoauthorDto;
import org.scientificcenter.dto.MagazineDto;
import org.scientificcenter.dto.ScientificPaperDto;
import org.scientificcenter.model.ScientificPaper;
import org.scientificcenter.repository.ScientificPaperRepository;
import org.scientificcenter.service.ScientificPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScientificPaperServiceImpl implements ScientificPaperService {

    private final ScientificPaperRepository scientificPaperRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ScientificPaperServiceImpl(final ScientificPaperRepository scientificPaperRepository, final ModelMapper modelMapper) {
        this.scientificPaperRepository = scientificPaperRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ScientificPaper findById(final Long id) {
        return this.scientificPaperRepository.findById(id).orElse(null);
    }

    @Override
    public ScientificPaper save(final ScientificPaper scientificPaper) {
        Assert.notNull(scientificPaper, "Scientific paper object can't be null!");
        this.scientificPaperRepository.save(scientificPaper);
        log.info("Scientific paper with id '{}' is saved successfully", scientificPaper.getId());
        return scientificPaper;
    }

    @Override
    public List<ScientificPaperDto> findAllByAuthor(final String author) {
        Assert.notNull(author, "Author username can't be null!");
        log.info("Retrieving all scientific papers created by author with username '{}'", author);
        final List<ScientificPaperDto> scientificPaperDtos = this.scientificPaperRepository.findAllByAuthor_Username(author)
                .stream()
                .map(scientificPaper -> {
                    final ScientificPaperDto scientificPaperDto = this.modelMapper.map(scientificPaper, ScientificPaperDto.class);
                    final MagazineDto magazineDto = this.modelMapper.map(scientificPaper.getMagazine(), MagazineDto.class);

                    final List<AddCoauthorDto> coauthorDtos = scientificPaper.getCoauthors()
                            .stream()
                            .map(coauthor -> this.modelMapper.map(coauthor, AddCoauthorDto.class))
                            .collect(Collectors.toList());
                    scientificPaperDto.setCoauthorDtos(coauthorDtos);
                    scientificPaperDto.setMagazineDto(magazineDto);
                    return scientificPaperDto;
                })
                .collect(Collectors.toList());
        log.info("Number of retrieved scientific papers for author with username '{}': {}", author, scientificPaperDtos.size());
        return scientificPaperDtos;
    }
}