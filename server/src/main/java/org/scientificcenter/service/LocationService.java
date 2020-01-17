package org.scientificcenter.service;

import org.scientificcenter.model.Location;

public interface LocationService {

    Location findByLatitudeAndLongitude(Double latitude, Double longitude);

    Location save(Location location);
}
