# Smart House Automation System for the Elderly and the Disabled

## Inleiding
Een intelligent house automation systeem dat ouderen en gehandicapten in de gaten kan houden, zodat ze zelfstandig kunnen leven. Zo een systeem steunt op ontwikkelingen in het veld van AI, meer bepaald ANN's.

## Architectuur van een smart system
Niet echt belangrijk te weten voor ons.

## Principe en methodologie
ANN's worden gebruikt om de gewoonten van de ouderen of gehandicapten te leren. Zo een netwerk kan gebruikt worden om een systeem te modelleren zonder a priori kennis van zijn interne functie. 

Een neuron oefent 3 functies uit:
- Input functie die de gewogen som is van de input data en van de gewichten van het netwerk.
- Activatie functie: sigmoïde.
- Ouput functie: lineaire functie.

De neuronen zijn geordend in lagen, met 1 verborgen laag.

Voor de implementatie van het systeem zijn er 3 fases:
- Leren van de gewoontes van de ouderen/gehandicapten met de ontwerper.
- Testen met een analyse van valse alarmen (als er zijn).
- Dagelijkse behandeling.

## Functionaliteiten van het smart monitoring systeem

### Aanwezigheid/afwezigheid
Er wordt verondersteld dat de oudere een regelmatige tijdstabel heeft in zijn woning. Eenmaal het systeem met meerdere sensoren actief is, kan data van de sensoren opgenomen worden. Een geschiedenis van alle aankomsten/vertrekken van de persoon kan verkregen worden, als ook met hun tijd en frequentie. Vanaf een persoon de woning binnenkomt, zou de geschiedenis moeten kunnen aanwijzen of deze persoon de bewoner is of niet. (Er zijn extra "size" sensoren die moeten helpen bij de identificatie). Bezoeken aan de bewoner worden eerst beschouwd als ruis.

Deze opgenomen data vormt de learning data base, wat ook de tijdstabel geeft. Een ANN is dan gebouwd om die te leren. Het systeem van sensoren zal in real-time de aanwezigheid of afwezigheid van de bewoner aanwijzen. In het geval van een onregelmatigheid tussen de voorspelling gegeven door het ANN en het antwoord van het multisensor systeem, zal een alarm afgaan.

### Locatie
Meer complex, genereert een tijdstabel met activiteitscycli.

### Mobiliteit
Meet het aantal kilometers afgelegd sinds een tijdsoorsprong en voor een bepaalde duur. Net zoals andere functies wordt het leren van de gewoontes uitgevoerd door het ANN.

### Comfort
Het systeem voert een optimalisatie voor stroom uit voor verwarming zonder dat er tussenkomst van de gebruiker nodig is.
- Leren van de gewoontes van de gebruiker (comfort temperatuur).
- Leren van de gebruikelijke temperaturen in de kamer volgens omgevingsparameters.
- Leren va de tijdstabel van de gebruiker (aanwezigheid/afwezigheid).
