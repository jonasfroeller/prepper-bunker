package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.FoodCreateDTO;
import org.jonasfroeller.dtos.FoodDTO;
import org.jonasfroeller.models.Food;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.FoodRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/food")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FoodResource {

    @Inject
    FoodRepository repository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all food items")
    @APIResponse(responseCode = "200", description = "List of all food items")
    public List<FoodDTO> getAllFood() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get food item by ID")
    @APIResponse(responseCode = "200", description = "The food item with the given ID")
    @APIResponse(responseCode = "404", description = "Food item not found")
    public Response getFoodById(@PathParam("id") Long id) {
        Food food = repository.findById(id);
        if (food == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Food item not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(food)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get food items by storage location")
    @APIResponse(responseCode = "200", description = "List of food items at the specified location")
    public List<FoodDTO> getFoodByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/expired")
    @Operation(summary = "Get expired food items")
    @APIResponse(responseCode = "200", description = "List of expired food items")
    public List<FoodDTO> getExpiredFood() {
        return repository.findExpiredItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/expiring-soon")
    @Operation(summary = "Get food items expiring within the next month")
    @APIResponse(responseCode = "200", description = "List of food items expiring soon")
    public List<FoodDTO> getFoodExpiringSoon() {
        return repository.findItemsExpiringSoon().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new food item")
    @APIResponse(responseCode = "201", description = "Food item created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createFood(@Valid FoodCreateDTO foodDTO) {
        StorageLocation storageLocation = storageLocationRepository.findById(foodDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + foodDTO.storageLocationId))
                    .build();
        }

        Food food = new Food();
        food.type = foodDTO.type;
        food.quantity = foodDTO.quantity;
        food.expirationDate = foodDTO.expirationDate;
        food.storageLocation = storageLocation;

        repository.persist(food);

        notificationService.notifyResourceCreated("Food", food.id, food.type);

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(food))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a food item")
    @APIResponse(responseCode = "200", description = "Food item updated")
    @APIResponse(responseCode = "404", description = "Food item not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateFood(@PathParam("id") Long id, @Valid FoodCreateDTO foodDTO) {
        Food food = repository.findById(id);
        if (food == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Food item not found with ID: " + id))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(foodDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + foodDTO.storageLocationId))
                    .build();
        }

        food.type = foodDTO.type;
        food.quantity = foodDTO.quantity;
        food.expirationDate = foodDTO.expirationDate;
        food.storageLocation = storageLocation;

        repository.persist(food);

        notificationService.notifyResourceUpdated("Food", food.id, food.type);

        return Response.ok(mapToDTO(food)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a food item")
    @APIResponse(responseCode = "204", description = "Food item deleted")
    @APIResponse(responseCode = "404", description = "Food item not found")
    public Response deleteFood(@PathParam("id") Long id) {
        Food food = repository.findById(id);
        if (food == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Food item not found with ID: " + id))
                    .build();
        }

        String foodType = food.type;

        repository.delete(food);

        notificationService.notifyResourceDeleted("Food", id, foodType);

        return Response.noContent().build();
    }

    private FoodDTO mapToDTO(Food food) {
        return new FoodDTO(
                food.id,
                food.type,
                food.quantity,
                food.expirationDate,
                food.storageLocation.id,
                food.storageLocation.name
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