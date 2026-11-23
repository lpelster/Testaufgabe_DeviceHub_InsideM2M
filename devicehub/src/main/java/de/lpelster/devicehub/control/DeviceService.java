package de.lpelster.devicehub.control;

import de.lpelster.devicehub.boundary.dtos.DeviceDto;
import de.lpelster.devicehub.boundary.dtos.MeasurementDataDto;
import de.lpelster.devicehub.entity.Device;
import de.lpelster.devicehub.entity.MeasurementData;
import de.lpelster.devicehub.shared.StateDevice;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class DeviceService implements IDeviceService {

    private final IDeviceRepo repo;
    private final IMeasurementDataRepo dataRepo;
    private final IDeviceSocketGateway deviceGateway;

    private final Converter converterDto;

    private int defaultIntervalSec = 5;

    @Inject
    public DeviceService(IDeviceRepo repo, IMeasurementDataRepo measurementDataRepo,
                         IDeviceSocketGateway deviceGateway, Converter converter){
        this.repo = repo;
        this.dataRepo = measurementDataRepo;
        this.deviceGateway = deviceGateway;
        this.converterDto = converter;
    }

    @Override
    public Optional<DeviceDto> getDeviceById(long id) {

        DeviceDto deviceDto = repo.findDeviceById(id)
                .map(converterDto::deviceToDto)
                .orElseThrow(() -> new NotFoundException("Device not found" + id));

        return Optional.of(deviceDto);
    }

    @Override
    public Collection<DeviceDto> getAllDevices() {
        return this.repo.getAllDevices()
                .stream()
                .map(converterDto::deviceToDto)
                .toList();
    }

    @Override
    public long registerNewDevice(String name, WebSocketConnection connection) {
        Device device = repo.getAllDevices().stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElseGet(() -> repo.createDevice(name, this.defaultIntervalSec));

        device.setStateDevice(StateDevice.ONLINE);
        this.deviceGateway.register(device.getId(), connection);

        return device.getId();
    }

    @Override
    public void markDeviceOffline(WebSocketConnection connection) {
        deviceGateway.unregister(connection)
                .flatMap(repo::findDeviceById)
                .ifPresent(device -> device.setStateDevice(StateDevice.OFFLINE));
    }

    @Override
    public boolean unregisterDevice(long id, WebSocketConnection connection){
        this.deviceGateway.unregister(connection);
        return this.repo.deleteDeviceById(id);
    }

    @Override
    public void receiveMeasurementByDeviceId(long id, MeasurementDataDto mDataDto) {
        if (mDataDto.id() == null) {
            throw new BadRequestException("Measurement Data ID missing");
        }
        if (id != mDataDto.deviceId()) {
            throw new BadRequestException("Device-ID does not match Measurement Device-ID");
        }

        Optional<Device> device = this.repo.findDeviceById(id);

        if(device.isPresent()){
            MeasurementData measurementData = this.converterDto.measurementToEntity(mDataDto);
            this.dataRepo.save(measurementData);

            //Device hat immer aktuellen Wert// auch wenn Offline
            device.get().setLatestMeasurement(measurementData);
        }
    }

    @Override
    public void updateIntervalByDeviceId(long deviceId, int intervalSeconds) {
        if (intervalSeconds < 1) {
            throw new BadRequestException("Intervall muss mindestens 1 sein");
        }

        Device device = repo.findDeviceById(deviceId)
                .orElseThrow(() -> new NotFoundException(
                        "Device not found: " + deviceId
                ));

        device.setIntervalSeconds(intervalSeconds);

        deviceGateway.sendConfig(
                deviceId,
                intervalSeconds,
                "UPDATE_INTERVAL"
        );
    }
}
