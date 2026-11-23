package de.lpelster.devicesimulation.entity;

public record DeviceCommand(
        String deviceId,
        String command,
        int intervalSeconds
) {}
