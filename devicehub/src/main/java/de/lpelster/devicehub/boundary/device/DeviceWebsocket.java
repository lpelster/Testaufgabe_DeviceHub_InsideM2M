package de.lpelster.devicehub.boundary.device;
import de.lpelster.devicehub.control.IDeviceService;
import de.lpelster.devicehub.boundary.dtos.MeasurementDataDto;
import io.quarkus.websockets.next.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

@WebSocket(path = "/ws/device/{name}")
@ApplicationScoped
public class DeviceWebsocket {

    private final IDeviceService service;

    @Inject
    public DeviceWebsocket(IDeviceService service){
        this.service = service;
    }

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        long id = service.registerNewDevice(connection.pathParam("name"), connection);
        connection.sendTextAndAwait(String.valueOf(id));
    }

    @OnTextMessage
    public void onMessage(MeasurementDataDto measurementDataDto) {
        try {
            long id = Long.parseLong(String.valueOf(measurementDataDto.deviceId()));

            service.receiveMeasurementByDeviceId(id, measurementDataDto);

        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    "Invalid device ID: " + measurementDataDto.deviceId()
            );
        }
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        service.markDeviceOffline(connection);
    }
}
