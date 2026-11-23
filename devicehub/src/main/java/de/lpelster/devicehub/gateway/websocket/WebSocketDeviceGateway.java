package de.lpelster.devicehub.gateway.websocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.lpelster.devicehub.control.IDeviceSocketGateway;
import de.lpelster.devicehub.entity.DeviceCommand;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WebSocketDeviceGateway implements IDeviceSocketGateway {

    private ObjectMapper mapper;
    private Map<Long, WebSocketConnection> connectionByDevice = new ConcurrentHashMap<>();;

    @Inject
    public WebSocketDeviceGateway(ObjectMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public void register(long deviceId, WebSocketConnection connection) {
        connectionByDevice.put(deviceId, connection);
    }
    @Override
    public Optional<Long> unregister(WebSocketConnection connection) {
        return connectionByDevice.entrySet().stream()
                .filter(e -> e.getValue().id().equals(connection.id()))
                .map(Map.Entry::getKey)
                .findFirst()
                .filter(id -> connectionByDevice.remove(id) != null);
    }

    @Override
    public boolean sendConfig(long deviceId, int intervalSeconds, String command)
    {
        WebSocketConnection connection = connectionByDevice.get(deviceId);

        if (connection == null || !connection.isOpen()) {
            return false;
        }

        DeviceCommand deviceCommand = new DeviceCommand(
                deviceId,
                command,
                intervalSeconds
        );
        try {

            String json = mapper.writeValueAsString(deviceCommand);
            connection.sendTextAndAwait(json);
            return true;

        } catch (JsonProcessingException | RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
}
