package de.lpelster.devicehub.gateway.repo;

import de.lpelster.devicehub.control.IDeviceRepo;
import de.lpelster.devicehub.entity.Device;
import de.lpelster.devicehub.shared.StateDevice;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class DeviceRepo implements IDeviceRepo {

    private final ConcurrentHashMap<Long, Device> devices = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Inject
    public DeviceRepo(){
    }

    @Override
    public Device createDevice(String name, int defaultInterval) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cant be null");
        }

        long id = this.getNewDeviceId();

        Device newDevice = new Device(
                id,
                name,
                defaultInterval,
                StateDevice.ONLINE
        );
        devices.put(id, newDevice);
        return newDevice;
    }

    @Override
    public Optional<Device> findDeviceById(long id) {
        return Optional.ofNullable(devices.get(id));
    }

    @Override
    public boolean deleteDeviceById(long id) {
        try {
            return devices.remove(id) != null;
        } catch (Exception e) {
            throw new IllegalStateException("Error while deleting Device: " + id, e);
        }
    }

    @Override
    public Collection<Device> getAllDevices() {
        return devices
                .values()
                .stream()
                .toList();
    }

    @Override
    public void updateDevice(Device device) {
        devices.replace(device.getId(), device);
    }

    private long getNewDeviceId(){
        return nextId.getAndIncrement();
    }
}
