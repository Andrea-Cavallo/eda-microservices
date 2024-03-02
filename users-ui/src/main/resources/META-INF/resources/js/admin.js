
console.log("carico script admin.js") ;
const sessionId = getCookie('sessionId');





document.addEventListener('DOMContentLoaded', async () => {
    if (!sessionId) {
        console.error('Session ID mancante: Devi prima effettuare il login...');
        window.location.href = 'http://localhost:8092/';
        return;
    }

    try {
        const url = new URL('/api/userdata', window.location.origin);
        url.searchParams.append('sessionId', sessionId);

        const response = await fetch(url.toString(), {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            },
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Errore nella validazione della sessione: ${response.statusText}`);
        }

        const userData = await response.json();
        if (userData.error) {
            throw new Error(userData.error);
        }

        await mostraTutteLeSpedizioni(); // Carica tutte le spedizioni all'avvio
    } catch (error) {
        console.error('Si è verificato un errore:', error);
    }
    document.getElementById('elencoSpedizioni').addEventListener('click', function(e) {
        if (e.target && e.target.className === 'idCliccabile') {
            const spedizioneId = e.target.getAttribute('data-id');
            // Apri il popup SweetAlert2 per modificare lo stato
            Swal.fire({
                title: 'Aggiorna lo stato per la spedizione con id: ' + spedizioneId,
                input: 'select',
                inputOptions: {
                    'PENDING': 'In attesa',
                    'ACCEPTED': 'Accettata',
                    'REFUSED': 'Rifiutata'
                },
                inputPlaceholder: 'Seleziona uno stato',
                showCancelButton: true,
                inputValidator: (value) => {
                    if (value) {
                        return aggiornaStatoSpedizione(spedizioneId, value);
                    } else {
                        return 'Devi selezionare uno stato';
                    }
                }
            });
        }
    });
});




async function mostraTutteLeSpedizioni(pageIndex = 0, pageSize = 10) {
    // Mostra il loader e nasconde eventuali messaggi di errore precedenti
    document.getElementById('loader').classList.remove('nascosto');
    document.getElementById('errore').classList.add('nascosto');

   // const sessionId = getCookie("sessionId");
    if (!sessionId) {
        console.error("Session ID non trovato. Assicurati di essere loggato.");
        document.getElementById('loader').classList.add('nascosto'); // Nasconde il loader se non c'è un sessionId
        return; // Interrompi l'esecuzione se non c'è un sessionId
    }

    try {


        const queryOrderMS = `http://shipments-read:8084/v1/shipments?pageIndex=${pageIndex}&pageSize=${pageSize}`;

        const response = await fetch(queryOrderMS, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': '*/*',
                'Accept-Encoding': 'gzip, deflate, br',
                'sessionId': sessionId
            }
        });
        //modifica con uno spinner questo non funziona
        document.getElementById('loader').classList.add('nascosto');

        if (!response.ok) {
            throw new Error(`Errore nel recupero delle spedizioni. Status: ${response.status}`);
        }

        const spedizioni = await response.json();
        const numeroTotaleSpedizioni = spedizioni.total; // Assumendo che 'spedizioni' abbia una proprietà 'total'
        const numeroTotalePagine = Math.ceil(numeroTotaleSpedizioni / pageSize);

        // Verifica se la pagina corrente è l'ultima e aggiorna lo stato dei pulsanti
        document.getElementById('nextPage').disabled = pageIndex >= numeroTotalePagine - 1;
        document.getElementById('prevPage').disabled = pageIndex <= 0;

        console.log("Spedizioni ricevute:", spedizioni);
        popolaTabellaSpedizioni(spedizioni);
        document.getElementById('leTueSpedizioni').classList.remove('nascosto');

        // Controlla il numero di spedizioni restituite per decidere se disabilitare il pulsante "avanti"
        document.getElementById('nextPage').disabled = spedizioni.length < pageSize;
        document.getElementById('prevPage').disabled = pageIndex <= 0;
    } catch (error) {
        console.error("Errore:", error);
        gestisciErrore("Errore nel recupero delle spedizioni", error.toString());
    }
}

async function aggiornaStatoSpedizione(spedizioneId, nuovoStato) {
    const asmaOrderProducer = `http://shipments-producer:8082/v1/shipments/${spedizioneId}`;

    // Crea un oggetto con i dati da inviare. Qui includi solo lo stato, ma puoi aggiungere altri campi se necessario.
    const datiSpedizione = {
        stato: nuovoStato,
        userEmail: 'admin@admin.it'

    };

    let serializedBody = JSON.stringify(datiSpedizione);
    console.log("Invio dati al microservizio con input body:", serializedBody);

    try {
        const response = await fetch(asmaOrderProducer, {
            method: 'PUT', // Utilizza il metodo PUT per aggiornare una risorsa esistente
            headers: {
                'Content-Type': 'application/json',
                'Accept': '*/*',
                'Accept-Encoding': 'gzip, deflate, br',
                'sessionId': sessionId
            },
            body: serializedBody,
        });

        if (!response.ok) {
            throw new Error('Errore nella richiesta di aggiornamento');
        }

        const responseData = await response.json();

        // Qui gestisci la risposta positiva, ad esempio mostrando un messaggio di successo
        Swal.fire('Aggiornato!', 'Lo stato della spedizione è stato aggiornato con successo.', 'success');
        return responseData; // Restituisce i dati della risposta se necessario

    } catch (error) {
        console.error('Errore durante l\'aggiornamento dello stato:', error);
        Swal.fire('Errore!', 'Non è stato possibile aggiornare lo stato della spedizione.', 'error');
        throw error; // Propaga l'errore per ulteriori gestioni
    }
}




function popolaTabellaSpedizioni(dati) {
    const tbody = document.getElementById('elencoSpedizioni');
    tbody.innerHTML = '';

    if (dati.output && dati.output.length > 0) {
        dati.output.forEach(spedizione => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
            <td><button class="idCliccabile" data-id="${spedizione.id}">${spedizione.id}</button></td>
            <td>${spedizione.destinatario}</td>
            <td>${spedizione.citta}</td>
            <td>${spedizione.cap}</td>
            <td>${spedizione.dimensioneArticolo}</td>
            <td>${spedizione.pesoArticolo}</td>
            <td>${spedizione.indirizzo}</td>
            <td>${spedizione.stato}</td>
        `;
            tbody.appendChild(tr);
        });
    } else {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td colspan="8">Nessuna spedizione trovata</td>`;
        tbody.appendChild(tr);
    }


}




function gestisciErrore(status, errore, dettagliAggiuntivi) {
    let erroreBase = `Errore: ${status}`;
    let dettagliErrore = errore ? errore : 'Nessun dettaglio disponibile.';
    let messaggioCompleto;

    if (status === 401) {
        messaggioCompleto = `${erroreBase} - Accesso non autorizzato. Assicurati di essere loggato correttamente.`;
    } else if (status === 404) {
        messaggioCompleto = `${erroreBase} - Risorsa non trovata. Verifica che la richiesta sia corretta.`;
    } else if (status === 500) {
        messaggioCompleto = `${erroreBase} - Errore interno del server. Riprova più tardi.`;
    } else {
        messaggioCompleto = `${erroreBase} - Dettagli: ${dettagliErrore}`;
    }

    if (dettagliAggiuntivi) {
        messaggioCompleto += ` Ulteriori informazioni: ${dettagliAggiuntivi}`;
    }

    console.error("Errore nella risposta:", messaggioCompleto);
    document.getElementById('contenuto-errore').textContent = messaggioCompleto;
    document.getElementById('errore').classList.remove('nascosto');
}


function getCookie(name) {
    let cookieArray = document.cookie.split(';');
    for (let i = 0; i < cookieArray.length; i++) {
        let cookiePair = cookieArray[i].split('=');
        if (name === cookiePair[0].trim()) {
            console.log("Dal cookie........")
            console.log(decodeURIComponent(cookiePair[1]));
            return decodeURIComponent(cookiePair[1]);
        }
    }
    return null;
}
let currentPageIndex = 0;
const pageSize = 10;
document.getElementById('prevPage').disabled = true; // Disabilita il pulsante all'avvio

document.getElementById('prevPage').addEventListener('click', () => {
    if (currentPageIndex > 0) {
        currentPageIndex--;
        mostraTutteLeSpedizioni(currentPageIndex, pageSize);
    }
});

document.getElementById('nextPage').addEventListener('click', () => {
    currentPageIndex++;
    mostraTutteLeSpedizioni(currentPageIndex, pageSize);
});