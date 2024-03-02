package it.eda.shipments.producer.message;


import it.eda.shipments.producer.controller.model.MarkerInterface;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SpedizioneMessage implements MarkerInterface {

	private String id;
	private SpedizioneDTO spedizione;
	private String dataProcessamentoTopic;
	private Boolean isUpdate;


}
