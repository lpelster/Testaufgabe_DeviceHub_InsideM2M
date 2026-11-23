package de.lpelster.devicehub.control;

import de.lpelster.devicehub.entity.Device;

import java.util.Collection;
import java.util.Optional;

public interface IDeviceRepo {

    Device createDevice(String name, int defaultInterval);

    Optional<Device> findDeviceById(long id);

    boolean deleteDeviceById(long id);

    Collection<Device> getAllDevices();

    void updateDevice(Device device);
}
