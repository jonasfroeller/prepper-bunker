package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.DrinkCreateDTO;
import org.jonasfroeller.dtos.DrinkDTO;
import org.jonasfroeller.models.Drink;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.DrinkRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/drinks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DrinkResource {

    @Inject
    DrinkRepository repository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all drinks")
    @APIResponse(responseCode = "200", description = "List of all drinks")
    public List<DrinkDTO> getAllDrinks() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get drink by ID")
    @APIResponse(responseCode = "200", description = "The drink with the given ID")
    @APIResponse(responseCode = "404", description = "Drink not found")
    public Response getDrinkById(@PathParam("id") Long id) {
        Drink drink = repository.findById(id);
        if (drink == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Drink not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(drink)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get drinks by storage location")
    @APIResponse(responseCode = "200", description = "List of drinks at the specified location")
    public List<DrinkDTO> getDrinksByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/expired")
    @Operation(summary = "Get expired drinks")
    @APIResponse(responseCode = "200", description = "List of expired drinks")
    public List<DrinkDTO> getExpiredDrinks() {
        return repository.findExpiredItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/expiring-soon")
    @Operation(summary = "Get drinks expiring within the next month")
    @APIResponse(responseCode = "200", description = "List of drinks expiring soon")
    public List<DrinkDTO> getDrinksExpiringSoon() {
        return repository.findItemsExpiringSoon().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new drink")
    @APIResponse(responseCode = "201", description = "Drink created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createDrink(@Valid DrinkCreateDTO drinkDTO) {
        StorageLocation storageLocation = storageLocationRepository.findById(drinkDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + drinkDTO.storageLocationId))
                    .build();
        }

        Drink drink = new Drink();
        drink.type = drinkDTO.type;
        drink.quantity = drinkDTO.quantity;
        drink.expirationDate = drinkDTO.expirationDate;
        drink.storageLocation = storageLocation;

        repository.persist(drink);

        notificationService.notifyResourceCreated("Drink", drink.id, drink.type);

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(drink))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a drink")
    @APIResponse(responseCode = "200", description = "Drink updated")
    @APIResponse(responseCode = "404", description = "Drink not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateDrink(@PathParam("id") Long id, @Valid DrinkCreateDTO drinkDTO) {
        Drink drink = repository.findById(id);
        if (drink == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Drink not found with ID: " + id))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(drinkDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + drinkDTO.storageLocationId))
                    .build();
        }

        drink.type = drinkDTO.type;
        drink.quantity = drinkDTO.quantity;
        drink.expirationDate = drinkDTO.expirationDate;
        drink.storageLocation = storageLocation;

        repository.persist(drink);

        notificationService.notifyResourceUpdated("Drink", drink.id, drink.type);

        return Response.ok(mapToDTO(drink)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a drink")
    @APIResponse(responseCode = "204", description = "Drink deleted")
    @APIResponse(responseCode = "404", description = "Drink not found")
    public Response deleteDrink(@PathParam("id") Long id) {
        Drink drink = repository.findById(id);
        if (drink == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Drink not found with ID: " + id))
                    .build();
        }

        String drinkType = drink.type;

        repository.delete(drink);

        notificationService.notifyResourceDeleted("Drink", id, drinkType);

        return Response.noContent().build();
    }

    private DrinkDTO mapToDTO(Drink drink) {
        return new DrinkDTO(
                drink.id,
                drink.type,
                drink.quantity,
                drink.expirationDate,
                drink.storageLocation.id,
                drink.storageLocation.name
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
}