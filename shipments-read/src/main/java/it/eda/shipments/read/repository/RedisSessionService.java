package it.eda.shipments.read.repository;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import it.eda.shipments.read.controller.model.Session;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import io.quarkus.redis.datasource.hash.ReactiveHashCommands;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;


import java.util.Map;
import java.util.Optional;

/**
 * Servizio di gestione Session, con ReactiveRedis
 */
@Singleton
public class RedisSessionService {

    public static final String EMAIL = "email";
    private static final Logger LOG = Logger.getLogger(RedisSessionService.class);
    public static final String ROLE = "role";

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    /**
     * Salva i dettagli della sessione in Redis.
     *
     * @param session L'oggetto sessione da salvare.
     *
     * @return Uni<Void> per indicare il completamento.
     */
    public Uni<Void> saveSession(Session session) {
        LOG.info("Salvo su Redis la sessione  : "+ session);
        ReactiveHashCommands<String, String, String> commands = reactiveRedisDataSource.hash(String.class, String.class, String.class);
        return commands.hset("session:" + session.getSessionId(), Map.of(EMAIL, session.getEmail(), ROLE, session.getRole()))
                .onFailure().invoke(e -> LOG.errorf("Errore nel salvare la sessione: %s", e.getMessage())).replaceWithVoid();
    }

    /**
     * Recupera i dettagli della sessione da Redis.
     *
     * @param sessionId L'ID della sessione da recuperare.
     * @return Uni<Optional<Session>> contenente la sessione, se presente.
     */
    public Uni<Optional<Session>> getSession(String sessionId) {
        LOG.info("Cerco su redis la sessione con sessionId  : "+ sessionId);

        ReactiveHashCommands<String, String, String> commands = reactiveRedisDataSource.hash(String.class, String.class, String.class);
        return commands.hgetall("session:" + sessionId)
                .onItem().transform(map -> {
                    if (map == null || map.isEmpty()) {
                        return Optional.<Session>empty();
                    }
                    String email = map.get(EMAIL);
                    String role = map.get(ROLE);
                    Session session = new Session(sessionId, email, role);
                    return Optional.of(session);
                })
                .onFailure().invoke(e -> LOG.errorf("Errore nel recuperare la sessione: %s", e.getMessage()));
    }


    /**
     * Invalida una sessione rimuovendola da Redis.
     *
     * @param sessionId L'ID della sessione da invalidare.
     * @return Uni<Void> per indicare il completamento.
     */
    public Uni<Void> invalidateSession(String sessionId) {
        ReactiveKeyCommands<String> commands = reactiveRedisDataSource.key(String.class);
        return commands.del("session:" + sessionId)
                .onFailure().invoke(e -> LOG.errorf("Errore nell'invalidare la sessione: %s", e.getMessage())).replaceWithVoid();
    }
}
