// Connessione al database specifico per operazioni specifiche di quel DB
const db = db.getSiblingDB('shipmentManagement');

// Creazione della collezione shipments se non esiste
if (!db.getCollectionNames().includes('shipments')) {
  db.createCollection('shipments');
}

// Creazione della collezione database_sequences se non esiste
if (!db.getCollectionNames().includes('database_sequences')) {
  db.createCollection('database_sequences');
}

// Rimuovi i record esistenti
db.shipments.deleteMany({});

// Inserisci i nuovi record con _id come numeri chiari
db.shipments.insertMany([
    { _id: 0, destinatario: "Mario", stato: "PENDING", indirizzo: "viale europa 175", citta: "Roma", cap: "00144", dimensioneArticolo: "10*10", pesoArticolo: 4, userEmail: "prova2@gmail.com", idSpedizione: 0, note: "" },
    { _id: 1, destinatario: "Luigi", stato: "PENDING", indirizzo: "viale italia 100", citta: "Milano", cap: "20100", dimensioneArticolo: "15*15", pesoArticolo: 5, userEmail: "prova2@gmail.com", idSpedizione: 1, note: "Urgente" },
    { _id: 2, destinatario: "Anna", stato: "DELIVERED", indirizzo: "corso francia 20", citta: "Torino", cap: "10143", dimensioneArticolo: "20*20", pesoArticolo: 2, userEmail: "prova2@gmail.com", idSpedizione: 2, note: "" }
]);

// Aggiorna la sequenza nel documento database_sequences
db.database_sequences.updateOne({ _id: "spedizioni_sequence" }, { $set: { seq: 3 } });


// Creazione degli indici se non esistono
db.shipments.createIndex({ idSpedizione: 1 }, { unique: true });
db.shipments.createIndex({ userEmail: 1 });
db.shipments.createIndex({ citta: 1 });
db.shipments.createIndex({ stato: 1 });

// Inizializza la sequenza per idSpedizione se non esiste
if (!db.database_sequences.findOne({ _id: "spedizioni_sequence" })) {
  db.database_sequences.insertOne({
    _id: "spedizioni_sequence",
    seq: 3
  });
}
