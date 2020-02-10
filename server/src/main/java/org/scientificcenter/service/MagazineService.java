package org.scientificcenter.service;

import org.scientificcenter.dto.*;
import org.scientificcenter.model.Magazine;

import java.util.List;

public interface MagazineService {

    Magazine findById(Long id);

    Magazine findByIssn(String issn);

    Magazine findByMerchantId(String merchantId);

    Magazine save(Magazine magazine);

    List<MagazineDto> findByMainEditor(String editor);

    RedirectionResponse registerMagazineAsMerchant(MagazineRegistrationDto magazineRegistrationDto);

    RegistrationCompleteDto completeMagazineRegistration(MagazineRegistrationCompleteDto magazineRegistrationCompleteDto);
}