# Kurze Übersicht der Struktur in Buddler Joe

## DEPENDENCIES
- **LWJGL**: LightWeight Java Game Library
- **JOML**: Erweiterte Vektoren und Matrizen Mathe
- **OPENGL**: Muss auf dem System installiert sein. Mindestens version 4.00 core

## Main / Java
- **Main**: Startet das Spiel und verbindet zum Server

## BIN
- **Game**: Game Loop mit allen Settings und im Moment noch einiges an überschüssigem Code der noch keinen anderen Platz gefunden hat

## EINTTIES
- **Entity**: Die Masterklasse für alle Entitäten im Spiel (alles mit einem Modell und Koordinaten). Spieler, Bäume und Blocks werden alle von dieser Klasse abgeleitet.
- **Blocks**: Hier entsteht dann die Abstrakte Klasse füt Blocks und alle Blockarten.
- **(Net)Player**: Player ist eine Erweiterung von NetPlayer, da dieser zusätzliche Eigenschaften definiert. Wir können die genaue Struktur hier noch besprechen, je nach dem was für die Netzwerk Devs mehr Sinn macht! **Hier sind die ganzen Commands für Laufen, Springen, etc mit drin und ein grosser Teil der Spiellogik**.
- **Camera/Light**: Kamera definiert was wir sehen und gibt dem Shader die viewMatrix zum Objekte berechnen. Mit der Licht Klasse und den Normal Vectors der modelle kann der Fragment Shader die Helligkeit von jedem Pixel auf einem Objekt berechnen.

## GUI
- **TextInput**: Noch leer, hier kommen Klassen hin um User Input zu parsen und lesen. Z.b. eine Klasse welche Key Inputs zu Text umwandelt für den Chat. Das ganze ist nicht so trivial bei einem Echtzeit Spiel! :) Evtl. müssen wir auch nicht alles selber schreiben.
- **RestlicheFiles**: GUI und HUD Elemente auf dem Bildschrim. Chatbox, Username, FPS Anzeige, usw.

## NET
- **Packets**: Pakete welche wir im UDP Protokoll verwenden werden. Login, Move, BlockStatus, etc.
- **Logic**: Empfangen, verarbeiten, aufbereiten und senden der Pakete.
- **StartServer**: Damit startet die ServerLogic in einem Thread auf welchen die Clients mit Main.java connecten.
- In diesen Ordner kommt alles, was nicht lokal auf dem Gaming PC läuft. Also auch die ganzen Server Gamestate Geschichten. Lobbies, Server Browser, etc.

## TERRAINS
- **Terrains**: Klassen um die Terrain Maps zu generieren. Hauptklasse für flaches Terrain (wie unsere Wand) und eine abgeleitete für Terrain mit einer Heightmap.

## UTIL
- **GLFW / IO**: Externe Files mit nützlichen Funktionen. Bisher nur zum Files lesen und für bessere Callbacks.
- **Maths**: Alle Mathe Funktionen welche wir öfters brauchen. Z.B. die ganzen Transformationen und BarryCentric um pixelgenaue Höhe von Objekten zu lesen (bisher nur für Terrain gebraucht).
- **Mouse Picker**: Raytracing. Transformation von Mausklick zu 3D Koordinaten. Die Klasse funktioniert noch nicht, daran arbeite ich gerade.. und der grösste Teil wird wohl in engine.io.InputHandler kommen und MousePicker lösche ich wieder.
- **RandomNamen**: Zufälliges Adjektiv und ein Tiername.. ist ja eines der Achievements mit dem random Namen :D

### TEXTURES

- **Textures**: Sammeln Eigenschaften für die verschiedenen Texturarten. So können wir z.B. Transparenz, Reflektivität oder Material einstellen. TexturPacks sind auch schon supportet (mehrere Texturen auf einem Bild für die gleichen Objekte -> viel effizienter).


## COLLISION
- **BoundingBox**: Klasse die beim konvertieren der .obj Dateien mitgeneriert wird und die Dimensionen eines Models mit 6 Koordinaten beschreibt. Brauchen wir um zu wissen wann die Figur einen Block erreicht hat z.B.


## ENGINE
Dieses Package ist ein wenig technischer und enthält den grössten Teil der openGL Logik.

### IO (input/output)
- **InputHandler**: Verwaltet allen input von Keyboard und Maus. Callback funktionen und speichern der states im letzten Frame um single presses handeln zu können. Wird einen Teil der Raytracing Logik enthalten später
- **Window**: Generiert das GFLW Fenster welches gut mit openGL integriert ist. Handelt die refreshes, aber hier gibt es wohl wenig zu tun.

### MODELS
- **Raw-/TexturedModel**: Kleine Klassen welche 3D modelle halten, mit und ohne Texturdaten.

### RENDER
- **font**: Parsing und aufbereiten von Bitmap Fonts (ein Bild mit allen Buchstaben). Diese können wir mit "Hierro" generieren und benutzen. Die Parser hier sind nicht selber geschrieben, Quelle steht in den Files.
- **objConverter**: Wandelt .obj files in Koordinaten für openGL um und generiert BoundingBox für Collisions.
- **Loader**: Nimmt die ganzen Modeldaten, öffnet die Texturdateien und lädt alles in die openGL engine.
- **Renderer**: Managen openGL flags und laden die ganzen Transofmationsmatrizen in den Vertex Shader. Kontrollieren Start und Stop der Shader.

### SHADERS
- **glsl**: openGL Scriptsprache. Vertex (position von jedem vektorpunkt) und Fragment (Farbe von jedem Pixel) Shaders. Wird direkt auf dem GPU ausgeführt und vervielfacht so die Leistung für unser Spiel. Vor allem verwendet für Matrizen Multiplikationen, aber wir können beliebige Daten in die Shader laden.
- **Shaders**: Übergeben Variablen von java zu openGL. Kompilieren und führen die shader aus. Die shader (glsl scripts) berechnen dann die Frames und geben es an das Fenster zurück.

## RESOURCES
Alles was nicht code ist. Texturen, Models, Schriften, HUDs, etc.
