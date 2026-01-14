# ☢️ The Aftermath: Post-Apocalyptic Prague (Server)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Multiplayer](https://img.shields.io/badge/Multiplayer-WebSocket-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-School_Project-orange?style=for-the-badge)

> **The Aftermath** je textová multiplayerová RPG hra zasazená do temné budoucnosti pražského metra.
>
> ⚠️ **Poznámka:** Tento repozitář obsahuje **pouze serverovou část (Backend)** aplikace. Server zajišťuje herní logiku, perzistenci světa a komunikaci mezi klienty přes WebSocket.

---

## 📖 O projektu

Cílem tohoto školního projektu bylo vytvořit robustní backend pro multiplayerovou hru, vyzkoušet si architekturu **klient-server** a práci s frameworkem **Spring Boot**.

### Příběh
Píše se rok **2100**. Povrch Prahy je po jaderné válce spálenou pustinou, kterou ovládá smrtící radiace a zmutované nestvůry. Poslední zbytky lidstva se stáhly hluboko do podzemí, kde stanice metra slouží jako provizorní domovy.

V tomto nelítostném světě není nic zadarmo. Společnost přežívá jen díky přísnému systému dluhů a poplatků za základní životní potřeby. Hráči představují přeživší, kteří musí pro Správu podnikat nebezpečné expedice. Cílem je nejen přežít a splatit dluh prodejem šrotu, ale také postupně **vyčistit metro od hrozeb**, aby v něm mohly žít i budoucí generace.

---

## ⚙️ Funkcionalita Serveru

Tato aplikace slouží jako centrální autorita ("Game Engine"), která:

* **Řídí herní smyčku:** Validuje pohyb a akce v reálném čase.
* **Spravuje relace:** Umožňuje připojení více hráčů najednou a jejich vzájemnou interakci pomocí chatu.
* **Simuluje svět:** Udržuje stav NPC, mutantů a dostupného lootu v jednotlivých stanicích.
* **Ekonomický systém:** Každých 24 herních hodin automaticky strhává poplatky za živobytí a kontroluje zadluženost hráčů.

---

## 🛠️ Použité technologie

Backend je postaven na moderním Java stacku:

* **Jazyk:** Java
* **Framework:** Spring Boot (Core, Web)
* **Komunikace:** WebSocket (pro real-time přenos příkazů a stavů)
* **Architektura:** Event-Driven Design (zpracování herních událostí)

---

## 🎮 Herní mechaniky (Server Logic)

Server zpracovává následující herní logiku a příkazy, které klienti odesílají:

### Třídy postav
Server počítá statistiky na základě zvolené specializace:

| Třída | Bonusy a Postihy |
| :--- | :--- |
| 💂 **Voják** | Vysoké HP a efektivita se zbraněmi, ale vyšší spotřeba kyslíku. |
| 🎒 **Sběrač** | Největší inventář pro loot a bonus k boji zblízka. |
| 🔭 **Průzkumník** | Vysoká odolnost vůči radiaci a efektivní hospodaření s filtry. |
| 🛡️ **Tank** | Nejvyšší přežití a nosnost, ale velmi malý inventář. |

### Implementované příkazy
API serveru přijímá a vyhodnocuje tyto textové příkazy:

* `move [směr]` - Validace pohybu v mřížce a odhalování mapy.
* `travel [stanice]` - Logika cestování vlakem a regenerace HP.
* `attack [cíl]` - Výpočet souboje v reálném čase.
* `grab` / `drop` - Manipulace s inventářem a kapacitou.
* `use [předmět]` - Aplikace efektů (léčení, snížení radiace).
* `status` - Odeslání aktuálního stavu hráče (HP, dluh, inventář).
* `chat [zpráva]` - Broadcast zprávy ostatním hráčům v lokaci.

---

> **Disclaimer:** Toto je studentský projekt vytvořený pro vzdělávací účely. Neslouží ke komerčnímu využití.
