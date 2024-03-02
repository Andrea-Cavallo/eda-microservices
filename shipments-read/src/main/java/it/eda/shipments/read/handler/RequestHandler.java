package it.eda.shipments.read.handler;


import io.smallrye.mutiny.Uni;
import it.eda.shipments.read.repository.RedisSessionService;
import it.eda.shipments.read.service.SpedizioneService;
import it.eda.shipments.read.utils.AsmaQueryOrderUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


@ApplicationScoped
public class RequestHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    public static final String AD_ACCEDERE_ALLA_RISORSA = "Utente non autorizzato ad accedere alla risorsa";
    public static final String ADMIN = "admin";
    private final SpedizioneService spedizioneService;

    @Inject
    RedisSessionService redisSessionService;


    public RequestHandler(SpedizioneService spedizioneService) {
        this.spedizioneService = spedizioneService;
    }

    /**
     * Retrieves a list of shipments for a user with the specified email address.
     *
     * @param email     The email address of the user.
     * @param sessionId The session ID of the user.
     * @param pageIndex The index of the page to retrieve.
     * @param pageSize  The number of shipments per page.
     * @return A Uni object that emits a Response containing the shipments found, or an error response.
     */
    public Uni<Response> handleGetShipmentsByEmail(String email, String sessionId, int pageIndex, int pageSize) {
        if (Objects.isNull(sessionId)) {
            return errorResponse(Response.Status.UNAUTHORIZED, AD_ACCEDERE_ALLA_RISORSA, 401L, 401L, sessionId);
        }


        return checkIfEmailProvidedIsStored(sessionId, email)
                .flatMap(isAccessible -> {
                    if (Boolean.TRUE.equals(isAccessible)) {
                        return spedizioneService.findShipmentsByEmail(email, pageIndex, pageSize)
                                ;
                    } else {
                        return errorResponse(Response.Status.UNAUTHORIZED, AD_ACCEDERE_ALLA_RISORSA, 401L, 401L, sessionId)
                                ;
                    }
                });
    }


    /**
     * Handles the request to retrieve all shipments.
     *
     * @param sessionId the session ID
     * @param pageIndex the index of the page
     * @param pageSize  the size of the page
     * @return a Uni containing the response with the list of shipments or an error response
     */
    public Uni<Response> handleGetAllShipments(String sessionId, int pageIndex, int pageSize) {
        if (Objects.isNull(sessionId)) {
            return errorResponse(Response.Status.UNAUTHORIZED, AD_ACCEDERE_ALLA_RISORSA, 401L, 401L, sessionId);
        }


        return checkIfIsAnAdmin(sessionId)
                .flatMap(isAdmin -> {
                    if (Boolean.TRUE.equals(isAdmin)) {
                        return spedizioneService.findAllShipments(pageIndex, pageSize);
                    } else {
                        return errorResponse(Response.Status.UNAUTHORIZED, AD_ACCEDERE_ALLA_RISORSA, 401L, 401L, sessionId)
                                ;
                    }
                });


    }


    /**
     * Builds an error response with the specified status, message, code, HTTP status, and log message.
     *
     * @param status     The status of the response.
     * @param message    The error message.
     * @param code       The error code.
     * @param httpStatus The HTTP status code.
     * @param logMessage The log message.
     * @return An error response with the specified status, message, code, HTTP status, and log message.
     */
    private Uni<Response> errorResponse(Response.Status status, String message, Long code, Long httpStatus, String logMessage) {
        LOGGER.error("Costruisco l'errore di risposta: {} , {}", message, logMessage);
        return Uni.createFrom().item(Response.status(status)
                .entity(AsmaQueryOrderUtils.buildRestResponseWithErrorMessages(message, code, httpStatus)).build());
    }


    /**
     * Checks if the user with the given session ID is an admin.
     *
     * @param sessionId The session ID of the user.
     * @return A Uni object that emits true if the user is an admin, false otherwise.
     */
    private Uni<Boolean> checkIfIsAnAdmin(String sessionId) {
        return redisSessionService.getSession(sessionId).map(s -> {
            if (s.isPresent() && !Objects.isNull(s.get().getRole())) {
                String role = s.get().getRole();
                return ADMIN.equalsIgnoreCase(role);

            }
            return false;
        });


    }


    private Uni<Boolean> checkIfEmailProvidedIsStored(String sessionId, String email) {
        return redisSessionService.getSession(sessionId).map(s -> {
            if (s.isPresent() && !Objects.isNull(s.get().getEmail())) {
                String emailStored = s.get().getEmail();
                return emailStored.equalsIgnoreCase(email);
            }
            return false;
        });


    }
}