package org.scientificcenter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.scientificcenter.model.Location;
import org.scientificcenter.repository.LocationRepository;
import org.scientificcenter.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.stream.Stream;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(final LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location findByLatitudeAndLongitude(final Double latitude, final Double longitude) {
        Assert.noNullElements(Stream.of(latitude, longitude).toArray(), "Both latitude and longitude must be set!");
        return this.locationRepository.findByLatitudeAndLongitude(latitude, longitude);
    }

    @Override
    public Location save(final Location location) {
        Assert.notNull(location, "Location can't be null!");
        Assert.noNullElements(Stream.of(location.getLatitude(), location.getLongitude()).toArray(),
                "Both latitude and longitude must be set!");

        final Location locationTmp = this.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude());

        if (locationTmp != null) {
            LocationServiceImpl.log.info("Location with latitude '{}' and longitude '{}' already exists", location.getLatitude(), location.getLongitude());
            return locationTmp;
        }

        return this.locationRepository.save(location);
    }
}