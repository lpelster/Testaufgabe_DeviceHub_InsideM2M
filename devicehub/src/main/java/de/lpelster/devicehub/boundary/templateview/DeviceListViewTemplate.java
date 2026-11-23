package de.lpelster.devicehub.boundary.templateview;

import de.lpelster.devicehub.control.IDeviceService;
import de.lpelster.devicehub.boundary.dtos.DeviceDto;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;

@Path("view/devices")
@RequestScoped
public class DeviceListViewTemplate {

    private final IDeviceService service;

    @Inject
    public DeviceListViewTemplate(IDeviceService service){
        this.service = service;
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance devices(
                Collection<DeviceDto> devices
        );
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getDevices() {
        return Templates.devices(service.getAllDevices());
    }

    @PUT
    @Path("/{id}/interval")
    public void updateInterval(@PathParam("id") long id, @QueryParam("seconds") int seconds
    ) {
        service.updateIntervalByDeviceId(id, seconds);
    }
}
