package it.eda.shipments.consumer.service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.eda.shipments.consumer.model.DatabaseSequence;
import it.eda.shipments.consumer.model.ShipmentsDTO;
import it.eda.shipments.consumer.model.StatoEnum;
import it.eda.shipments.consumer.repository.ShipmentsRepository;

@Service
public class ShipmentsService {

	private static final Logger logger = LoggerFactory.getLogger(ShipmentsService.class);
	private final ShipmentsRepository spedizioneRepository;
	private final MongoOperations mongoOperations; 

	@Autowired
	public ShipmentsService(ShipmentsRepository spedizioneRepository, MongoOperations mongoOperations) {
		this.spedizioneRepository = spedizioneRepository;
		this.mongoOperations = mongoOperations;
	}

	public Long getNextIdSpedizioneSequence() {
		String sequenceName = "spedizioni_sequence";

		Query query = new Query(Criteria.where("_id").is(sequenceName));
		Update update = new Update().inc("seq", 1);
		FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);

		DatabaseSequence sequence = mongoOperations.findAndModify(query, update, options, DatabaseSequence.class);

		return sequence != null ? sequence.getSeq() : 1;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void saveSpedizione(ShipmentsDTO spedizione) {
		Long nextId = getNextIdSpedizioneSequence();
		spedizione.setIdSpedizione(nextId);
		spedizione.setStato(StatoEnum.ACCEPTED);

		spedizioneRepository.save(spedizione);
		logger.info("Spedizione saved successfully with ID: {} " , nextId);
	}
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateSpedizione(ShipmentsDTO spedizione) {
	    Query query = new Query(Criteria.where("idSpedizione").is(spedizione.getIdSpedizione()));
	    Update update = new Update();
	    AtomicBoolean updateNeeded = new AtomicBoolean(false);

	    BiConsumer<String, Object> addUpdateCondition = (fieldName, value) -> {
	        if (value != null) {
	            update.set(fieldName, value);
	            updateNeeded.set(true);
	        }
	    };

	    addUpdateCondition.accept("destinatario", spedizione.getDestinatario());
	    addUpdateCondition.accept("citta", spedizione.getCitta());
	    addUpdateCondition.accept("cap", spedizione.getCap());
	    addUpdateCondition.accept("dimensioneArticolo", spedizione.getDimensioneArticolo());
	    addUpdateCondition.accept("pesoArticolo", spedizione.getPesoArticolo());
	    addUpdateCondition.accept("indirizzo", spedizione.getIndirizzo());
	    addUpdateCondition.accept("note", spedizione.getNote());
	    update.set("stato", spedizione.getStato()); // Stato aggiunto direttamente poiché sempre presente
	    updateNeeded.set(true);

	    if (updateNeeded.get()) {
	        mongoOperations.findAndModify(query, update, ShipmentsDTO.class);
	        logger.info("Spedizione con ID: {} aggiornata con successo", spedizione.getIdSpedizione());
	    } else {
	        logger.info("Nessun campo da aggiornare per la spedizione con ID: {}", spedizione.getIdSpedizione());
	    }
	}


}
