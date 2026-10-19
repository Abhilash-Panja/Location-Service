package com.rideflow.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversRequestDto {
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Schema(description = "Latitude in degrees.", requiredMode = Schema.RequiredMode.REQUIRED, example = "17.3850")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Schema(description = "Longitude in degrees.", requiredMode = Schema.RequiredMode.REQUIRED, example = "78.4867")
    private Double longitude;
}
