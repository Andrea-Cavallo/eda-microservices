package it.eda.shipments.producer.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.message.SpedizioneMessage;
import it.eda.shipments.producer.utils.OrderProducerUtils;
import jakarta.enterprise.context.RequestScoped;


@RequestScoped
public class SpedizioneMapper {

	/**
	 * Converte una spedizioneRequest in una SpedizioneMessage rappresentate il topic
	 * Genera un Id univoco UUID, e setta una LocalDateTime.now() in formato Stringa
	 *
	 * @param spedizioneRequest l'oggetto da convertire
	 * @return il topic SpedizioneMessage
	 */
	public SpedizioneMessage toSpedizioneMessage(SpedizioneDTO spedizioneRequest) {
		String id = UUID.randomUUID().toString();
		return  SpedizioneMessage.builder() .id(id).spedizione(spedizioneRequest)
				.dataProcessamentoTopic(OrderProducerUtils.stringValue(LocalDateTime.now())).build();
	}

}
