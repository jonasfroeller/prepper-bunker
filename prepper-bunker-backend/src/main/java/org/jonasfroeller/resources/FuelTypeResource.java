package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.FuelTypeCreateDTO;
import org.jonasfroeller.dtos.FuelTypeDTO;
import org.jonasfroeller.models.FuelType;
import org.jonasfroeller.repositories.FuelRepository;
import org.jonasfroeller.repositories.FuelTypeRepository;
import org.jonasfroeller.repositories.GeneratorRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/fuel-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuelTypeResource {

    @Inject
    FuelTypeRepository repository;

    @Inject
    FuelRepository fuelRepository;

    @Inject
    GeneratorRepository generatorRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all fuel types")
    @APIResponse(responseCode = "200", description = "List of all fuel types")
    public List<FuelTypeDTO> getAllFuelTypes() {
        return repository.findAll().stream()
                .map(fuelType -> new FuelTypeDTO(fuelType.id, fuelType.name))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get fuel type by ID")
    @APIResponse(responseCode = "200", description = "The fuel type with the given ID")
    @APIResponse(responseCode = "404", description = "Fuel type not found")
    public Response getFuelTypeById(@PathParam("id") Long id) {
        FuelType fuelType = repository.findById(id);
        if (fuelType == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + id))
                    .build();
        }

        FuelTypeDTO dto = new FuelTypeDTO(fuelType.id, fuelType.name);
        return Response.ok(dto).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new fuel type")
    @APIResponse(responseCode = "201", description = "Fuel type created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createFuelType(@Valid FuelTypeCreateDTO fuelTypeDTO) {
        FuelType fuelType = new FuelType();
        fuelType.name = fuelTypeDTO.name;

        repository.persist(fuelType);

        notificationService.notifyResourceCreated("FuelType", fuelType.id, fuelType.name);

        return Response.status(Response.Status.CREATED)
                .entity(new FuelTypeDTO(fuelType.id, fuelType.name))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a fuel type")
    @APIResponse(responseCode = "200", description = "Fuel type updated")
    @APIResponse(responseCode = "404", description = "Fuel type not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateFuelType(@PathParam("id") Long id, @Valid FuelTypeCreateDTO fuelTypeDTO) {
        FuelType fuelType = repository.findById(id);
        if (fuelType == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + id))
                    .build();
        }

        fuelType.name = fuelTypeDTO.name;

        repository.persist(fuelType);

        notificationService.notifyResourceUpdated("FuelType", fuelType.id, fuelType.name);

        return Response.ok(new FuelTypeDTO(fuelType.id, fuelType.name)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a fuel type")
    @APIResponse(responseCode = "204", description = "Fuel type deleted")
    @APIResponse(responseCode = "404", description = "Fuel type not found")
    @APIResponse(responseCode = "409", description = "Fuel type cannot be deleted due to existing references")
    public Response deleteFuelType(@PathParam("id") Long id) {
        FuelType fuelType = repository.findById(id);
        if (fuelType == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + id))
                    .build();
        }

        long fuelCount = fuelRepository.count("fuelType.id", id);
        long generatorCount = generatorRepository.count("fuelType.id", id);

        if (fuelCount > 0 || generatorCount > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Cannot delete Fuel Type with ID: " + id +
                            ". It is referenced by " + fuelCount + " fuel record(s) and " +
                            generatorCount + " generator(s)."))
                    .build();
        }

        String fuelTypeName = fuelType.name;

        repository.delete(fuelType);

        notificationService.notifyResourceDeleted("FuelType", id, fuelTypeName);

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