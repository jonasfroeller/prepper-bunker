package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.WeaponCreateDTO;
import org.jonasfroeller.dtos.WeaponDTO;
import org.jonasfroeller.models.AmmunitionType;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.models.Weapon;
import org.jonasfroeller.repositories.AmmunitionTypeRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.repositories.WeaponRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/weapons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeaponResource {

    @Inject
    WeaponRepository repository;

    @Inject
    AmmunitionTypeRepository ammunitionTypeRepository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all weapons")
    @APIResponse(responseCode = "200", description = "List of all weapons")
    public List<WeaponDTO> getAllWeapons() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get weapon by ID")
    @APIResponse(responseCode = "200", description = "The weapon with the given ID")
    @APIResponse(responseCode = "404", description = "Weapon not found")
    public Response getWeaponById(@PathParam("id") Long id) {
        Weapon weapon = repository.findById(id);
        if (weapon == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Weapon not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(weapon)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get weapons by storage location")
    @APIResponse(responseCode = "200", description = "List of weapons at the specified location")
    public List<WeaponDTO> getWeaponsByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new weapon")
    @APIResponse(responseCode = "201", description = "Weapon created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createWeapon(@Valid WeaponCreateDTO weaponDTO) {
        AmmunitionType ammunitionType = ammunitionTypeRepository.findById(weaponDTO.ammunitionTypeId);
        if (ammunitionType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + weaponDTO.ammunitionTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(weaponDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + weaponDTO.storageLocationId))
                    .build();
        }

        Weapon weapon = new Weapon();
        weapon.type = weaponDTO.type;
        weapon.model = weaponDTO.model;
        weapon.quantity = weaponDTO.quantity;
        weapon.ammunitionType = ammunitionType;
        weapon.storageLocation = storageLocation;

        repository.persist(weapon);

        notificationService.notifyResourceCreated("Weapon", weapon.id, weapon.type + " " + weapon.model);

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(weapon))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a weapon")
    @APIResponse(responseCode = "200", description = "Weapon updated")
    @APIResponse(responseCode = "404", description = "Weapon not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateWeapon(@PathParam("id") Long id, @Valid WeaponCreateDTO weaponDTO) {
        Weapon weapon = repository.findById(id);
        if (weapon == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Weapon not found with ID: " + id))
                    .build();
        }

        AmmunitionType ammunitionType = ammunitionTypeRepository.findById(weaponDTO.ammunitionTypeId);
        if (ammunitionType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + weaponDTO.ammunitionTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(weaponDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + weaponDTO.storageLocationId))
                    .build();
        }

        weapon.type = weaponDTO.type;
        weapon.model = weaponDTO.model;
        weapon.quantity = weaponDTO.quantity;
        weapon.ammunitionType = ammunitionType;
        weapon.storageLocation = storageLocation;

        repository.persist(weapon);

        notificationService.notifyResourceUpdated("Weapon", weapon.id, weapon.type + " " + weapon.model);

        return Response.ok(mapToDTO(weapon)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a weapon")
    @APIResponse(responseCode = "204", description = "Weapon deleted")
    @APIResponse(responseCode = "404", description = "Weapon not found")
    public Response deleteWeapon(@PathParam("id") Long id) {
        Weapon weapon = repository.findById(id);
        if (weapon == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Weapon not found with ID: " + id))
                    .build();
        }

        String weaponName = weapon.type + " " + weapon.model;

        repository.delete(weapon);

        notificationService.notifyResourceDeleted("Weapon", id, weaponName);

        return Response.noContent().build();
    }

    private WeaponDTO mapToDTO(Weapon weapon) {
        return new WeaponDTO(
                weapon.id,
                weapon.type,
                weapon.model,
                weapon.quantity,
                weapon.ammunitionType.id,
                weapon.storageLocation.id,
                weapon.ammunitionType.caliber + " " + weapon.ammunitionType.type,
                weapon.storageLocation.name
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