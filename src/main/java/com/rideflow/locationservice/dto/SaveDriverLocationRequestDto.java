package com.rideflow.locationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveDriverLocationRequestDto {
    @Schema(description = "Redis GEO member identifier. No SQL lookup or driver-existence check. Reusing it moves the stored position.", requiredMode = Schema.RequiredMode.REQUIRED, example = "201")
    String driverId;
    @Schema(description = "Passed as Point.x (Redis longitude) in current implementation: coordinate order is reversed. No Bean Validation.", requiredMode = Schema.RequiredMode.REQUIRED, example = "17.3850")
    Double latitude;
    @Schema(description = "Passed as Point.y (Redis latitude) in current implementation. See GEO issue before interpreting a 5 km radius geographically.", requiredMode = Schema.RequiredMode.REQUIRED, example = "78.4867")
    Double longitude;
}
