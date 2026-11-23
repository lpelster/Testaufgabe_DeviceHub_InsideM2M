package de.lpelster.devicesimulation.entity;

public record MeasurementPayload(
        String id,
        String deviceId,
        double value,
        String unit,
        String timestamp
) {}
