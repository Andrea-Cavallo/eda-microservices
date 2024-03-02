package it.eda.shipments.read.controller.model;


import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import it.eda.shipments.read.controller.model.enums.StatoEnum;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection="shipments")
public class Shipments extends PanacheMongoEntity implements MarkerInterface {
    private String destinatario;
    private StatoEnum stato;
    private String indirizzo;
    private String citta;
    private String cap;
    private String dimensioneArticolo;
    private Double pesoArticolo;
    private String userEmail;
    private Long idSpedizione;
    @BsonProperty("_id")
    private Long id;

}
