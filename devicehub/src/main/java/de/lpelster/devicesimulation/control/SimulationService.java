package de.lpelster.devicesimulation.control;

import de.lpelster.devicesimulation.entity.SimulatedDevice;
import de.lpelster.devicesimulation.gateway.DeviceHubClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.*;

@ApplicationScoped
public class SimulationService {

    private final Map<Long, SimulatedDevice> devices = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    private final DeviceHubClient client = new DeviceHubClient(this::changeInterval);

    public void start() {
        for (int i = 1; i <= 5; i++) {
            SimulatedDevice device = new SimulatedDevice("Temperatursensor-" + i, 5);
            client.connect(device);
            schedule(device);
        }
    }

    private void schedule(SimulatedDevice device) {
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            if (device.getId() == 0 || !device.isConnected()) {
                return;
            }
            devices.put(device.getId(), device);
            client.sendMeasurement(device);
        }, 1, device.getIntervalSeconds(), TimeUnit.SECONDS);

        tasks.put(device.getName(), task);
    }

    private void changeInterval(SimulatedDevice device, int seconds) {
        if (seconds < 1) {
            return;
        }
        device.setIntervalSeconds(seconds);
        tasks.get(device.getName()).cancel(false);
        schedule(device);
    }

    public Map<Long, SimulatedDevice> getDevices() {
        return devices;
    }
}