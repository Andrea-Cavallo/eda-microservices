package it.eda.users.ui.repo;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import it.eda.users.ui.model.session.Session;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

import static it.eda.users.ui.utils.Constants.*;
@Singleton
@Slf4j
public class RedisSessionRepo {

    @Inject
    RedisDataSource redisDataSource;

    /**
     * Saves the session in Redis.
     *
     * @param session the session to be saved
     */
    public void saveSession(Session session) {
        HashCommands<String, String, String> commands = redisDataSource.hash(String.class, String.class, String.class);
        commands.hset(SESSION + session.getSessionId(), Map.of(EMAIL, session.getEmail(), ROLE, session.getRole()));
    }

    /**
     * Retrieves the session with the given session ID from Redis.
     *
     * @param sessionId the session ID
     * @return an Optional containing the session if found, or an empty Optional if not found
     */
    public Optional<Session> getSession(String sessionId) {
        log.info("Cerco su Redis la sessione con sessionId: {}", sessionId);
        HashCommands<String, String, String> commands = redisDataSource.hash(String.class, String.class, String.class);
        Map<String, String> map = commands.hgetall(SESSION + sessionId);
        if (map == null || map.isEmpty()) {
            log.info("Nessuna sessione trovata per sessionId: {}", sessionId);
            return Optional.empty();
        } else {
            String email = map.get(EMAIL);
            String role = map.get(ROLE);
            log.info("Sessione trovata per sessionId: {} Email: {}, Ruolo: {}", sessionId, email, role);
            return Optional.of(new Session(sessionId, email, role));
        }
    }

    /**
     * Invalidates a session for the given session ID by deleting it from the Redis cache.
     *
     * @param sessionId the session ID of the session to invalidate
     */
    public void invalidateSession(String sessionId) {
        log.info("Invalido la sessione con sessionId: {}", sessionId);
        redisDataSource.key().del(SESSION + sessionId);
    }
}
