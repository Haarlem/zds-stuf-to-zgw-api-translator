# OpenZaakBrug #

[![Build Status](https://travis-ci.com/Sudwest-Fryslan/OpenZaakBrug.svg?branch=master)](https://travis-ci.com/Sudwest-Fryslan/OpenZaakBrug) travis-ci

[![codecov](https://codecov.io/gh/Sudwest-Fryslan/OpenZaakBrug/branch/master/graph/badge.svg)](https://codecov.io/gh/Sudwest-Fryslan/OpenZaakBrug) codecov.io

Deze applicatie is een proof-of-concept van een service die een vertaling kan doen van StUF-ZDS Soap-berichten naar de corresponderende ZGW API-calls. 
Met deze proof-of-concept kan aantoond worden welke functionaliteiten er nodig zijn door gemeente, maar ook aan te tonen dat het ook daadwerkelijk mogelijk is.
Deze implemenatie kan als voorbeeld dienen voor een software-leveranier, om zo uiteindelijk de gemeenten deze functionaliteiten te kunnen aanbieden 

## Over OpenZaakBrug
De API-standaarden voor zaakgericht werken stellen gemeenten in staat om de informatievoorziening rondom zaakgericht werken in te richten volgens de informatiekundige visie Common Ground. Hiermee zetten ze een eerste stap in de richting van een modernisering van het ICT-landschap. Dit helpt bij het (ont)koppelen van processystemen en de opslagcomponenten waar documenten en overige informatie in zijn opgeslagen.

Wij willen niet meer investeren in de oude koppelingen van het zaaksgericht werken (ZDS standaard) en willen zo snel mogelijk de nieuwe Zaakgericht Werken-api&#39;s gebruiken. Daarom willen we zo snel mogelijk de nieuwe opslaginfrastructuur op basis van deze standaard aanbieden. Daarna kunnen alle nieuwe applicaties via deze nieuwe standaard aansluiten.

Omdat de bestaande applicaties niet de nieuwe standaard ondersteunen kunnen deze niet aangesloten worden op het nieuwe zaaksysteem. Door een component te ontwikkelen welke de communicatie omzet van de oude standaard (ZDS) naar de nieuwe (ZGW) hoeven de bestaande applicaties niet op hetzelfde moment te worden aangepast.

Het uiteindelijke doel is om alle applicaties op de nieuwe Zaakgericht Werken-api&#39;s te krijgen, met deze aanpak ontstaat er tijd om beheerst en onder regie dit uit te voeren.

Wij willen hiervoor een vertaal component maken die ervoor zorgt dat de bestaande ZDS- applicaties kunnen aansluiten op de nieuwe zaak en document opslagcomponenten.

Hierbij hebben we de volgende uitgangspunten:

- Het doel is het aansluiten van de bestaande ZDS-applicaties
- De oplossing hoeft niet aan de volledige ZDS-standaard te voldoen (minimaal vereiste functionele ondersteuning)
- De programmatuur moet makkelijk herbruikbaar zijn
- De broncode moet overdraagbaar zijn en naar wens aanpasbaar zijn door gemeenten
- Het betreft een tijdelijke oplossing, de leveranciers moeten over naar ZGW

## Technische informatie
- informatie over de installatie kijk in : [Installing Open Zaakbrug.md](docs/Installing Open Zaakbrug.md)  
- informatie over het aanroepen van der service en het vertalen en repliceren kijk : [Workings of Replication.md](docs/Workings of Replication.md) 

## Status
Op dit moment wordt de Open Zaakbrug getest door de functioneelbeheerders van één applicatie, hierbij wordt gekeken of de applicatie zich gedraagd zoals het voorheen werkte met het legacy (ZDS) zaaksysteem. Daarnaast kan natuurlijk ook de https://github.com/Sudwest-Fryslan/DeSleepTool gebruikt worden om hiermee documenten aan bestaande zaken in een ZGW server (zoals OpenZaak) toe gevoegen

Onderdelen en de stand van zaken:
- **Proxy** : Verstuurd de berichten door naar het oude, legacy zaaksysteem. Status = getest door functioneelbeheer en werkt
- **Translate** : Vertaald de ZDS berichten naar ZGW en geeft weer netjes een antwoord in ZDS. Status = draait in de test-omgeving en wordt getest door functioneelbeheer 
- **Replicate**:  Combinatie van de bovenste 2, de berichten worden vertaald EN doorgestuurd naar het nieuwe systeem. Wanneer er verwezen wordt naar een zaakid /documentid welke niet bestaat in openzaak, dan wordt deze informatie uit het oude systeem gecopieerd naar openzaak
