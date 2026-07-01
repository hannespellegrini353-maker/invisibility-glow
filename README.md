# Invisibility Glow

Eine rein clientseitige Fabric-Mod für **Minecraft 1.21.11**, die unsichtbaren
Spielern eine Glow-Outline (wie beim Vanilla-Effekt "Leuchten") verpasst,
**ohne** den eigentlichen Unsichtbarkeitseffekt zu entfernen.

## Build

Voraussetzung: **JDK 21**

```bash
./gradlew build
```

Die fertige Mod liegt danach unter `build/libs/invisibility-glow-1.0.0.jar`.

Zum Testen im Entwicklungsclient:

```bash
./gradlew runClient
```

## Verwendete Versionen

| Komponente     | Version                |
|----------------|-------------------------|
| Minecraft      | 1.21.11                 |
| Fabric Loader  | 0.18.1                  |
| Fabric API     | 0.141.3+1.21.11         |
| Yarn Mappings  | 1.21.11+build.4         |
| Java           | 21                      |
| Loom           | 1.14.x                  |

> Hinweis: Fabric-Loader, Fabric-API und Yarn-Build-Nummern werden von den
> jeweiligen Maintainern regelmäßig aktualisiert. Prüfe vor dem produktiven
> Einsatz auf https://fabricmc.net/develop/ bzw. https://modrinth.com/mod/fabric-api,
> ob es neuere Patch-Versionen für 1.21.11 gibt, und passe `gradle.properties`
> entsprechend an.

## Ingame-Kommando

```
/invisglow                        - zeigt aktuellen Status
/invisglow enabled <true|false>   - Funktion global an/aus
/invisglow includeSelf <true|false> - eigenen Spieler mit einbeziehen
```

## Konfigurationsdatei

`config/invisglow.json` im Minecraft-Verzeichnis (wird automatisch beim
ersten Start erzeugt).

Siehe Kapitel "Funktionsweise" in der Projekt-Beschreibung des Chats für
Details zur technischen Umsetzung.
