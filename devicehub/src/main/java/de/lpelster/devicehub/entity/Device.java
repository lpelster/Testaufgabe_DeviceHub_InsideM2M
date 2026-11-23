package de.lpelster.devicehub.entity;

import de.lpelster.devicehub.shared.StateDevice;
import jakarta.enterprise.inject.Vetoed;

@Vetoed
public class Device {
        private final long id;
        private final String name;
        StateDevice stateDevice;

        private int intervalSeconds;
        private MeasurementData latestMeasurement;

        public Device(long id, String name, int intervalSeconds, StateDevice stateDevice) {
                this.id = id;
                this.name = name;
                this.intervalSeconds = intervalSeconds;
                this.stateDevice = stateDevice;
        }

        public long getId() {
                return id;
        }

        public MeasurementData getLatestMeasurement() {
                return latestMeasurement;
        }

        public void setLatestMeasurement(MeasurementData latestMeasurement) {
                this.latestMeasurement = latestMeasurement;
        }

        public int getIntervalSeconds() {
                return intervalSeconds;
        }

        public void setIntervalSeconds(int intervalSeconds) {
                this.intervalSeconds = intervalSeconds;
        }

        public StateDevice getStateDevice() {
                return stateDevice;
        }

        public void setStateDevice(StateDevice stateDevice) {
                this.stateDevice = stateDevice;
        }

        public String getName() {
                return name;
        }
}