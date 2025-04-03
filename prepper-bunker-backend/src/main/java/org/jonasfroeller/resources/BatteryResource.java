package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.BatteryCreateDTO;
import org.jonasfroeller.dtos.BatteryDTO;
import org.jonasfroeller.models.Battery;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.BatteryRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/batteries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BatteryResource {

    @Inject
    BatteryRepository repository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all batteries")
    @APIResponse(responseCode = "200", description = "List of all batteries")
    public List<BatteryDTO> getAllBatteries() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get battery by ID")
    @APIResponse(responseCode = "200", description = "The battery with the given ID")
    @APIResponse(responseCode = "404", description = "Battery not found")
    public Response getBatteryById(@PathParam("id") Long id) {
        Battery battery = repository.findById(id);
        if (battery == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Battery not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(battery)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get batteries by storage location")
    @APIResponse(responseCode = "200", description = "List of batteries at the specified location")
    public List<BatteryDTO> getBatteriesByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/by-type/{type}")
    @Operation(summary = "Get batteries by type")
    @APIResponse(responseCode = "200", description = "List of batteries of the specified type")
    public List<BatteryDTO> getBatteriesByType(@PathParam("type") String type) {
        return repository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/total-by-type/{type}")
    @Operation(summary = "Get total quantity of a battery type")
    @APIResponse(responseCode = "200", description = "Total quantity of the specified battery type")
    public Response getTotalQuantityByType(@PathParam("type") String type) {
        int totalQuantity = repository.getTotalQuantityByType(type);

        return Response.ok(new TotalQuantityResponse(type, totalQuantity)).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new battery")
    @APIResponse(responseCode = "201", description = "Battery created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createBattery(@Valid BatteryCreateDTO batteryDTO) {
        StorageLocation storageLocation = storageLocationRepository.findById(batteryDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + batteryDTO.storageLocationId))
                    .build();
        }

        Battery battery = new Battery();
        battery.type = batteryDTO.type;
        battery.capacity = batteryDTO.capacity;
        battery.quantity = batteryDTO.quantity;
        battery.storageLocation = storageLocation;

        repository.persist(battery);

        notificationService.notifyResourceCreated("Battery", battery.id,
                battery.type + " " + battery.capacity + "Ah (Qty: " + battery.quantity + ")");

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(battery))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a battery")
    @APIResponse(responseCode = "200", description = "Battery updated")
    @APIResponse(responseCode = "404", description = "Battery not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateBattery(@PathParam("id") Long id, @Valid BatteryCreateDTO batteryDTO) {
        Battery battery = repository.findById(id);
        if (battery == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Battery not found with ID: " + id))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(batteryDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + batteryDTO.storageLocationId))
                    .build();
        }

        battery.type = batteryDTO.type;
        battery.capacity = batteryDTO.capacity;
        battery.quantity = batteryDTO.quantity;
        battery.storageLocation = storageLocation;

        repository.persist(battery);

        notificationService.notifyResourceUpdated("Battery", battery.id,
                battery.type + " " + battery.capacity + "Ah (Qty: " + battery.quantity + ")");

        return Response.ok(mapToDTO(battery)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a battery")
    @APIResponse(responseCode = "204", description = "Battery deleted")
    @APIResponse(responseCode = "404", description = "Battery not found")
    public Response deleteBattery(@PathParam("id") Long id) {
        Battery battery = repository.findById(id);
        if (battery == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Battery not found with ID: " + id))
                    .build();
        }

        String batteryDescription = battery.type + " " + battery.capacity + "Ah (Qty: " + battery.quantity + ")";

        repository.delete(battery);

        notificationService.notifyResourceDeleted("Battery", id, batteryDescription);

        return Response.noContent().build();
    }

    private BatteryDTO mapToDTO(Battery battery) {
        return new BatteryDTO(
                battery.id,
                battery.type,
                battery.capacity,
                battery.quantity,
                battery.storageLocation.id,
                battery.storageLocation.name
        );
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public ErrorResponse() {
        }
    }

    public static class TotalQuantityResponse {
        public String batteryType;
        public int totalQuantity;

        public TotalQuantityResponse(String batteryType, int totalQuantity) {
            this.batteryType = batteryType;
            this.totalQuantity = totalQuantity;
        }

        public TotalQuantityResponse() {
        }
    }
}