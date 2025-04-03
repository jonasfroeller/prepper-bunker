package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.StorageLocationDTO;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.*;
import org.jonasfroeller.services.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/storage-locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StorageLocationResource {

    @Inject
    StorageLocationRepository repository;

    @Inject
    WeaponRepository weaponRepository;
    @Inject
    AmmunitionStockRepository ammunitionStockRepository;
    @Inject
    DrinkRepository drinkRepository;
    @Inject
    FoodRepository foodRepository;
    @Inject
    MedicationRepository medicationRepository;
    @Inject
    FuelRepository fuelRepository;
    @Inject
    BatteryRepository batteryRepository;
    @Inject
    GeneratorRepository generatorRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all storage locations")
    @APIResponse(responseCode = "200", description = "List of all storage locations")
    public List<StorageLocationDTO> getAllStorageLocations() {
        return repository.findAll().stream()
                .map(location -> new StorageLocationDTO(location.id, location.name, location.description))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get storage location by ID")
    @APIResponse(responseCode = "200", description = "The storage location with the given ID")
    @APIResponse(responseCode = "404", description = "Storage location not found")
    public Response getStorageLocationById(@PathParam("id") Long id) {
        StorageLocation location = repository.findById(id);
        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Storage location not found with ID: " + id))
                    .build();
        }

        StorageLocationDTO dto = new StorageLocationDTO(location.id, location.name, location.description);
        return Response.ok(dto).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new storage location")
    @APIResponse(responseCode = "201", description = "Storage location created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createStorageLocation(@Valid StorageLocationDTO locationDTO) {
        StorageLocation location = new StorageLocation();
        location.name = locationDTO.name;
        location.description = locationDTO.description;

        repository.persist(location);

        notificationService.notifyResourceCreated("StorageLocation", location.id, location.name);

        return Response.status(Response.Status.CREATED)
                .entity(new StorageLocationDTO(location.id, location.name, location.description))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a storage location")
    @APIResponse(responseCode = "200", description = "Storage location updated")
    @APIResponse(responseCode = "404", description = "Storage location not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateStorageLocation(@PathParam("id") Long id, @Valid StorageLocationDTO locationDTO) {
        StorageLocation location = repository.findById(id);
        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Storage location not found with ID: " + id))
                    .build();
        }

        location.name = locationDTO.name;
        location.description = locationDTO.description;

        repository.persist(location);

        notificationService.notifyResourceUpdated("StorageLocation", location.id, location.name);

        return Response.ok(new StorageLocationDTO(location.id, location.name, location.description)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a storage location")
    @APIResponse(responseCode = "204", description = "Storage location deleted")
    @APIResponse(responseCode = "404", description = "Storage location not found")
    @APIResponse(responseCode = "409", description = "Storage location cannot be deleted due to existing references")
    public Response deleteStorageLocation(@PathParam("id") Long id) {
        StorageLocation location = repository.findById(id);
        if (location == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Storage location not found with ID: " + id))
                    .build();
        }

        List<String> referencingItems = new ArrayList<>();
        if (weaponRepository.count("storageLocation.id", id) > 0) referencingItems.add("Weapon");
        if (ammunitionStockRepository.count("storageLocation.id", id) > 0) referencingItems.add("AmmunitionStock");
        if (drinkRepository.count("storageLocation.id", id) > 0) referencingItems.add("Drink");
        if (foodRepository.count("storageLocation.id", id) > 0) referencingItems.add("Food");
        if (medicationRepository.count("storageLocation.id", id) > 0) referencingItems.add("Medication");
        if (fuelRepository.count("storageLocation.id", id) > 0) referencingItems.add("Fuel");
        if (batteryRepository.count("storageLocation.id", id) > 0) referencingItems.add("Battery");
        if (generatorRepository.count("storageLocation.id", id) > 0) referencingItems.add("Generator");

        if (!referencingItems.isEmpty()) {
            String dependencyDetails = String.join(", ", referencingItems);
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Cannot delete Storage Location with ID: " + id +
                            ". It is referenced by the following item types: " + dependencyDetails + "."))
                    .build();
        }

        String locationName = location.name;

        repository.delete(location);

        notificationService.notifyResourceDeleted("StorageLocation", id, locationName);

        return Response.noContent().build();
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public ErrorResponse() {
        }
    }
}