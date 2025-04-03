package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.GeneratorCreateDTO;
import org.jonasfroeller.dtos.GeneratorDTO;
import org.jonasfroeller.models.FuelType;
import org.jonasfroeller.models.Generator;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.FuelTypeRepository;
import org.jonasfroeller.repositories.GeneratorRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/generators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeneratorResource {

    @Inject
    GeneratorRepository repository;

    @Inject
    FuelTypeRepository fuelTypeRepository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all generators")
    @APIResponse(responseCode = "200", description = "List of all generators")
    public List<GeneratorDTO> getAllGenerators() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get generator by ID")
    @APIResponse(responseCode = "200", description = "The generator with the given ID")
    @APIResponse(responseCode = "404", description = "Generator not found")
    public Response getGeneratorById(@PathParam("id") Long id) {
        Generator generator = repository.findById(id);
        if (generator == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Generator not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(generator)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get generators by storage location")
    @APIResponse(responseCode = "200", description = "List of generators at the specified location")
    public List<GeneratorDTO> getGeneratorsByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/by-fuel-type/{fuelTypeId}")
    @Operation(summary = "Get generators by fuel type")
    @APIResponse(responseCode = "200", description = "List of generators using the specified fuel type")
    public List<GeneratorDTO> getGeneratorsByFuelType(@PathParam("fuelTypeId") Long fuelTypeId) {
        return repository.findByFuelType(fuelTypeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/by-status/{status}")
    @Operation(summary = "Get generators by status")
    @APIResponse(responseCode = "200", description = "List of generators with the specified status")
    public List<GeneratorDTO> getGeneratorsByStatus(@PathParam("status") String status) {
        return repository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new generator")
    @APIResponse(responseCode = "201", description = "Generator created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createGenerator(@Valid GeneratorCreateDTO generatorDTO) {
        FuelType fuelType = fuelTypeRepository.findById(generatorDTO.fuelTypeId);
        if (fuelType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + generatorDTO.fuelTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(generatorDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + generatorDTO.storageLocationId))
                    .build();
        }

        Generator generator = new Generator();
        generator.type = generatorDTO.type;
        generator.power = generatorDTO.power;
        generator.status = generatorDTO.status;
        generator.fuelType = fuelType;
        generator.storageLocation = storageLocation;

        repository.persist(generator);

        notificationService.notifyResourceCreated("Generator", generator.id,
                generator.type + " " + generator.power + "kW (" + generator.status + ")");

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(generator))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a generator")
    @APIResponse(responseCode = "200", description = "Generator updated")
    @APIResponse(responseCode = "404", description = "Generator not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateGenerator(@PathParam("id") Long id, @Valid GeneratorCreateDTO generatorDTO) {
        Generator generator = repository.findById(id);
        if (generator == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Generator not found with ID: " + id))
                    .build();
        }

        FuelType fuelType = fuelTypeRepository.findById(generatorDTO.fuelTypeId);
        if (fuelType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Fuel type not found with ID: " + generatorDTO.fuelTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(generatorDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + generatorDTO.storageLocationId))
                    .build();
        }

        generator.type = generatorDTO.type;
        generator.power = generatorDTO.power;
        generator.status = generatorDTO.status;
        generator.fuelType = fuelType;
        generator.storageLocation = storageLocation;

        repository.persist(generator);

        notificationService.notifyResourceUpdated("Generator", generator.id,
                generator.type + " " + generator.power + "kW (" + generator.status + ")");

        return Response.ok(mapToDTO(generator)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a generator")
    @APIResponse(responseCode = "204", description = "Generator deleted")
    @APIResponse(responseCode = "404", description = "Generator not found")
    public Response deleteGenerator(@PathParam("id") Long id) {
        Generator generator = repository.findById(id);
        if (generator == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Generator not found with ID: " + id))
                    .build();
        }

        String generatorDescription = generator.type + " " + generator.power + "kW (" + generator.status + ")";

        repository.delete(generator);

        notificationService.notifyResourceDeleted("Generator", id, generatorDescription);

        return Response.noContent().build();
    }

    private GeneratorDTO mapToDTO(Generator generator) {
        return new GeneratorDTO(
                generator.id,
                generator.type,
                generator.power,
                generator.status,
                generator.fuelType.id,
                generator.storageLocation.id,
                generator.fuelType.name,
                generator.storageLocation.name
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