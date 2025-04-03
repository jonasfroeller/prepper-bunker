package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.MedicationCreateDTO;
import org.jonasfroeller.dtos.MedicationDTO;
import org.jonasfroeller.models.Medication;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.MedicationRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/medications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicationResource {

    @Inject
    MedicationRepository repository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all medications")
    @APIResponse(responseCode = "200", description = "List of all medications")
    public List<MedicationDTO> getAllMedications() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get medication by ID")
    @APIResponse(responseCode = "200", description = "The medication with the given ID")
    @APIResponse(responseCode = "404", description = "Medication not found")
    public Response getMedicationById(@PathParam("id") Long id) {
        Medication medication = repository.findById(id);
        if (medication == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Medication not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(medication)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get medications by storage location")
    @APIResponse(responseCode = "200", description = "List of medications at the specified location")
    public List<MedicationDTO> getMedicationsByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/by-purpose/{purpose}")
    @Operation(summary = "Get medications by purpose")
    @APIResponse(responseCode = "200", description = "List of medications for the specified purpose")
    public List<MedicationDTO> getMedicationsByPurpose(@PathParam("purpose") String purpose) {
        return repository.findByPurpose(purpose).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/expired")
    @Operation(summary = "Get expired medications")
    @APIResponse(responseCode = "200", description = "List of expired medications")
    public List<MedicationDTO> getExpiredMedications() {
        return repository.findExpiredItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/expiring-soon")
    @Operation(summary = "Get medications expiring within the next month")
    @APIResponse(responseCode = "200", description = "List of medications expiring soon")
    public List<MedicationDTO> getMedicationsExpiringSoon() {
        return repository.findItemsExpiringSoon().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new medication")
    @APIResponse(responseCode = "201", description = "Medication created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createMedication(@Valid MedicationCreateDTO medicationDTO) {
        StorageLocation storageLocation = storageLocationRepository.findById(medicationDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + medicationDTO.storageLocationId))
                    .build();
        }

        Medication medication = new Medication();
        medication.name = medicationDTO.name;
        medication.quantity = medicationDTO.quantity;
        medication.expirationDate = medicationDTO.expirationDate;
        medication.purpose = medicationDTO.purpose;
        medication.storageLocation = storageLocation;

        repository.persist(medication);

        notificationService.notifyResourceCreated("Medication", medication.id, medication.name);

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(medication))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update a medication")
    @APIResponse(responseCode = "200", description = "Medication updated")
    @APIResponse(responseCode = "404", description = "Medication not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateMedication(@PathParam("id") Long id, @Valid MedicationCreateDTO medicationDTO) {
        Medication medication = repository.findById(id);
        if (medication == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Medication not found with ID: " + id))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(medicationDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + medicationDTO.storageLocationId))
                    .build();
        }

        medication.name = medicationDTO.name;
        medication.quantity = medicationDTO.quantity;
        medication.expirationDate = medicationDTO.expirationDate;
        medication.purpose = medicationDTO.purpose;
        medication.storageLocation = storageLocation;

        repository.persist(medication);

        notificationService.notifyResourceUpdated("Medication", medication.id, medication.name);

        return Response.ok(mapToDTO(medication)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a medication")
    @APIResponse(responseCode = "204", description = "Medication deleted")
    @APIResponse(responseCode = "404", description = "Medication not found")
    public Response deleteMedication(@PathParam("id") Long id) {
        Medication medication = repository.findById(id);
        if (medication == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Medication not found with ID: " + id))
                    .build();
        }

        String medicationName = medication.name;

        repository.delete(medication);

        notificationService.notifyResourceDeleted("Medication", id, medicationName);

        return Response.noContent().build();
    }

    private MedicationDTO mapToDTO(Medication medication) {
        return new MedicationDTO(
                medication.id,
                medication.name,
                medication.quantity,
                medication.expirationDate,
                medication.purpose,
                medication.storageLocation.id,
                medication.storageLocation.name
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