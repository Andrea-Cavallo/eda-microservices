package it.eda.shipments.consumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.eda.shipments.consumer.model.ShipmentsDTO;

@Repository
public interface ShipmentsRepository extends MongoRepository<ShipmentsDTO, Long> {

}
