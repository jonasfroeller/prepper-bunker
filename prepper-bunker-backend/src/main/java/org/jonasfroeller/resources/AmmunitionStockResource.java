package org.jonasfroeller.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jonasfroeller.dtos.AmmunitionStockCreateDTO;
import org.jonasfroeller.dtos.AmmunitionStockDTO;
import org.jonasfroeller.dtos.AmmunitionSummaryDTO;
import org.jonasfroeller.models.AmmunitionStock;
import org.jonasfroeller.models.AmmunitionType;
import org.jonasfroeller.models.StorageLocation;
import org.jonasfroeller.repositories.AmmunitionStockRepository;
import org.jonasfroeller.repositories.AmmunitionTypeRepository;
import org.jonasfroeller.repositories.StorageLocationRepository;
import org.jonasfroeller.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/ammunition-stocks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AmmunitionStockResource {

    @Inject
    AmmunitionStockRepository repository;

    @Inject
    AmmunitionTypeRepository ammunitionTypeRepository;

    @Inject
    StorageLocationRepository storageLocationRepository;

    @Inject
    NotificationService notificationService;

    @GET
    @Operation(summary = "Get all ammunition stocks")
    @APIResponse(responseCode = "200", description = "List of all ammunition stocks")
    public List<AmmunitionStockDTO> getAllAmmunitionStocks() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get ammunition stock by ID")
    @APIResponse(responseCode = "200", description = "The ammunition stock with the given ID")
    @APIResponse(responseCode = "404", description = "Ammunition stock not found")
    public Response getAmmunitionStockById(@PathParam("id") Long id) {
        AmmunitionStock stock = repository.findById(id);
        if (stock == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition stock not found with ID: " + id))
                    .build();
        }

        return Response.ok(mapToDTO(stock)).build();
    }

    @GET
    @Path("/by-location/{locationId}")
    @Operation(summary = "Get ammunition stocks by storage location")
    @APIResponse(responseCode = "200", description = "List of ammunition stocks at the specified location")
    public List<AmmunitionStockDTO> getAmmunitionStocksByLocation(@PathParam("locationId") Long locationId) {
        return repository.findByStorageLocation(locationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/total/{typeId}")
    @Operation(summary = "Get total quantity of an ammunition type across all stocks")
    @APIResponse(responseCode = "200", description = "Total quantity of the specified ammunition type")
    @APIResponse(responseCode = "404", description = "Ammunition type not found")
    public Response getTotalQuantityByType(@PathParam("typeId") Long typeId) {
        AmmunitionType type = ammunitionTypeRepository.findById(typeId);
        if (type == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + typeId))
                    .build();
        }

        int totalQuantity = repository.getTotalQuantityByType(typeId);

        AmmunitionSummaryDTO summary = new AmmunitionSummaryDTO(
                type.id,
                type.caliber,
                type.type,
                totalQuantity
        );

        return Response.ok(summary).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create a new ammunition stock")
    @APIResponse(responseCode = "201", description = "Ammunition stock created")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createAmmunitionStock(@Valid AmmunitionStockCreateDTO stockDTO) {
        AmmunitionType ammunitionType = ammunitionTypeRepository.findById(stockDTO.ammunitionTypeId);
        if (ammunitionType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + stockDTO.ammunitionTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(stockDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + stockDTO.storageLocationId))
                    .build();
        }

        AmmunitionStock stock = new AmmunitionStock();
        stock.quantity = stockDTO.quantity;
        stock.ammunitionType = ammunitionType;
        stock.storageLocation = storageLocation;

        repository.persist(stock);

        notificationService.notifyResourceCreated("AmmunitionStock", stock.id,
                ammunitionType.caliber + " " + ammunitionType.type + " (Qty: " + stock.quantity + ")");

        return Response.status(Response.Status.CREATED)
                .entity(mapToDTO(stock))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update an ammunition stock")
    @APIResponse(responseCode = "200", description = "Ammunition stock updated")
    @APIResponse(responseCode = "404", description = "Ammunition stock not found")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response updateAmmunitionStock(@PathParam("id") Long id, @Valid AmmunitionStockCreateDTO stockDTO) {
        AmmunitionStock stock = repository.findById(id);
        if (stock == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition stock not found with ID: " + id))
                    .build();
        }

        AmmunitionType ammunitionType = ammunitionTypeRepository.findById(stockDTO.ammunitionTypeId);
        if (ammunitionType == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Ammunition type not found with ID: " + stockDTO.ammunitionTypeId))
                    .build();
        }

        StorageLocation storageLocation = storageLocationRepository.findById(stockDTO.storageLocationId);
        if (storageLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Storage location not found with ID: " + stockDTO.storageLocationId))
                    .build();
        }

        stock.quantity = stockDTO.quantity;
        stock.ammunitionType = ammunitionType;
        stock.storageLocation = storageLocation;

        repository.persist(stock);

        notificationService.notifyResourceUpdated("AmmunitionStock", stock.id,
                ammunitionType.caliber + " " + ammunitionType.type + " (Qty: " + stock.quantity + ")");

        return Response.ok(mapToDTO(stock)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete an ammunition stock")
    @APIResponse(responseCode = "204", description = "Ammunition stock deleted")
    @APIResponse(responseCode = "404", description = "Ammunition stock not found")
    public Response deleteAmmunitionStock(@PathParam("id") Long id) {
        AmmunitionStock stock = repository.findById(id);
        if (stock == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Ammunition stock not found with ID: " + id))
                    .build();
        }

        String stockName = stock.ammunitionType.caliber + " " + stock.ammunitionType.type + " (Qty: " + stock.quantity + ")";

        repository.delete(stock);

        notificationService.notifyResourceDeleted("AmmunitionStock", id, stockName);

        return Response.noContent().build();
    }

    private AmmunitionStockDTO mapToDTO(AmmunitionStock stock) {
        return new AmmunitionStockDTO(
                stock.id,
                stock.quantity,
                stock.ammunitionType.id,
                stock.storageLocation.id,
                stock.ammunitionType.caliber + " " + stock.ammunitionType.type,
                stock.storageLocation.name
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