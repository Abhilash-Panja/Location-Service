package com.rideflow.locationservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "RideFlow Location API",
        version = "0.0.1-SNAPSHOT",
        description = "Redis driver GEO storage and fixed-radius lookup. The existing implementation passes latitude as Point.x and longitude as Point.y, reversing Redis coordinate semantics. Responses document current behavior."
))
public class OpenApiConfig {
}
