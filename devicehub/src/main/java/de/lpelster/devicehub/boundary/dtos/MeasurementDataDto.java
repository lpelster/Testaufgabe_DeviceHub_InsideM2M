package de.lpelster.devicehub.boundary.dtos;

import java.time.LocalDateTime;

public record MeasurementDataDto
        (
            String id,
            long deviceId,
            double value,
            String unit,
            LocalDateTime timestamp
        ){
}
