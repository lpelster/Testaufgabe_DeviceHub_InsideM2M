package de.lpelster.devicesimulation.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.lpelster.devicehub.entity.DeviceCommand;
import de.lpelster.devicesimulation.entity.MeasurementPayload;
import de.lpelster.devicesimulation.entity.SimulatedDevice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.function.BiConsumer;

public class DeviceHubClient {

    private static final String HUB_URL = "ws://localhost:8080/ws/device/";
    private static final int RETRY_SECONDS = 3;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService retry = Executors.newSingleThreadScheduledExecutor();

    private final BiConsumer<SimulatedDevice, Integer> onIntervalChange;

    public DeviceHubClient(BiConsumer<SimulatedDevice, Integer> onIntervalChange) {
        this.onIntervalChange = onIntervalChange;
    }
    public void connect(SimulatedDevice device) {
        httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(HUB_URL + device.getName()), new HubListener(device))
                .exceptionally(e -> {
                    reconnectLater(device);
                    return null;
                });
    }

    private void reconnectLater(SimulatedDevice device) {
        device.setConnected(false);
        System.out.println(device.getName() + " nicht verbunden, neuer Versuch in "
                + RETRY_SECONDS + " s");
        retry.schedule(() -> connect(device), RETRY_SECONDS, TimeUnit.SECONDS);
    }

    public void sendMeasurement(SimulatedDevice device) {
        if (!device.isConnected() || device.getId() == 0) {
            return;
        }
        try {
            MeasurementPayload measurement = new MeasurementPayload(
                    UUID.randomUUID().toString(),
                    String.valueOf(device.getId()),
                    20 + Math.random() * 10,
                    "°C",
                    java.time.LocalDateTime.now().toString()
            );
            device.getSocket().sendText(objectMapper.writeValueAsString(measurement), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class HubListener implements WebSocket.Listener {

        private final SimulatedDevice device;
        private boolean idReceived = false; // pro Verbindung neu

        HubListener(SimulatedDevice device) {
            this.device = device;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            device.setSocket(webSocket);
            device.setConnected(true);
            System.out.println(device.getName() + " verbunden.");
            webSocket.request(1);
        }

        @Override
        public CompletableFuture<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                String message = data.toString();

                if (!idReceived) {
                    device.setId(Long.parseLong(message));
                    idReceived = true;
                    System.out.println(device.getName() + " erhielt ID: " + device.getId());
                } else {
                    handleCommand(objectMapper.readValue(message, DeviceCommand.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            reconnectLater(device);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            reconnectLater(device);
        }

        private void handleCommand(DeviceCommand command) {
            if ("UPDATE_INTERVAL".equals(command.command())) {
                onIntervalChange.accept(device, command.intervalSeconds());
                System.out.println(device.getName() + ": neues Intervall "
                        + command.intervalSeconds() + " s");
            }
        }
    }
}