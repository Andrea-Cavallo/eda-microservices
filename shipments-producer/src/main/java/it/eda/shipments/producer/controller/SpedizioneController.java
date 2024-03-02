package it.eda.shipments.producer.controller;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import it.eda.shipments.producer.controller.model.RestResponse;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.handler.RequestHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Path("/v1/shipments")
@Tag(name = "Shipments", description = "the Shipments API")
public class SpedizioneController {

    public SpedizioneController(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    private final RequestHandler requestHandler;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createAShipment", summary = "Create Shipment")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Shipment Created",
             content = @Content(schema = @Schema(implementation = SpedizioneDTO.class))),
            @APIResponse(responseCode = "401", description = "User not authorized"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
        @APIResponse(responseCode = "500", description = "Internal Server Error")})
    public Uni<Response> createAShippment(@HeaderParam("sessionId")
          @Parameter(description = "Session ID", required = true)
          String sessionId, SpedizioneDTO spedizioneRequest,  @Context
    HttpHeaders headers) {
        logIncomingHeaders(headers);

        Log.infof("Request in ingresso al createAShipment-Controller %s", spedizioneRequest);
        return requestHandler.handleShippment(spedizioneRequest, false,sessionId);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "updateShipment", summary = "Update Shipment")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Shipment Updated",
             content = @Content(schema = @Schema(implementation = SpedizioneDTO.class))),
            @APIResponse(responseCode = "401", description = "User not authorized"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
        @APIResponse(responseCode = "500", description = "Internal Server Error")})
    public Uni<Response> updateShipment(@PathParam("id") @Parameter(description = "Shipment ID", required = true) Long id,
            @HeaderParam("sessionId") @Parameter(description = "Session ID", required = true) String sessionId,
            SpedizioneDTO spedizioneRequest) {
        if (id == null) {
            RestResponse<SpedizioneDTO> response = new RestResponse<>();
            Log.error("Attenzione: Id mancante per l'update");
            response.setCodiceErrore(Map.of("codice",400L));
            response.setDescrizioneErrore(Map.of("descrizione",  "ID della spedizione  da  aggiornare mancante"));
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity(response).build());
        }
        Log.infof("Request in ingresso al updateShipment-Controller %s", spedizioneRequest);
        spedizioneRequest.setIdSpedizione(id);
        return requestHandler.handleShippment(spedizioneRequest, true, sessionId);
    }
    private void logIncomingHeaders(HttpHeaders headers) {
        for (Map.Entry<String, List<String>> header : headers.getRequestHeaders().entrySet()) {
            Log.infof("Header in ingresso: %s: %s", header.getKey(), header.getValue());
        }
    }
}