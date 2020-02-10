package org.scientificcenter.service;

import org.scientificcenter.dto.RedirectionResponse;
import org.scientificcenter.dto.RegistrationCompleteDto;
import org.scientificcenter.dto.SubscriptionDto;

public interface SubscriptionService {

    RedirectionResponse subscribe(SubscriptionDto subscriptionRequest);

    RegistrationCompleteDto completeSubscription(String subscriptionId);

    void updateSubscriptionStatus();
}
