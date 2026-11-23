package de.lpelster.devicehub.control;

import de.lpelster.devicehub.boundary.dtos.DeviceDto;
import de.lpelster.devicehub.boundary.dtos.MeasurementDataDto;
import io.quarkus.websockets.next.WebSocketConnection;

import java.util.Collection;
import java.util.Optional;

public interface IDeviceService {

    Optional<DeviceDto> getDeviceById(long id);

    boolean unregisterDevice(long id, WebSocketConnection connection);

    void markDeviceOffline(WebSocketConnection connection);

    Collection<DeviceDto> getAllDevices();

    long registerNewDevice(String name, WebSocketConnection connection);

    void receiveMeasurementByDeviceId(long id, MeasurementDataDto measurementDataDto);

    void updateIntervalByDeviceId(long deviceId, int intervalSeconds);
}
