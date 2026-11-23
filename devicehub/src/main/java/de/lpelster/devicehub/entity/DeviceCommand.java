package de.lpelster.devicehub.entity;

import jakarta.enterprise.inject.Vetoed;

@Vetoed
public record DeviceCommand(
        long deviceId,
        String command,
        int intervalSeconds
) {
}
