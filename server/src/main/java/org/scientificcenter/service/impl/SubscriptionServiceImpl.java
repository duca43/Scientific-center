package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.scientificcenter.dto.MerchantRequest;
import org.scientificcenter.dto.RedirectionResponse;
import org.scientificcenter.dto.RegistrationCompleteDto;
import org.scientificcenter.dto.SubscriptionDto;
import org.scientificcenter.exception.MagazineNotFoundException;
import org.scientificcenter.exception.SubscriptionNotFoundException;
import org.scientificcenter.exception.UserNotFoundException;
import org.scientificcenter.model.Magazine;
import org.scientificcenter.model.Subscription;
import org.scientificcenter.model.SubscriptionStatus;
import org.scientificcenter.model.User;
import org.scientificcenter.repository.SubscriptionRepository;
import org.scientificcenter.service.MagazineService;
import org.scientificcenter.service.SubscriptionService;
import org.scientificcenter.service.UserService;
import org.scientificcenter.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final MagazineService magazineService;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final RestTemplate restTemplate;
    private final Util util;

    @Autowired
    public SubscriptionServiceImpl(final MagazineService magazineService, final UserService userService, final SubscriptionRepository subscriptionRepository, final RestTemplate restTemplate, final Util util) {
        this.magazineService = magazineService;
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
        this.restTemplate = restTemplate;
        this.util = util;
    }

    @Override
    public RedirectionResponse subscribe(final SubscriptionDto subscriptionRequest) {
        log.info("Begin subscription process for user with username '{}' and magazine with id '{}",
                subscriptionRequest.getUsername(),
                subscriptionRequest.getMagazineId());

        final Magazine magazine = this.magazineService.findById(subscriptionRequest.getMagazineId());

        if (magazine == null) {
            log.error("Magazine with id '{}' does not exist", subscriptionRequest.getMagazineId());
            throw new MagazineNotFoundException(subscriptionRequest.getMagazineId());
        }

        final User user = this.userService.findByUsername(subscriptionRequest.getUsername());

        if (user == null) {
            log.error("User with username '{}' does not exist", subscriptionRequest.getUsername());
            throw new UserNotFoundException(subscriptionRequest.getUsername());
        }

        final MerchantRequest merchantRequest = MerchantRequest.builder()
                .name(magazine.getName())
                .merchantId(magazine.getMerchantId())
                .returnUrl(Util.HTTP_PREFIX.concat(this.util.FRONTEND_ADDRESS).concat(":").concat(this.util.FRONTEND_PORT).concat("/my_magazines"))
                .build();

        log.info("Call payment gateway service to create subscription");
        final RedirectionResponse redirectionResponse = this.restTemplate.postForEntity(Util.HTTP_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/subscription"), merchantRequest, RedirectionResponse.class).getBody();
        if (redirectionResponse == null || redirectionResponse.getId() == null || redirectionResponse.getRedirectionUrl() == null) {
            log.error("Response for subscription for user with username '{}' and magazine id '{}' from payment gateway is not retrieved properly",
                    subscriptionRequest.getUsername(),
                    subscriptionRequest.getMagazineId());
            return null;
        }
        log.info("Retrieved following response for subscription for user with username '{}' and magazine id '{}' from payment gateway service: id - {}, redirection url - {}",
                subscriptionRequest.getUsername(),
                subscriptionRequest.getMagazineId(),
                redirectionResponse.getId(),
                redirectionResponse.getRedirectionUrl());

        final Subscription subscription = Subscription.builder()
                .subscriptionId(redirectionResponse.getId())
                .magazine(magazine)
                .user(user)
                .status(SubscriptionStatus.APPROVAL_PENDING)
                .build();

        this.subscriptionRepository.save(subscription);
        log.info("Subscription with subscription id '{}' is saved successfully", subscription.getSubscriptionId());

        return redirectionResponse;
    }

    @Override
    public RegistrationCompleteDto completeSubscription(final String subscriptionId) {
        Assert.notNull(subscriptionId, "Subscription id can't be null!");

        log.info("Completing subscription with subscription id '{}'", subscriptionId);

        final Subscription subscription = this.subscriptionRepository.findBySubscriptionId(subscriptionId);

        if (subscription == null) {
            log.error("Subscription with subscription id '{}' does not exist", subscriptionId);
            throw new SubscriptionNotFoundException(subscriptionId);
        }

        log.info("Call payment gateway service to check if subscription with subscription id '{}' is completed", subscriptionId);
        final SubscriptionStatus subscriptionStatus = this.restTemplate.getForEntity(Util.HTTP_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/subscription/").concat(subscriptionId), SubscriptionStatus.class).getBody();
        log.info("Retrieved following response for checking subscription with subscription id '{}' from payment gateway service: status - {}",
                subscriptionId,
                subscriptionStatus);

        if (subscriptionStatus == null) {
            log.error("Response for checking subscription with subscription id '{}' from payment gateway is not retrieved properly", subscriptionId);
            return RegistrationCompleteDto.builder().flag(false).build();
        }

        subscription.setStatus(subscriptionStatus);
        this.subscriptionRepository.save(subscription);
        log.info("Status of subscription with subscription id '{}' is updated to: {}", subscriptionId, subscriptionStatus.name());

        return RegistrationCompleteDto.builder()
                .flag(subscriptionStatus.equals(SubscriptionStatus.ACTIVE))
                .build();
    }

    @Override
//    @Scheduled(initialDelayString = "${scientific-center.scheduling-initial-delay}", fixedDelayString = "${scientific-center.scheduling-fixed-delay}")
    public void updateSubscriptionStatus() {
        this.updateSubscriptionStatus(SubscriptionStatus.APPROVAL_PENDING);
        this.updateSubscriptionStatus(SubscriptionStatus.ACTIVE);
    }

    private void updateSubscriptionStatus(final SubscriptionStatus subscriptionStatusToCheck) {
        log.info("Checking subscription transactions with status {}...", subscriptionStatusToCheck.name());

        final List<Subscription> subscriptions = this.subscriptionRepository.findAllByStatus(subscriptionStatusToCheck);
        subscriptions.forEach(subscription -> {

            log.info("Sending request to payment gateway service to retrieve status of subscription with subscription id {}", subscription.getSubscriptionId());
            final SubscriptionStatus subscriptionStatus = this.restTemplate.getForEntity(Util.HTTPS_PREFIX.concat(this.util.SERVER_ADDRESS).concat(":").concat(this.util.GATEWAY_PORT).concat("/subscription/").concat(subscription.getSubscriptionId()), SubscriptionStatus.class).getBody();
            log.info("Response from payment gateway service for subscription with subscription id {}: status - {}", subscription.getSubscriptionId(), subscriptionStatus);

            if (subscriptionStatus == null) return;

            if (subscription.getStatus() != subscriptionStatus) {
                final SubscriptionStatus subscriptionStatusToBeChanged = subscription.getStatus();
                log.info("Update status of subscription with subscription id '{}' from {} to {}", subscription.getSubscriptionId(), subscriptionStatusToBeChanged.name(), subscriptionStatus.name());
                subscription.setStatus(subscriptionStatus);
                this.subscriptionRepository.save(subscription);
                log.info("Status of subscription with subscription id '{}' is successfully updated from {} to {}", subscription.getSubscriptionId(), subscriptionStatusToBeChanged.name(), subscriptionStatus.name());
            }
        });

        log.info("Checking subscription transactions with status {} is completed...", subscriptionStatusToCheck.name());
    }
}