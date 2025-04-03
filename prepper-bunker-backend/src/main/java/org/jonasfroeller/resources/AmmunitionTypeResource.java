package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.AmmunitionTypeDTO;
import org.jonasfroeller.models.AmmunitionType;
import org.jonasfroeller.repositories.AmmunitionStockRepository;
import org.jonasfroeller.repositories.AmmunitionTypeRepository;
import org.jonasfroeller.repositories.WeaponRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/ammunition-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AmmunitionTypeResource {

    @Inject
    AmmunitionTypeRepository repository;

    @Inject
    WeaponRepository weaponRepository;

    @Inject
    AmmunitionStockRepository ammunitionStockRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all ammunition types")
    @APIResponse(responseCode = "200", description = "List of all ammunition types")
    public List<AmmunitionTypeDTO> getAllAmmunitionTypes() {
        return repository.findAll().stream()
                .map(type -> new AmmunitionTypeDTO(type.id, type.caliber, type.type))
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get ammunition type by ID")
    @APIResponse(responseCode = "200", description = "The ammunition type with the given ID")
    @APIResponse(responseCode = "404", description = "Ammunition type not found")
    public Response getAmmunitionTypeById(@PathParam("id") Long id) {
        AmmunitionType type = repository.findById(id);
        if (type == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + id))
                    .build();
        }

        AmmunitionTypeDTO dto = new AmmunitionTypeDTO(type.id, type.caliber, type.type);
        return Response.ok(dto).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new ammunition type")
    @APIResponse(responseCode = "201", description = "Ammunition type created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createAmmunitionType(@Valid AmmunitionTypeDTO typeDTO) {
        AmmunitionType type = new AmmunitionType();
        type.caliber = typeDTO.caliber;
        type.type = typeDTO.type;

        repository.persist(type);

        notificationService.notifyResourceCreated("AmmunitionType", type.id, type.caliber + " " + type.type);

        return Response.status(Response.Status.CREATED)
                .entity(new AmmunitionTypeDTO(type.id, type.caliber, type.type))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update an ammunition type")
    @APIResponse(responseCode = "200", description = "Ammunition type updated")
    @APIResponse(responseCode = "404", description = "Ammunition type not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateAmmunitionType(@PathParam("id") Long id, @Valid AmmunitionTypeDTO typeDTO) {
        AmmunitionType type = repository.findById(id);
        if (type == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + id))
                    .build();
        }

        type.caliber = typeDTO.caliber;
        type.type = typeDTO.type;

        repository.persist(type);

        notificationService.notifyResourceUpdated("AmmunitionType", type.id, type.caliber + " " + type.type);

        return Response.ok(new AmmunitionTypeDTO(type.id, type.caliber, type.type)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete an ammunition type")
    @APIResponse(responseCode = "204", description = "Ammunition type deleted")
    @APIResponse(responseCode = "404", description = "Ammunition type not found")
    @APIResponse(responseCode = "409", description = "Ammunition type cannot be deleted due to existing references")
    public Response deleteAmmunitionType(@PathParam("id") Long id) {
        AmmunitionType type = repository.findById(id);
        if (type == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + id))
                    .build();
        }

        long weaponCount = weaponRepository.count("ammunitionType.id", id);
        long stockCount = ammunitionStockRepository.count("ammunitionType.id", id);

        if (weaponCount > 0 || stockCount > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Cannot delete Ammunition Type with ID: " + id +
                            ". It is referenced by " + weaponCount + " weapon(s) and " +
                            stockCount + " ammunition stock(s)."))
                    .build();
        }

        String typeName = type.caliber + " " + type.type;

        repository.delete(type);

        notificationService.notifyResourceDeleted("AmmunitionType", id, typeName);

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