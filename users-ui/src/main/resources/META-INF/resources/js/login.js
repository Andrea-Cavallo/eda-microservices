console.log("carico lo script login js..");



async function effettuaLogin(event) {
    event.preventDefault();
    console.log("dentro la funzione effettuaLogin js..");

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    mostraSpinner();

    const loginInput = JSON.stringify({ "email": email, "password": password });
    console.log("Il body che invio al login =" + loginInput);

    try {

        const response = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: loginInput,
        });
        if (email === "admin@admin.it" && password === "admin") {
            nascondiSpinner();
            window.location.href = '/admin';
            return;
        }
        nascondiSpinner();

        if (response.ok) {
         //   const sessionId = getCookie('sessionId'); // Assumendo che questa funzione restituisca il valore del cookie sessionId
          //  sessionStorage.setItem('sessionId', sessionId); // Salv
           return window.location.href = 'user';
        }else if (response.status === 404) {
            Swal.fire({
                title: 'Attenzione!',
                text: 'Utente non registrato, controlla le tue credenziali.',
                icon: 'error',
                confirmButtonText: 'Ok'
            }).then((result) => {
                // Reindirizza dopo la chiusura del popup
                if (result.isConfirmed) {
                    window.location.href = '/';
                }
            });
            return;}

        else {
            gestisciErroreAPI(response);
        }
    } catch (error) {
        console.log("si è verificato un errore durante il login " + error);
        nascondiSpinner();
        mostraErroreGenerico();
    }
}

async function effettuaRegistrazione(event) {
    event.preventDefault();
    console.log("dentro la funzione effettuaRegistrazione js..");

    const nome = document.getElementById('reg-nome').value;
    const cognome = document.getElementById('reg-cognome').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    mostraSpinner();

    const inputRegister = JSON.stringify({ "nome": nome, "cognome": cognome, "email": email, "password": password });
    console.log("Il body che invio alla registrazione =" + inputRegister);

    try {
        let response = await fetch('/api/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: inputRegister,
        });

        nascondiSpinner();

        if (response.ok) {
            mostraSuccessoRegistrazione();
            window.location.href = 'http://localhost:8092/';
            return window.location.href;
        } else {
            gestisciErroreAPI(response);
        }
    } catch (error) {
        console.log(error); // Aggiunto per vedere l'errore nel log
        nascondiSpinner();
        mostraErroreGenerico();
    }
}

document.getElementById('mostra-registrazione').addEventListener('click', mostraFormRegistrazione);

function mostraFormRegistrazione() {
    toggleVisibility('registrazione-container', 'login-container');
}

function toggleVisibility(showId, hideId) {
    const showElement = document.getElementById(showId);
    const hideElement = document.getElementById(hideId);

    if (showElement && hideElement) {
        showElement.classList.remove('nascosto');
        hideElement.classList.add('nascosto');
    } else {
        console.error('Elemento da mostrare o nascondere non trovato');
    }
}


function mostraFormLogin() {
    document.getElementById('login-container').classList.remove('nascosto');
    document.getElementById('registrazione-container').classList.add('nascosto');
    document.getElementById('messaggio-errore').classList.add('nascosto'); // Opzionale: nascondere messaggi di errore
}


function mostraErrore(messaggio) {
    const contenitoreErrore = document.getElementById('messaggio-errore');
    contenitoreErrore.textContent = messaggio;
    contenitoreErrore.classList.remove('nascosto');
}

function mostraSuccessoRegistrazione() {
    const contenitoreSuccesso = document.getElementById('registrazione-successo');
    contenitoreSuccesso.textContent = 'Registrazione completata con successo. Effettua il login.';
    contenitoreSuccesso.classList.remove('nascosto');
    document.getElementById('form-registrazione').classList.add('nascosto'); // Nascondi il form di registrazione
}




function mostraErroreGenerico() {
    mostraErrore('Si è verificato un errore. Riprova più tardi o contatta il supporto se il problema persiste.');
}

function gestisciErroreAPI(response) {
    console.log("dentro la funzione gestisciErroreAPI Js..");

    switch (response.status) {
        case 400:
            mostraErrore('Errore nella richiesta. Controlla i dati inseriti.');
            break;
        case 401:
            mostraErrore('Credenziali non valide.');
            break;
        case 403:
            mostraErrore('Utente non autorizzato.');
            break;
        case 404:
            mostraErrore('Pagina non trovata 404.');
            break;
        case 500:
            mostraErrore('Errore del server. Riprova più tardi.');
            break;
        default:
            console.log("Non è ne un 400, ne un 401, ne un 403 ne 404 un 500");

            response.json().then(data => {
                // Check se all'interno di data c'e' anche la prop. messaggio..
                if (data && typeof data === 'object' && 'messaggio' in data) {
                    mostraErrore(data.messaggio);
                } else {
                    mostraErroreGenerico();
                }
            }).catch(error => {
                console.error("Errore nel parsing della risposta JSON:", error);
                mostraErroreGenerico();
            });
    }
}

function mostraSpinner() {
    const spinner = document.getElementById('spinner');
    spinner.classList.remove('nascosto');
}

function nascondiSpinner() {
    const spinner = document.getElementById('spinner');
    spinner.classList.add('nascosto');
}


