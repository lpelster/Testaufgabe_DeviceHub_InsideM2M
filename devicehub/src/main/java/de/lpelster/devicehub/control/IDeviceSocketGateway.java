package de.lpelster.devicehub.control;

import io.quarkus.websockets.next.WebSocketConnection;

import java.util.Optional;

public interface IDeviceSocketGateway {

    void register(long deviceId, WebSocketConnection connection);

    Optional<Long> unregister(WebSocketConnection connection);

    boolean sendConfig(long deviceId, int intervalSeconds, String command);
}
