package it.eda.shipments.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ShipmentsMessage implements MarkerInterface {

	private String id;
	private ShipmentsDTO spedizione;
	private String dataProcessamentoTopic;
	private Boolean isUpdate;

}
