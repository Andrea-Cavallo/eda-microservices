package it.eda.users.ui.api.users;

import it.eda.users.ui.model.user.UserBase;
import it.eda.users.ui.repo.RedisSessionRepo;
import it.eda.users.ui.model.session.Session;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/api/userdata")
public class UserDataApi {

    @Inject
    RedisSessionRepo redisAuthService;
    /**
     * Fetches the user email from a session ID.
     *
     * @param sessionId the session ID
     * @return a Response object containing the user email if the session is valid, or an error message if the session is invalid or missing
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchUserEmailFromSessionID(@CookieParam("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Devi prima effettuare il login.\"}").build();
        }
        Optional<Session> optionalSession;
        try {
            optionalSession = redisAuthService.getSession(sessionId);
        } catch
        (Exception e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Non sono riuscito a contattare Redis..").build();
        }
        if (optionalSession.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session not found or expired").build();
        }
        return returnResponseWithUserData(optionalSession.get());
    }

    private static Response returnResponseWithUserData(Session session) {
        UserBase responseUser = new UserBase();
        responseUser.setEmail(session.getEmail());
        return Response.ok(responseUser).build();
    }
}

