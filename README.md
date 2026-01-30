<div align="center">

# ☢️ THE AFTERMATH ☢️
### POST-APOCALYPTIC PRAGUE | SERVER CORE

![Java](https://img.shields.io/badge/Java_21-F89820?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![WebSocket](https://img.shields.io/badge/Event_Driven-Architecture-8A2BE2?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)

<br>

> *"Povrch je mrtvý. Budoucnost je v hlubinách."*

**The Aftermath** je real-time multiplayerová RPG hra s TUI rozhraním, zasazená do temné budoucnosti pražského metra.
Hráči musí přežít, splácet dluhy a bojovat o zdroje v nelítostném systému podzemních stanic.

[Report Bug](https://github.com/Matysekxx/aftermath_server/issues) · [Request Feature](https://github.com/Matysekxx/aftermath_server/issues)

</div>

---

### ⚠️ Status Projektu

> **Tento repozitář obsahuje pouze SERVEROVOU ČÁST (Backend).**
>
> 🚧 **WIP:** Projekt je aktuálně ve fázi aktivního vývoje.

---

## 📖 Vize projektu

**The Aftermath** začal jako experiment s cílem posunout hranice toho, co dokáže "obyčejná" textová hra. Co původně vzniklo jako studium architektury klient-server, se rozrostlo v plnohodnotný herní ekosystém.

Cílem projektu je vytvořit **robustní backend v Javě**, který zvládne simulovat žijící svět v reálném čase. Nejde jen o databázi, ale o komplexní engine, který počítá fyziku pohybu, řeší konflikty mezi hráči a spravuje ekonomiku metra – to vše komunikující přes WebSockety s vysokou frekvencí pro plynulý zážitek na straně klienta.

### 🛠️ Technologické pilíře
* **Moderní Java 21:** Využití nejnovějších features jazyka pro maximální efektivitu.
* **Spring Boot 3:** Jádro aplikace zajišťující Dependency Injection a REST/WS vrstvu.
* **Event-Driven Design:** Asynchronní zpracování herních událostí pro okamžitou odezvu.

### 📜 Příběhové pozadí
Píše se rok **2100**. Po jaderné katastrofě se zbytky pražské populace stáhly do metra. Není to ale bezpečný úkryt – je to diktatura dluhů. Správa metra vyžaduje denní poplatky za kyslík a vodu. Kdo nezaplatí, je vyhoštěn na Povrch.

---

## ⚙️ Game Engine & Logika

Server v této architektuře vystupuje jako autoritativní **Game Engine**. Klient (TUI) slouží pouze k vykreslování a odesílání vstupů. Veškerá logika a pravda o herním světě leží zde:

* **⚡ Real-time Input Processing**
    Server nečeká na textové příkazy. Okamžitě zpracovává stream stisknutých kláves z klienta, validuje kolize a vypočítává pohyb v reálném čase.

* **🌍 Synchronizace Světa**
    Zajišťuje konzistenci stavu pro všechny hráče. Pokud jeden hráč sebere předmět nebo zabije mutanta, změna se ihned projeví všem ostatním v dané stanici.

* **💀 Ekonomika přežití**
    Server udržuje perzistentní stav světa. Počítá spotřebu kyslíku, opotřebení filtrů a každých 24 hodin provádí "Zúčtování" – strhávání poplatků za přežití.

* **🛡️ Anti-Cheat Validace**
    Kontroluje, zda jsou tahy fyzikálně a logicky platné (dosah útoku, průchodnost terénem, cooldowny schopností).

---

## 🖥️ Klient (C++ / TUI)

Server nemá vlastní grafické rozhraní. Hra využívá pokročilé **Text User Interface (TUI)**, které běží v konzoli, ale ovládá se jako moderní akční hra.

<div align="center">

## 👉 [STÁHNOUT AFTERMATH CLIENT (C++)](https://github.com/Matysekxx/aftermath_client) 👈
*Nativní Windows klient zajišťující vykreslování TUI, hudbu a input handling.*

</div>

---

## 🎮 Herní mechaniky

### Třídy postav
Styl hry se odvíjí od zvolené třídy a jejích statistik:

| Ikona | Třída | HP | Atk | Specifikace |
| :---: | :--- | :---: | :---: | :--- |
| 💂 | **Voják** | ⭐⭐⭐ | ⭐⭐⭐ | Vysoké poškození, ale rychle spotřebovává kyslík. |
| 🎒 | **Sběrač** | ⭐ | ⭐ | **2x větší inventář**. Bonus k útoku na blízko. |
| 🔭 | **Průzkumník** | ⭐⭐ | ⭐⭐ | Imunita vůči lehké radiaci. Šetří filtry plynové masky. |
| 🛡️ | **Tank** | ⭐⭐⭐⭐ | ⭐ | Maximální přežití a nosnost. Velmi malý inventář. |

### ⌨️ Ovládání
Hra využívá přímé ovládání klávesnicí pro plynulý zážitek:

| Klávesa | Akce | Funkce |
| :---: | :--- | :--- |
| <kbd>W</kbd><kbd>A</kbd><kbd>S</kbd><kbd>D</kbd> | **Pohyb** | Navigace v prostoru stanice a mapy. |
| <kbd>E</kbd> | **Interakce** | Sebrání lootu, otevření dveří, obchodování. |
| <kbd>SPACE</kbd> | **Útok** | Souboj s nepřítelem v dosahu. |
| <kbd>I</kbd> | **Inventář** | Správa batohu a vybavení. |
| <kbd>T</kbd> | **Chat** | Komunikace s ostatními přeživšími. |
| <kbd>ESC</kbd> | **Menu** | Nastavení nebo odhlášení ze hry. |

---

<div align="center">

Created by **Matysekxx**
<br>
2025 - 2026

</div>
