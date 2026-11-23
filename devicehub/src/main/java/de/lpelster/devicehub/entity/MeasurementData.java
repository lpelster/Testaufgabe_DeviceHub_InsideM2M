package de.lpelster.devicehub.entity;

import java.time.LocalDateTime;

public record MeasurementData(
        String id,
        long deviceId,
        double value,
        String unit,
        LocalDateTime timestamp
){
}
