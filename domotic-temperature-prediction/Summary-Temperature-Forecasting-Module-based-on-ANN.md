# Some Empirical Evaluations of a Temperature Forecasting Module based on Artificial Neural Networks for a Domotic Home Environment

## Inleiding
- ANN's zijn succesvol bij niet lineaire voorspellingen, ookal is het onderliggend model onbekend.
- Bedoeling: Energie efficiëntie van huizen te verbeteren.
- Hoe:
  - Artificiële intellegentie technieken
  - Huis automatisatie
  - Standalone modules voor ieder subsysteem, die als agent fungeert.

## Domotic home environment setup
Setup van het huis beschrijven

### Hardware architecture
- Europese standaard KNX
- KNX modules groeperen per functionaliteit
- Werken zonder de Master Control Server (MCS)
- MCS kan status van de sensoren lezen en kan bijsturen via TCP/IP

### Software architecture
- Software bestaat uit 3 lagen:
  - KNX-IP brug:
    - Data ophalen van de KNX
  - Data persistentie laag
    - Bemonsteren
  - Verschillende applicaties
    - iOS applicatie
        - monitoren en controleren van de huidige staat
    - Intelegentie modules
- Het complete pakket van alle software en intellegentie modules zijn het controle en monitoring systeem.

## Data preprocessing
Temperatuur signaal in 3 opdelen:
1. ANN trainen
2. Model valideren
3. Testen

1 en 2 bevatten data van dezelfde dag, 3 bevat data van een week later.

## Neural networks description
- Het ANN krijgt het de uur component van de huidige tijd
- Het ANN berekent een window van mogelijke voorspellingen
