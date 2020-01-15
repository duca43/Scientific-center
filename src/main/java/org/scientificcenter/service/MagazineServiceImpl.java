package org.scientificcenter.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.MagazineDto;
import org.scientificcenter.exception.UserNotFoundException;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.User;
import org.scientificcenter.repository.MagazineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MagazineServiceImpl implements MagazineService {

    private final MagazineRepository magazineRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public MagazineServiceImpl(final MagazineRepository magazineRepository, final UserService userService) {
        this.magazineRepository = magazineRepository;
        this.userService = userService;
    }

    @Override
    public List<Magazine> findAll() {
        return this.magazineRepository.findAll();
    }

    @Override
    public Magazine findByIssn(final String issn) {
        return this.magazineRepository.findByIssn(issn);
    }

    @Override
    public Magazine save(Magazine magazine) {
        Assert.notNull(magazine, "Magazine object can't be null!");
        Assert.noNullElements(Stream.of(magazine.getIssn(),
                magazine.getName(),
                magazine.getPayment(),
                magazine.getScientificAreas(),
                magazine.getMainEditor())
                        .toArray(),
                "One or more fields are null!");

        MagazineServiceImpl.log.info("Saving magazine with issn '{}' and name '{}'", magazine.getIssn(), magazine.getName());

        MagazineServiceImpl.log.info("DEBUG: magazine id -> {}", magazine.getId());
        magazine = this.magazineRepository.save(magazine);

        MagazineServiceImpl.log.info("Magazine with issn '{}' and name '{}' is saved successfully", magazine.getIssn(), magazine.getName());

        return magazine;
    }

    @Override
    public List<MagazineDto> findByMainEditor(final String editor) {
        Assert.notNull(editor, "Editor's username can't be null!");

        final User mainEditor = this.userService.findByUsername(editor);

        if (mainEditor == null) throw new UserNotFoundException(editor);

        final List<Magazine> magazines = this.magazineRepository.findAllByMainEditor(mainEditor);

        return magazines.stream().map(magazine -> {
            MagazineDto magazineDto = this.modelMapper.map(magazine, MagazineDto.class);
            magazineDto.setPayment(magazine.getPayment().getType());
            return magazineDto;
        }).collect(Collectors.toList());
    }
}