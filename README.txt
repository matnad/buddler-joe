# Buddler Joe ReadMe

## INFOS FÜR MILESTONE 5

Programm wird über /src/main/java/Main.java gestartet und unterstützt folgende Kommandozeilen Parameter:

Client starten:
[client] [<hostadress>[:<port>]] [<username>]

Alle Parameter sind optional. Für fehlende Parameter werden die zuletzt verwendeten Werte genommen (ist in den Settings lokal gespeichert) oder, falls keine Settings gefunden wurden, die Default Werte (client buddlerjoe.ch:11337 "Joe Buddler"). Falls Parameter übergeben werden, so werden diese in den Settings gespeichert.

Server starten:
server [<port>]

Um den Server zu starten muss das erste Argument "server" lauten. Der Port ist optional und wird auf 11337 gesetzt falls nicht angegeben.

Es läuft ein Server mit der aktuellen master Version unter der hostaddresse "budlerjoe.ch" auf welchen man ohne Einschränkung verbinden kann.

Sobald man zu server verbunden ist (sollte beim Spielstart automatisch passieren) kann in der Konsole "help" eingegeben werden für eine Liste mit allen Befehlen. Lobbies erstellen funktioniert im Moment zum Beispiel nur per Konsole.

Dokumente für Milestone 2:
- Tagebuch (Diary): https://sites.google.com/view/buddler-joe/
- Netzwerkprotokolldokumentation: docs\milestone2\Netzwerkprotokoll\NetzwerkProtokollDokumentation.pdf
- QA Konzept: docs\milestone2\QualityAssurance\QualityAssuranceKonzept.pdf

Dokumente für Milestone 3:
- Tagebuch (Diary): https://sites.google.com/view/buddler-joe/
- Netzwerkprotokolldokumentation: docs\milestone3\Netzwerkprotokoll\NetzwerkProtokollDokumentation_Buddler_Joe
- QA Konzept: docs\milestone3\QualityAssurance\QualityAssuranceKonzept.pdf
- Manual: docs\milestone3\Manual\BuddlerJoeManual.pdf
- Präsentation Milestone 3: docs\milestone3\Milestone3Praesentation.pdf
- Demo videos: docs\milestone3\demos\2019-04-07 20-23-46.mkv
	       docs\milestone3\demos\2019-04-07 20-36-44.mkv
- Teaser:      docs\milestone3\teaser\teaser.avi

Dokumente für Milestone 5:
- Tagebuch (Diary): https://www.buddlerjoe.ch/entwickler-tagebuch
- Netzwerkprotokolldokumentation: docs\milestone5\NetzwerkprotokollDokument\NetzwerkProtokollSpezifikation.pdf
- Manual: docs\milestone5\manual\BuddlerJoeManual.pdf
- QA Dokument: docs\milestone5\QualityAssuranceKonzeptl\QualityAssuranceKonzept.pdf
- Logo: docs\milestone5\Logo.png
- Archidekturdokument: docs\milestone5\ArchidekturDokument\Architektur.pdf
- Twitchy: docs\milestone5\Buddler Joe - 2 minutes of gameplay.mp4
- Präsentation: docs\milestone5\Präsentation\Milestone5Präsentation.pdf

## DEPENDENCIES
- **LWJGL**: LightWeight Java Game Library
- **JOML**: Math Library
- **OPENGL**: Muss auf dem System installiert sein. Mindestens version 4.00 core

## ANDERE WICHTIGE DOKUMENTE
- ProjektTimeline: docs\gantt\Buddler-Joe-Projekttimeline.gan
- Präsentation Milestone 1: docs\milestone1\Präsentation_Buddler_Joe_MS1.pdf
- Sitzungsprotokolle: docs\protokolle