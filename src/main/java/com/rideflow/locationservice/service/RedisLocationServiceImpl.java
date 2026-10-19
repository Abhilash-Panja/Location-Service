package com.rideflow.locationservice.service;

import com.rideflow.locationservice.dto.DriverLocationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisLocationServiceImpl implements LocationService {

    private static final String DRIVER_GEO_OPS_KEY = "drivers";

    private final StringRedisTemplate stringRedisTemplate;
    private final double searchRadiusKm;

    public RedisLocationServiceImpl(
            StringRedisTemplate stringRedisTemplate,
            @Value("${location.search-radius-km:5}") double searchRadiusKm) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.searchRadiusKm = searchRadiusKm;
    }

    @Override
    public Boolean saveDriverLocation(String driverId, Double latitude, Double longitude) {
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();

        // Redis expects Point.x = longitude and Point.y = latitude.
        geoOps.add(
                DRIVER_GEO_OPS_KEY,
                new RedisGeoCommands.GeoLocation<>(
                        driverId,
                        new Point(longitude, latitude)));
        return true;
    }

    @Override
    public List<DriverLocationDto> getNearByDrivers(Double latitude, Double longitude) {
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        Distance radius = new Distance(searchRadiusKm, Metrics.KILOMETERS);
        Circle within = new Circle(new Point(longitude, latitude), radius);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                geoOps.radius(DRIVER_GEO_OPS_KEY, within);
        List<DriverLocationDto> drivers = new ArrayList<>();

        if (results == null) {
            return drivers;
        }

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            List<Point> positions =
                    geoOps.position(DRIVER_GEO_OPS_KEY, result.getContent().getName());
            if (positions == null || positions.isEmpty() || positions.get(0) == null) {
                continue;
            }

            Point point = positions.get(0);
            drivers.add(DriverLocationDto.builder()
                    .driverId(result.getContent().getName())
                    .latitude(point.getY())
                    .longitude(point.getX())
                    .build());
        }
        return drivers;
    }
}
