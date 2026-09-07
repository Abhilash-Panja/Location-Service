package com.rideflow.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversRequestDto {
    @Schema(description = "Search Point.x in current implementation; no @Valid or range checks.", requiredMode = Schema.RequiredMode.REQUIRED, example = "17.3850")
    Double latitude;
    @Schema(description = "Search Point.y in current implementation. Radius fixed to 5 km in code; no radius query parameter.", requiredMode = Schema.RequiredMode.REQUIRED, example = "78.4867")
    Double longitude;
}