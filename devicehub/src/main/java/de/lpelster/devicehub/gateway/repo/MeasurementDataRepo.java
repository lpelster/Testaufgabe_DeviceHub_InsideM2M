package de.lpelster.devicehub.gateway.repo;
import de.lpelster.devicehub.control.IMeasurementDataRepo;
import de.lpelster.devicehub.entity.MeasurementData;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class MeasurementDataRepo implements IMeasurementDataRepo {

    private static final int MAX_MEASUREMENTS = 100;

    private final Map<String, MeasurementData> mDatas = new LinkedHashMap<>();

    @Override
    public synchronized void save(MeasurementData measurement) {

        mDatas.put(measurement.id(), measurement);

        if (mDatas.size() > MAX_MEASUREMENTS) {
            String oldestId = mDatas.keySet().iterator().next();
            mDatas.remove(oldestId);
        }
        printAllMeasurements();
    }
    @Override
    public synchronized Optional<MeasurementData> findById(String id) {
        return Optional.ofNullable(mDatas.get(id));
    }

    @Override
    public synchronized List<MeasurementData> findAll() {
        return List.copyOf(mDatas.values());
    }

    //
    private void printAllMeasurements() {
        System.out.println("All Current MeasurementData: ");

        for (MeasurementData measurement : mDatas.values()) {
            System.out.println(measurement);
        }

        System.out.println("Anzahl: " + mDatas.size());
        System.out.println("-----------");
    }
}
