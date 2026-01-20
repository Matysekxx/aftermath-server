# ☢️ The Aftermath: Post-Apocalyptic Prague (Server)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Status](https://img.shields.io/badge/Status-In_Development-yellow?style=for-the-badge)

> **The Aftermath** je textová multiplayerová RPG hra zasazená do temné budoucnosti pražského metra.
>
> 🚧 **WIP:** Projekt je ve fázi vývoje.
> 
> ⚠️ **Poznámka:** Tento repozitář obsahuje **pouze serverovou část (Backend)**.

---

## 📖 O projektu

Tento projekt vznikl jako školní práce na téma textové hry. Zadání jsem využil především k tomu, abych se naučil:
* Navrhnout architekturu **klient-server**.
* Pracovat s frameworkem **Spring Boot**.
* Implementovat real-time komunikaci přes **WebSockety**.

### Příběh
Rok **2100**. Povrch Prahy je po jaderné válce neobyvatelný. Poslední lidé přežívají v metru, kde vládne tvrdý systém dluhů. Hráči musí podnikat nebezpečné expedice, splácet dluhy Správě a čistit stanice od mutantů pro budoucí generace.

---

## ⚙️ Funkcionalita Serveru

Backend funguje jako "Game Engine", který:
* **Řídí hru:** Validuje tahy a souboje v reálném čase.
* **Spravuje multiplayer:** Umožňuje interakci hráčů (chat, setkávání).
* **Simuluje svět:** Udržuje stav lootu a nepřátel ve stanicích.
* **Řídí ekonomiku:** Každých 24h strhává poplatky za přežití.

---

## 🛠️ Technologie

* **Java & Spring Boot** (Core, Web)
* **WebSocket** (Real-time komunikace)
* **Event-Driven Architecture**

---

## 🖥️ Klient

Pro testování funkčnosti a hraní je k dispozici testovací klient napsaný v Pythonu:
👉 **[Aftermath Client (Test)](https://github.com/Matysekxx/aftermath-client-test)**

---

## 🎮 Herní mechaniky

### Třídy postav
| Třída | Bonusy |
| :--- | :--- |
| 💂 **Voják** | Vysoké HP a útok / Vyšší spotřeba kyslíku. |
| 🎒 **Sběrač** | Velký inventář / Bonus na blízko. |
| 🔭 **Průzkumník** | Odolnost proti radiaci / Šetří filtry. |
| 🛡️ **Tank** | Max přežití a nosnost / Malý inventář. |

### Příkazy
* `move [směr]` / `travel [stanice]` - Pohyb a cestování.
* `attack [cíl]` - Boj.
* `grab` / `drop` / `use` - Inventář.
* `status` - Stav postavy a dluhu.
* `chat [zpráva]` - Komunikace.
