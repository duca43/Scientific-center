package org.scientificcenter.repository;

import org.scientificcenter.model.MerchantOrderStatus;
import org.scientificcenter.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByMerchantOrderId(String merchantOrderId);

    List<Payment> findAllByStatus(MerchantOrderStatus merchantOrderStatus);

    Payment findByUser_UsernameAndMagazine_Id(String username, Long magazineId);

    List<Payment> findAllByUser_UsernameAndMagazineNotNull(String username);
}