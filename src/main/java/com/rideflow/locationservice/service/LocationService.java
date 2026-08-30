package com.rideflow.locationservice.service;

import com.rideflow.locationservice.dto.DriverLocationDto;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(String driverId, Double latitude, Double Longitude);

    List<DriverLocationDto> getNearByDrivers(Double latitude, Double Longitude);

}