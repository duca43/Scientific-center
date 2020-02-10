package org.scientificcenter.repository;

import org.scientificcenter.model.Subscription;
import org.scientificcenter.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByUser_UsernameAndMagazine_Id(String username, Long magazineId);

    List<Subscription> findAllByStatus(SubscriptionStatus status);

    Subscription findBySubscriptionId(String subscriptionId);
}