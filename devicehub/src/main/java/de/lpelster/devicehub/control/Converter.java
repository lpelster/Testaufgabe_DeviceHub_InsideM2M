package de.lpelster.devicehub.control;

import de.lpelster.devicehub.boundary.dtos.DeviceDto;
import de.lpelster.devicehub.boundary.dtos.MeasurementDataDto;
import de.lpelster.devicehub.entity.Device;
import de.lpelster.devicehub.entity.MeasurementData;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Converter {

    protected DeviceDto deviceToDto(Device device) {
        try {
            return new DeviceDto(
                    device.getId(),
                    device.getName(),
                    device.getStateDevice(),
                    device.getIntervalSeconds(),
                    measurementToDto(device.getLatestMeasurement())
            );
        } catch (Exception e) {
            throw new IllegalStateException("Error while converting Device", e);
        }
    }

    protected MeasurementDataDto measurementToDto(MeasurementData measurement) {
        if (measurement == null) {
            return null;
        }

        try {
            return new MeasurementDataDto(
                    measurement.id(),
                    measurement.deviceId(),
                    measurement.value(),
                    measurement.unit(),
                    measurement.timestamp()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Error while converting MeasurementData to DTO", e);
        }
    }

    protected MeasurementData measurementToEntity(MeasurementDataDto dto) {
        if (dto == null) {
            return null;
        }

        try {
            return new MeasurementData(
                    dto.id(),
                    dto.deviceId(),
                    dto.value(),
                    dto.unit(),
                    dto.timestamp()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Error while converting DTO to MeasurementData", e);
        }
    }
}
