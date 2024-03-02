package it.eda.shipments.producer.controller.model;


import it.eda.shipments.producer.controller.model.enums.StatoEnum;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SpedizioneDTO implements MarkerInterface {

	private String destinatario;
	private StatoEnum stato;
	private String indirizzo;
	private String citta;
	private String cap;
	private String dimensioneArticolo;
	private Double pesoArticolo;
	private String userEmail;
	private String note;
	private Long idSpedizione;
}
