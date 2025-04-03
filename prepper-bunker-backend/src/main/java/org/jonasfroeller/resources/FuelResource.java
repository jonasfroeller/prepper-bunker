package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.FuelCreateDTO;
import org.jonasfroeller.dtos.FuelDTO;
import org.jonasfroeller.models.Fuel;
import org.jonasfroeller.models.FuelType;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.FuelRepository;
import org.jonasfroeller.repositories.FuelTypeRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/fuel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuelResource {

    @Inject
    FuelRepository repository;

    @Inject
    FuelTypeRepository fuelTypeRepository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all fuel records")
    @APIResponse(responseCode = "200", description = "List of all fuel records")
    public List<FuelDTO> getAllFuel() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get fuel record by ID")
    @APIResponse(responseCode = "200", description = "The fuel record with the given ID")
    @APIResponse(responseCode = "404", description = "Fuel record not found")
    public Response getFuelById(@PathParam("id") Long id) {
        Fuel fuel = repository.findById(id);
        if (fuel == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel record not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(fuel)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get fuel records by storage location")
    @APIResponse(responseCode = "200", description = "List of fuel records at the specified location")
    public List<FuelDTO> getFuelByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/by-type/{typeId}")
    @Operation(summary = "Get fuel records by fuel type")
    @APIResponse(responseCode = "200", description = "List of fuel records of the specified type")
    public List<FuelDTO> getFuelByType(@PathParam("typeId") Long typeId) {
        return repository.findByFuelType(typeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/total-by-type/{typeId}")
    @Operation(summary = "Get total quantity of a fuel type")
    @APIResponse(responseCode = "200", description = "Total quantity of the specified fuel type")
    @APIResponse(responseCode = "404", description = "Fuel type not found")
    public Response getTotalQuantityByType(@PathParam("typeId") Long typeId) {
        FuelType fuelType = fuelTypeRepository.findById(typeId);
        if (fuelType == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + typeId))
                    .build();
        }

        double totalQuantity = repository.getTotalQuantityByType(typeId);

        return Response.ok(new TotalQuantityResponse(fuelType.name, totalQuantity)).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new fuel record")
    @APIResponse(responseCode = "201", description = "Fuel record created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createFuel(@Valid FuelCreateDTO fuelDTO) {
        FuelType fuelType = fuelTypeRepository.findById(fuelDTO.fuelTypeId);
        if (fuelType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + fuelDTO.fuelTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(fuelDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + fuelDTO.storageLocationId))
                    .build();
        }

        Fuel fuel = new Fuel();
        fuel.quantity = fuelDTO.quantity;
        fuel.fuelType = fuelType;
        fuel.storageLocation = storageLocation;

        repository.persist(fuel);

        notificationService.notifyResourceCreated("Fuel", fuel.id, fuelType.name + " (Qty: " + fuel.quantity + ")");

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(fuel))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a fuel record")
    @APIResponse(responseCode = "200", description = "Fuel record updated")
    @APIResponse(responseCode = "404", description = "Fuel record not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateFuel(@PathParam("id") Long id, @Valid FuelCreateDTO fuelDTO) {
        Fuel fuel = repository.findById(id);
        if (fuel == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel record not found with ID: " + id))
                    .build();
        }

        FuelType fuelType = fuelTypeRepository.findById(fuelDTO.fuelTypeId);
        if (fuelType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + fuelDTO.fuelTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(fuelDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + fuelDTO.storageLocationId))
                    .build();
        }

        fuel.quantity = fuelDTO.quantity;
        fuel.fuelType = fuelType;
        fuel.storageLocation = storageLocation;

        repository.persist(fuel);

        notificationService.notifyResourceUpdated("Fuel", fuel.id, fuelType.name + " (Qty: " + fuel.quantity + ")");

        return Response.ok(mapToDTO(fuel)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a fuel record")
    @APIResponse(responseCode = "204", description = "Fuel record deleted")
    @APIResponse(responseCode = "404", description = "Fuel record not found")
    public Response deleteFuel(@PathParam("id") Long id) {
        Fuel fuel = repository.findById(id);
        if (fuel == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel record not found with ID: " + id))
                    .build();
        }

        String fuelDescription = fuel.fuelType.name + " (Qty: " + fuel.quantity + ")";

        repository.delete(fuel);

        notificationService.notifyResourceDeleted("Fuel", id, fuelDescription);

        return Response.noContent().build();
    }

    private FuelDTO mapToDTO(Fuel fuel) {
        return new FuelDTO(
                fuel.id,
                fuel.quantity,
                fuel.fuelType.id,
                fuel.storageLocation.id,
                fuel.fuelType.name,
                fuel.storageLocation.name
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
        public String fuelType;
        public double totalQuantity;

        public TotalQuantityResponse(String fuelType, double totalQuantity) {
            this.fuelType = fuelType;
            this.totalQuantity = totalQuantity;
        }

        public TotalQuantityResponse() {
        }
    }
}