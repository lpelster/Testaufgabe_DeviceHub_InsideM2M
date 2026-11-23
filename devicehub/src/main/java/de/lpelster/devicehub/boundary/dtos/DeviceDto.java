package de.lpelster.devicehub.boundary.dtos;

import de.lpelster.devicehub.shared.StateDevice;

public record DeviceDto(
        long id,
        String name,
        StateDevice stateDevice,
        int intervalSeconds,
        MeasurementDataDto latestMeasurement
) {
}
