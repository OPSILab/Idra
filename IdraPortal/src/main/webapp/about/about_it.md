### Cos'è Idra?

Idra è un'applicazione web capace di federare Open Data Management System (ODMS) esistenti basati su diverse tecnologie, fornendo un unico punto di accesso per la ricerca di dataset open proventienti da sorgenti eterogenee. Idra rende uniforme la rappresentazione dei dataset collezionati, grazie all'adozione di standard internazionali (DCAT-AP) e fornisce un set di RESTful API per garantire l'accesso ad applicazioni di terze parti.

Idra supporta nativamente ODMS basati su [CKAN](https://ckan.org/), [DKAN](https://getdkan.org/), [Socrata](https://socrata.com/), Orion Context Broker ([NGSI v2](https://swagger.lab.fiware.org/?url=https://raw.githubusercontent.com/Fiware/specifications/master/OpenAPI/ngsiv2/ngsiv2-openapi.json)) e molte altre tecnologie: Idra prevede un set di API per federare ODMS non supportati nativamente. In aggiunta, è possibile federare portali open data generici, che non espongono API, utilizzando la funzionalità di web scraping o fornendo un file dump dei dataset nel formato [DCAT-AP](https://joinup.ec.europa.eu/solution/dcat-application-profile-data-portals-europe). Inoltre, Idra espone un endpoint [SPARQL](https://www.w3.org/TR/sparql11-query/) al fine di eseguire query su RDF linked open data collezionati dagli ODMS federati e permette la creazione di grafici basati sui dataset aperti federati. 

### Perché usare Idra?

Uno dei problemi maggiori, che limita l'adozione e l'uso di open data, è quello della frammentazione: ogni entità che desidera fornire open data (ad esempio una Pubblica Amministrazione) utilizza piattaforme e formati diversi per la loro pubblicazione. Questo rappresenta un ostacolo sia per la difficoltà di ricerca e fruizione dei dati, sia per il loro riutilizzo da parte di applicazioni esterne. Idra è una piattaforma open source che risolve tale problema, fornendo un unico punto di accesso agli open data, di pubbliche amministrazioni o enti privati, provenienti da sorgenti e portali basati su tecnologie eterogenee.

L'adozione e l'uso di Idra può produrre benefici diretti per differenti stakeholder: 

#### Vantaggi per le Pubbliche Amministrazioni:

-   Rendere i propri open data “standard” esponendoli attraverso API aperte e formati dati standard (DCAT_AP) in linea con le direttive Europee vigenti. Le API 	possono essere utilizzate da altri portali di federazione di open data.
-	Fornire al cittadino, a costo zero, nuove funzionalità non presenti nei propri portali nativi (ad esempio le query SPARQL, la visualizzazione dei dataset, etc)
-	Maggiore visibilità dei propri open data attraverso un sistema federato accessibile da un maggior numero di utenti

#### Vantaggi per i cittadini

-   Avere un unico punto di accesso per gli open data senza necessità di conoscere il portale di origine
-   Avere funzionalità avanzate di ricerca e di visualizzazione non presenti nei portali di origine

#### Vantaggi per le aziende

-	Poter accedere una grande quantità di open data in maniera univoca e a relative funzionalità di data analytics e visualizzazione per i più differenti scopi (ad 	esempio statistiche, ricerche di settore specifiche, riutilizzo dei dati per la costruzione di applicazioni innovative a valore aggiunto)

Il progetto **Idra** è parte di [FIWARE](https://fiware.org/).