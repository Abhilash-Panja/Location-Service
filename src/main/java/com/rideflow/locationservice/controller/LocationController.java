package com.rideflow.locationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.rideflow.locationservice.dto.DriverLocationDto;
import com.rideflow.locationservice.dto.NearbyDriversRequestDto;
import com.rideflow.locationservice.dto.SaveDriverLocationRequestDto;
import com.rideflow.locationservice.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Driver locations")
@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(operationId = "LocationService_saveDriverLocation", summary = "Save or move a driver GEO member",
            description = "Seeds Redis before a nearby-driver lookup; repeats update the same member location. LocationController.saveDriverLocation -> RedisLocationServiceImpl.saveDriverLocation -> StringRedisTemplate.opsForGeo().add on key drivers. Controller catches all service exceptions. The service passes latitude as Point.x and longitude as Point.y. Redis interprets x as longitude and y as latitude. Both write and search repeat this swap, so a same-coordinate match does not prove geographic correctness.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.rideflow.locationservice.dto.SaveDriverLocationRequestDto.class),
                    examples = @ExampleObject(value = "{\n  \"driverId\": \"201\",\n  \"latitude\": 17.385,\n  \"longitude\": 78.4867\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Save or move a driver GEO member completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class), examples = @ExampleObject(value = "true"))),
            @ApiResponse(responseCode = "400", description = "Request JSON cannot be deserialized before controller invocation; framework body.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Caught location-service exception: literal JSON false.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class), examples = @ExampleObject(value = "false")))
    })
    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto) {
        try {
            Boolean response = locationService.saveDriverLocation(saveDriverLocationRequestDto.getDriverId(), saveDriverLocationRequestDto.getLatitude(), saveDriverLocationRequestDto.getLongitude());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Temporary diagnostic for local testing
            return new ResponseEntity<>(
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @Operation(operationId = "LocationService_getNearbyDrivers", summary = "Find GEO members within the fixed search radius",
            description = "Confirms Location-Service can read the Redis data that Booking-Service requests asynchronously. LocationController.getNearbyDrivers -> RedisLocationServiceImpl.getNearByDrivers -> GEO radius search on drivers with fixed 5 km -> position lookup for each member -> DriverLocationDto list. No SQL driver validation, availability check, radius query parameter, or guaranteed ordering. Returned latitude comes from Point.x and longitude from Point.y, mirroring the storage swap.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "JSON body is required by Spring MVC. Field descriptions distinguish service requirements from active validation.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.rideflow.locationservice.dto.NearbyDriversRequestDto.class),
                    examples = @ExampleObject(value = "{\n  \"latitude\": 17.385,\n  \"longitude\": 78.4867\n}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Find GEO members within the fixed search radius completed on the controller success branch.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.rideflow.locationservice.dto.DriverLocationDto.class)), examples = @ExampleObject(value = "[{\"driverId\":\"201\",\"latitude\":17.385,\"longitude\":78.4867}]"))),
            @ApiResponse(responseCode = "400", description = "Unreadable request JSON before controller invocation; framework body.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Caught service exception: literal empty JSON array [].",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = com.rideflow.locationservice.dto.DriverLocationDto.class)), examples = @ExampleObject(value = "[]")))
    })
    @PostMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> getNearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto) {
        try {
            List<DriverLocationDto> drivers = locationService.getNearByDrivers(nearbyDriversRequestDto.getLatitude(), nearbyDriversRequestDto.getLongitude());
            return new ResponseEntity<>(drivers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }
}
