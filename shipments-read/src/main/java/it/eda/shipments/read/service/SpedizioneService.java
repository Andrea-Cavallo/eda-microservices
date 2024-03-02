package it.eda.shipments.read.service;


import it.eda.shipments.read.controller.model.Shipments;
import it.eda.shipments.read.repository.SpedizioneRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static it.eda.shipments.read.utils.AsmaQueryOrderUtils.buildRestResponseWithErrorMessages;
import static it.eda.shipments.read.utils.AsmaQueryOrderUtils.buildSuccesfulResponse;

/**
 * Service class for handling shipments.
 */
@ApplicationScoped
public class SpedizioneService {

    private final SpedizioneRepository spedizioneRepository;
    public static final Logger logger = LoggerFactory.getLogger(SpedizioneService.class);

    public SpedizioneService(SpedizioneRepository spedizioneRepository) {
        this.spedizioneRepository = spedizioneRepository;
    }

    /**
     * Finds shipments for a user with the specified email address.
     *
     * @param email     The email address of the user.
     * @param pageIndex The index of the page to retrieve.
     * @param pageSize  The number of shipments per page.
     * @return A Uni object that emits a Response containing the shipments found, or an error response if no shipments were found or an error occurred.
     */
    public Uni<Response> findShipmentsByEmail(String email, int pageIndex, int pageSize) {
        logger.info("Finding all shipments for user with email {}", email);
        return spedizioneRepository.findByEmail(email, pageIndex, pageSize)
                .map(spedizioni -> generateResponseBasedOnShipmentList(spedizioni,
                        "Nessuna spedizione trovata per l'email: " + email))
                .onFailure().recoverWithItem(this::buildErrorResponse);
    }

    /**
     * Returns the list of shipments based on the given page index and page size.
     *
     * @param pageIndex the index of the page
     * @param pageSize  the size of the page
     * @return a Uni containing the response with the list of shipments or an error response
     */
    public Uni<Response> findAllShipments(int pageIndex, int pageSize) {
        logger.info("Finding all shipments - findAllShipments");
        return spedizioneRepository.findAllShipments(pageIndex, pageSize)
                .map(spedizioni -> generateResponseBasedOnShipmentList(spedizioni,
                        "Nessuna spedizione trovata"))
                .onFailure().recoverWithItem(this::buildErrorResponse);
    }

    /**
     * Generate response based on shipment list.
     *
     * @param shipments  the list of shipments
     * @param errMessage the error message
     * @return the generated response
     */
    private Response generateResponseBasedOnShipmentList(List<Shipments> shipments, String errMessage) {
        if (shipments != null && !shipments.isEmpty()) {
            return Response.ok(buildSuccesfulResponse(shipments)).build();
        } else {
            return Response.status(Response.Status.OK)
                    .entity(buildRestResponseWithErrorMessages(errMessage, 404L, 404L)).build();
        }
    }

    /**
     * Builds an error response with the given throwable.
     *
     * @param throwable the throwable representing the error
     * @return the response with the error
     */
    private Response buildErrorResponse(Throwable throwable) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                buildRestResponseWithErrorMessages(
                        "Database error: " + throwable.getMessage(), 500L, 500L)).build();
    }
}