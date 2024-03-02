package it.eda.shipments.consumer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "shipments")
public class ShipmentsDTO implements MarkerInterface {

	private String destinatario;
	private StatoEnum stato;
	private String indirizzo;
	private String citta;
	private String cap;
	private String dimensioneArticolo;
	private Double pesoArticolo;
	private String userEmail;
	private String note;

	@Id
	private Long idSpedizione;
}
