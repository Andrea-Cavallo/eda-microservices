package it.eda.users.ui.api.users;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import it.eda.users.ui.model.response.RestResponse;
import it.eda.users.ui.model.session.Session;
import it.eda.users.ui.model.user.User;
import it.eda.users.ui.repo.RedisSessionRepo;
import it.eda.users.ui.service.UserDbProxyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static it.eda.users.ui.utils.Constants.*;

@Path("/api")
@Slf4j
public class UserSessionApi {

    public static final String EMAIL1 = "email";
    public static final String PASSWORD = "password";
    public static final String NOME = "nome";
    public static final String COGNOME = "cognome";
    public static final String EMPTY_STRING = "";
    @Inject
    Template user;

    @Inject
    Template index;

    @Inject
    UserDbProxyService userDbProxyService;

    @Inject
    RedisSessionRepo redisAuthService;

    private static final Map<String, String> userSessions = new ConcurrentHashMap<>();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    @Path("login")
    public Response login(Map<String, Object> loginData) {

        log.info("Dentro l api-Login.. oggetto in ingresso:" + loginData);
        User userToLogin = createUserFromMap(loginData, false);
        if (isUserDataIncomplete(userToLogin, false)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Controllare i campi: I campi email e password sono necessari per il Login")
                    .build();
        }
        if (isUserRegistered(userToLogin)) {
            log.info("l'utente è registrato perciò può accedere, e loggarsi.. ");
            return saveSessionAndReturnResponse(userToLogin.getEmail());
        } else {
            log.info("Utente non loggato, non registrato");
            return Response.status(404).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    @Path("register")
    public Response register(Map<String, Object> userData) {
        User userToRegister = createUserFromMap(userData, true);
        if (isUserDataIncomplete(userToRegister,true)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Controllare i campi: tutti i dati sono necessari per registrarsi.")
                    .build();
        }

        log.info("Utente da Registrare nome {}, cognome {}", userToRegister.getNome(), userToRegister.getCognome());

        try {
            log.info("Chiamo il ms dBproxy, registro l'utente..");

            Response response = userDbProxyService.register(userToRegister);
            log.info("La risposta del ms dbProxy SaveUser {}", response);

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                // se tutto è ok dovrei tornare alla pagina di login....
                return Response.ok(index.render()).build();
            } else {
                log.error("Attenzione errore durante la registrazione ms dbProxy");
                String errorMessage = response.hasEntity() ? response.readEntity(String.class) : "Errore sconosciuto durante la registrazione.";
                return Response.status(response.getStatus())
                        .entity(errorMessage)
                        .build();
            }
        } catch (Exception e) {
            log.error("Attenzione non è stato possibile contattare il ms dbProxy di registrazione: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Errore durante la registrazione dell'utente: " + e.getMessage())
                    .build();
        }
    }


    private User createUserFromMap(Map<String, Object> userData, Boolean isToRegister) {
        User userRequest = new User();
        if (Boolean.TRUE.equals(isToRegister)) {
            userRequest.setNome((String) userData.getOrDefault(NOME, EMPTY_STRING));
            userRequest.setCognome((String) userData.getOrDefault(COGNOME, EMPTY_STRING));
        }
        userRequest.setEmail((String) userData.getOrDefault(EMAIL1, EMPTY_STRING));
        userRequest.setPassword((String) userData.getOrDefault(PASSWORD, EMPTY_STRING));
        return userRequest;
    }

    private boolean isUserDataIncomplete(User user, Boolean isToRegister) {
        if (Boolean.TRUE.equals(isToRegister)) {
            return Stream.of(user.getNome(), user.getCognome(), user.getEmail(), user.getPassword())
                    .anyMatch(Objects::isNull);
        } else return Stream.of(user.getEmail(), user.getPassword()).anyMatch(Objects::isNull);
    }


    private Response saveSessionAndReturnResponse(String email) {
        String role = USER;
        if (ADMIN_EMAIL.equalsIgnoreCase(email)) {
            log.debug("Facciamo finta che sia un admin visto che l'email è admin@admin.it ...");
            role = ADMIN;
        }

        String sessionId = UUID.randomUUID().toString();
        NewCookie userSessionCookie = createUserSessionCookie(sessionId);
        saveUserEmail(sessionId, email);
        TemplateInstance templateInstance = user.data(EMAIL1, email);
        Session session = Session.builder().sessionId(sessionId).email(email).role(role).build();

        try {
            log.debug("Salvo la sessione su Redis...");
            redisAuthService.saveSession(session);
        } catch
        (Exception e) {
            log.error("Attenzione Errore durante la save della sessione su Redis...");

            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Ci sono stati problemi nel salvare la sessione su Redis...").build();
        }
        return Response.ok(templateInstance).cookie(userSessionCookie).build();
    }


    public boolean isUserRegistered(User user) {
        log.info("Verifico che l utente sia registrato e quindi che possa loggarsi.");
        try {
            try (Response response = userDbProxyService.login(user)) {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    log.info("Utente con email {} trovato e autenticato nel database.", user.getEmail());
                    return true;
                } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                    log.warn("Autenticazione fallita per l utente con email {}. Password errata.", user.getEmail());
                    return false;
                } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                    log.warn("Nessun utente trovato con email {}.", user.getEmail());
                    return false;
                } else {
                    log.warn("Errore non gestito durante il tentativo di login per l utente con email {}.", user.getEmail());
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Errore durante la verifica dell'utente con email {}, {}", user.getEmail(), e.getMessage());
            return false;
        }
    }


    /**
     * Creates a session cookie for the user with the given session ID.
     *
     * @param sessionId the session ID of the user
     * @return the session cookie
     */
    private NewCookie createUserSessionCookie(String sessionId) {
        return new NewCookie.Builder(SESSION_ID)
                .value(sessionId)
                .path("/")
                .maxAge(3600)
                .secure(false)
                .build();
    }


    private void saveUserEmail(String sessionId, String userEmail) {
        userSessions.put(sessionId, userEmail);
    }


}
