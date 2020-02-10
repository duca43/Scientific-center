package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.scientificcenter.dto.*;
import org.scientificcenter.exception.MagazineNotFoundException;
import org.scientificcenter.exception.NotMainEditorException;
import org.scientificcenter.exception.UserNotFoundException;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.User;
import org.scientificcenter.repository.MagazineRepository;
import org.scientificcenter.service.MagazineService;
import org.scientificcenter.service.UserService;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MagazineServiceImpl implements MagazineService {

    private final MagazineRepository magazineRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final Util util;
    private final ModelMapper modelMapper;

    @Autowired
    public MagazineServiceImpl(final MagazineRepository magazineRepository, final UserService userService, final RestTemplate restTemplate, final Util util, final ModelMapper modelMapper) {
        this.magazineRepository = magazineRepository;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.util = util;
        this.modelMapper = modelMapper;
    }

    @Override
    public Magazine findById(final Long id) {
        return this.magazineRepository.findById(id).orElse(null);
    }

    @Override
    public Magazine findByIssn(final String issn) {
        return this.magazineRepository.findByIssn(issn);
    }

    @Override
    public Magazine findByMerchantId(final String merchantId) {
        return this.magazineRepository.findByMerchantId(merchantId);
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

    @Override
    public RedirectionResponse registerMagazineAsMerchant(final MagazineRegistrationDto magazineRegistrationDto) {
        Assert.notNull(magazineRegistrationDto.getMagazineId(), "Magazine id has to be provided!");

        log.info("Registering magazine with id '{}' as a merchant", magazineRegistrationDto.getMagazineId());

        final Magazine magazine = this.findById(magazineRegistrationDto.getMagazineId());

        if (magazine == null) {
            log.error("Magazine with id '{}' does not exist", magazineRegistrationDto.getMagazineId());
            throw new MagazineNotFoundException(magazineRegistrationDto.getMagazineId());
        }

        final User editor = this.userService.findByUsername(magazineRegistrationDto.getEditorUsername());

        if (editor == null || !magazine.getMainEditor().getId().equals(editor.getId())) {
            log.error("Editor with username '{}' is not main editor of magazine with id '{}'", magazineRegistrationDto.getMagazineId(), magazineRegistrationDto.getEditorUsername());
            throw new NotMainEditorException(magazineRegistrationDto.getMagazineId(), magazineRegistrationDto.getEditorUsername());
        }

        final MerchantRequest merchantRequest = MerchantRequest.builder()
                .name(magazine.getName())
                .returnUrl(Util.HTTP_PREFIX.concat(this.util.FRONTEND_ADDRESS).concat(":").concat(this.util.FRONTEND_PORT).concat("/my_magazines"))
                .build();

        log.info("Call payment gateway service to register magazine with id '{}'", magazineRegistrationDto.getMagazineId());
        final RedirectionResponse redirectionResponse = this.restTemplate.postForEntity(Util.HTTPS_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/register"), merchantRequest, RedirectionResponse.class).getBody();
        if (redirectionResponse == null || redirectionResponse.getId() == null || redirectionResponse.getRedirectionUrl() == null) {
            log.error("Response for registration of magazine with id '{}' from payment gateway is not retrieved properly", magazineRegistrationDto.getMagazineId());
            return null;
        }
        log.info("Retrieved following response for registration of magazine  with id '{}' from payment gateway service: id - {}, return url - {}", magazineRegistrationDto.getMagazineId(), redirectionResponse.getId(), redirectionResponse.getRedirectionUrl());

        magazine.setMerchantId(redirectionResponse.getId());
        magazine.setEnabledAsMerchant(false);
        this.save(magazine);

        log.info("Registration of magazine with id '{}' and given merchant id is '{}' has started successfully", magazineRegistrationDto.getMagazineId(), redirectionResponse.getId());

        return redirectionResponse;
    }

    @Override
    public RegistrationCompleteDto completeMagazineRegistration(final MagazineRegistrationCompleteDto magazineRegistrationCompleteDto) {
        Assert.notNull(magazineRegistrationCompleteDto, "Magazine registration complete object can't bre null");
        Assert.notNull(magazineRegistrationCompleteDto.getMerchantId(), "Merchant id can't be null!");
        Assert.notNull(magazineRegistrationCompleteDto.getEditorUsername(), "Editor username can't be null!");

        log.info("Completing registration of magazine with merchant id '{}'", magazineRegistrationCompleteDto.getMerchantId());

        final Magazine magazine = this.findByMerchantId(magazineRegistrationCompleteDto.getMerchantId());

        if (magazine == null) {
            log.error("Magazine with merchant id '{}' does not exist", magazineRegistrationCompleteDto.getMerchantId());
            throw new MagazineNotFoundException(magazineRegistrationCompleteDto.getMerchantId());
        }

        final User editor = this.userService.findByUsername(magazineRegistrationCompleteDto.getEditorUsername());

        if (editor == null || !magazine.getMainEditor().getId().equals(editor.getId())) {
            log.error("Editor with username '{}' is not main editor of magazine with merchant id '{}'", magazineRegistrationCompleteDto.getMerchantId(), magazineRegistrationCompleteDto.getEditorUsername());
            throw new NotMainEditorException(magazineRegistrationCompleteDto.getMerchantId(), magazineRegistrationCompleteDto.getEditorUsername());
        }

        if (magazine.getEnabledAsMerchant()) {
            return RegistrationCompleteDto.builder()
                    .flag(false)
                    .message("Magazine named '".concat(magazine.getName()).concat("' is already registered for payments."))
                    .build();
        }

        log.info("Call payment gateway service to check if registration of magazine with merchant id '{}' is completed", magazineRegistrationCompleteDto.getMerchantId());
        final RegistrationCompleteDto registrationCompleteDto = this.restTemplate.getForEntity(Util.HTTPS_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/complete_registration/").concat(magazineRegistrationCompleteDto.getMerchantId()), RegistrationCompleteDto.class).getBody();

        if (registrationCompleteDto == null) {
            log.error("Response for checking registration of magazine with merchant id '{}' from payment gateway is not retrieved properly", magazineRegistrationCompleteDto.getMerchantId());
            return RegistrationCompleteDto.builder()
                    .flag(false)
                    .message("Problem in communication with external payment system. Please, try again later.")
                    .build();
        }

        log.info("Retrieved following response for checking registration of magazine with merchant id '{}' from payment gateway service: flag - {}",
                magazineRegistrationCompleteDto.getMerchantId(),
                registrationCompleteDto.isFlag());


        magazine.setEnabledAsMerchant(registrationCompleteDto.isFlag());
        this.save(magazine);

        if (registrationCompleteDto.isFlag()) {
            log.info("Registration of magazine with id '{}' and given merchant id is '{}' is completed successfully", magazine.getId(), magazineRegistrationCompleteDto.getMerchantId());
        } else {
            log.info("Registration of magazine with id '{}' and given merchant id is '{}' is not completed successfully", magazine.getId(), magazineRegistrationCompleteDto.getMerchantId());
        }

        return RegistrationCompleteDto.builder()
                .flag(registrationCompleteDto.isFlag())
                .message(registrationCompleteDto.isFlag()
                        ? "Registration of magazine named '".concat(magazine.getName()).concat("' completed successfully!")
                        : "Registration of magazine named '".concat(magazine.getName()).concat("' has failed to complete!"))
                .build();
    }
}