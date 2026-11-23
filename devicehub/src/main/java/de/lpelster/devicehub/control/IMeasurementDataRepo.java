package de.lpelster.devicehub.control;

import de.lpelster.devicehub.entity.MeasurementData;

import java.util.List;
import java.util.Optional;

public interface IMeasurementDataRepo {

    void save(MeasurementData measurement);

    Optional<MeasurementData> findById(String id);

    List<MeasurementData> findAll();
}
