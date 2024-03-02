package it.eda.shipments.read.repository;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import it.eda.shipments.read.controller.model.Shipments;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class represents a repository for managing Spedizione entities.
 */
@ApplicationScoped
public class SpedizioneRepository implements ReactivePanacheMongoRepository<Shipments> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpedizioneRepository.class);

    /**
     * Finds shipments for a user with the specified email address.
     *
     * @param email     The email address of the user.
     * @param pageIndex The index of the page to retrieve.
     * @param pageSize  The number of shipments per page.
     * @return A Uni object that emits a List containing the shipments found, or an empty list if an error occurs.
     */
    public Uni<List<Shipments>> findByEmail(String email, int pageIndex, int pageSize) {
        return find("userEmail", email)
                .page(pageIndex, pageSize)
                .list()
                .onFailure().invoke(e -> LOGGER.error("Error querying shipments by email: {}", email, e));
    }

    /**
     * Retrieves a list of shipments based on the specified page index and page size.
     *
     * @param pageIndex The index of the page to retrieve.
     * @param pageSize  The number of shipments per page.
     * @return A Uni object that emits a List containing the shipments found, or an empty list if an error occurs.
     */
    public Uni<List<Shipments>> findAllShipments(int pageIndex, int pageSize) {
        return findAll()
                .page( Page.of(pageIndex, pageSize))
                .list()
                .onFailure().invoke(e -> LOGGER.error("Error querying all shipments", e));
    }
}
