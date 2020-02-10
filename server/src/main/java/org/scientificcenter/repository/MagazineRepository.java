package org.scientificcenter.repository;

import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    Magazine findByIssn(String issn);

    List<Magazine> findAllByMainEditor(User mainEditor);

    Magazine findByMerchantId(String merchantId);
}