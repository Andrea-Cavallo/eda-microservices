
console.log("carico script user.js") ;
const sessionId = getCookie('sessionId');




document.addEventListener('DOMContentLoaded', async () => {

    if (!sessionId) {
        console.error('Session ID mancante: Devi prima effettuare il login...');
        window.location.href = 'http://localhost:8092/';
        return window.location.href;
    }

    try {
        const url = new URL('/api/userdata', window.location.origin);
        url.searchParams.append('sessionId', sessionId);

        const response = await fetch(url, {
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


    } catch (error) {
        console.error('Si è verificato un errore:', error);
    }
});



async function inviaDatiSpedizione(event) {
    event.preventDefault();
    console.log("Script js caricato...");
    document.getElementById('loader').style.display = 'block';

    const sessionId = getCookie("sessionId");
    if (!sessionId) {
        console.error("Session ID non trovato. Assicurati di essere loggato.");
        document.getElementById('loader').style.display = 'none';
        return;
    }

    try {
        const userEmail = await getUserEmailFromSession(sessionId);
        if (!userEmail) {
            throw new Error("Impossibile ottenere l'email dell'utente dalla sessione");
        }
        console.log("prima di formare il body spedizione l'email è " + userEmail);

        const formSpedizione = document.getElementById('form-spedizione');
        const formData = new FormData(formSpedizione);
        const datiSpedizione = {
            destinatario: formData.get("destinatario"),
            citta: formData.get("citta"),
            cap: formData.get("cap"),
            dimensioneArticolo: formData.get("dimensioneArticolo"),
            pesoArticolo: formData.get("pesoArticolo"),
            userEmail: userEmail,
            indirizzo: formData.get("indirizzo"),
            note: formData.get("note")
        };
        const asmaOrderProducer = 'http://shipments-producer:8082/v1/shipments';

        let serializedBody = JSON.stringify(datiSpedizione);
        console.log("Invio dati al microservizio con input body:", serializedBody);

        const response = await fetch(asmaOrderProducer, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': '*/*',
                'Accept-Encoding': 'gzip, deflate, br',
                'sessionId': sessionId

            },
            body: serializedBody,
        });

        document.getElementById('loader').style.display = 'none';

        if (response.ok) {
            const risposta = await response.json();
            console.log("Risposta del microservizio:", risposta);
            document.getElementById('contenuto-risposta').textContent = response.status === 201 ? "L'ordine è stato evaso con successo." : JSON.stringify(risposta, null, 2);
            document.getElementById('risposta').classList.remove('nascosto');
            document.getElementById('azioni-post-risposta').classList.remove('nascosto');
        } else {
            const errore = await response.text();
            console.log("Errore nella risposta del server:", errore);
            gestisciErrore(response.status, errore);
        }
    } catch (error) {
        console.error("Errore nella chiamata al microservizio:", error);
        document.getElementById('contenuto-errore').textContent = "Errore nella comunicazione con il microservizio.";
        document.getElementById('errore').classList.remove('nascosto');
        document.getElementById('loader').style.display = 'none';
    }
}

async function mostraSpedizioni(pageIndex = 0, pageSize = 10) {
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
        // Ottieni l'email dell'utente dalla sessione
        const userEmail = await getUserEmailFromSession(sessionId);
        if (!userEmail) {
            throw new Error("Impossibile ottenere l'email dell'utente dalla sessione");
        }


        const queryOrderMS = `http://shipments-read:8084/v1/shipments/${userEmail}?pageIndex=${pageIndex}&pageSize=${pageSize}`;

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

async function getUserEmailFromSession(sessionId) {
    const response = await fetch('/api/userdata', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Cookie': `sessionId=${sessionId}`
        }
    });

    if (!response.ok) {
        console.error("Impossibile ottenere i dati utente:", response.statusText);
        return null;
    }

    const userData = await response.json();
    return userData.email;
}




function popolaTabellaSpedizioni(dati) {
    const tbody = document.getElementById('elencoSpedizioni');
    tbody.innerHTML = '';

    if (dati.output && dati.output.length > 0) {
        dati.output.forEach(spedizione => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                 <td>${spedizione.id}</td>
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
        mostraSpedizioni(currentPageIndex, pageSize);
    }
});

document.getElementById('nextPage').addEventListener('click', () => {
    currentPageIndex++;
    mostraSpedizioni(currentPageIndex, pageSize);
});