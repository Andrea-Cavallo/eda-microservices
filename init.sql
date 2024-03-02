CREATE TABLE IF NOT EXISTS utente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

INSERT INTO utente (nome, cognome, email, password) VALUES
('Mario', 'Rossi', 'mario.rossi@example.com', 'password1'),
('Luigi', 'Bianchi', 'luigi.bianchi@example.com', 'password2'),
('Anna', 'Verdi', 'anna.verdi@example.com', 'password3');
