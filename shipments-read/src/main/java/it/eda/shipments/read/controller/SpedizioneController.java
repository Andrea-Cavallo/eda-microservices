package it.eda.shipments.read.controller;

import io.smallrye.mutiny.Uni;
import it.eda.shipments.read.controller.model.Shipments;
import it.eda.shipments.read.handler.RequestHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/shipments")
@Tag(name = "Shipments", description = "the Shipments API")
public class SpedizioneController {

    @Inject
    RequestHandler requestHandler;



    /**
     * Returns the list of shipments based on an email.
     *
     * @param userEmail the email of the user
     * @param pageIndex the index of the page (default: 0)
     * @param pageSize  the size of the page (default: 10)
     * @param sessionId the session ID (required)
     * @return a Uni containing the response with the list of shipments or an error response
     */
    @GET
    @Path("/{userEmail}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns the list of shipments based on an Email")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Shipments returned successfully",
                    content = @Content(schema = @Schema(implementation = Shipments[].class))),
            @APIResponse(responseCode = "404", description = "Shipments not found"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    public Uni<Response> getShipmentsByEmail(@PathParam("userEmail") String userEmail,
                                             @QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
                                             @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                                             @HeaderParam("sessionId") @Parameter(description = "Session ID", required = true) String sessionId) {
        return requestHandler.handleGetShipmentsByEmail(userEmail, sessionId, pageIndex, pageSize);
    }

    /**
     * Returns all shipments.
     *
     * @param pageIndex  the index of the page
     * @param pageSize   the size of the page
     * @param sessionId  the session ID
     * @return a Uni containing the response with the list of shipments or an error response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Returns all shipments")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "All shipments returned",
                    content = @Content(schema = @Schema(implementation = Shipments[].class))),
            @APIResponse(responseCode = "500", description = "Internal Server Error")})
    public Uni<Response> getAllShipments(@QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
                                         @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                                         @HeaderParam("sessionId") @Parameter(description = "Session ID", required = true) String sessionId) {
        return requestHandler.handleGetAllShipments(sessionId, pageIndex, pageSize);
    }
}
