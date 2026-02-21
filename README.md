<div align="center">

# ☢️ THE AFTERMATH SERVER ☢️
### 🛠️ The Authoritative Game Engine for Post-Apocalyptic Prague

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-Real--time-blue?style=for-the-badge&logo=socket.io&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)

<br/>
The Aftermath Server is the authoritative backend for a real-time multiplayer TUI RPG set in the grim future of the Prague metro. Players must survive, manage debt, and fight for resources in a ruthless underground system.

[Report Bug](https://github.com/Matysekxx/aftermath_server/issues) · [Request Feature](https://github.com/Matysekxx/aftermath_server/issues)

</div>

---
## 🌟 Highlights

*   ⚡ **Real-time Synchronization:** High-frequency WebSocket updates for a fluid multiplayer experience.
*   🏗️ **Authoritative Engine:** All logic (combat, movement, economy) is calculated server-side to prevent cheating.
*   🚇 **Living Metro Ecosystem:** Dynamic NPC AI, radiation mechanics, and a persistent global debt system.
*   🛠️ **Modern Tech Stack:** Built on **Java 21** and **Spring Boot 3** with an event-driven architecture.
*   🌍 **Expandable World:** Easily configurable maps, NPCs, and items via JSON, XML and YAML assets.
*   🎮 **Low-Latency Stream:** Optimized for high-frequency updates and efficient communication with connected clients.

---

## ℹ️ Overview

### 🛠️ Technological Pillars

*   **Modern Java 21:** Leveraging the latest language features for optimal performance and maintainability.
*   **Spring Boot 3:** The application's core, providing Dependency Injection, WebSocket communication, and flexible configuration via **YAML** and **XML** assets.
*   **Event-Driven Architecture:** Decoupled game logic and network layers ensure that the game loop remains fast and responsive.

### 📜 Story Background

The year is **2100**. After a devastating nuclear catastrophe, the remnants of Prague's population have retreated into the metro system. But this is no safe haven—it's a dictatorship of debt. The Metro Administration demands daily fees for oxygen and water. Those who fail to pay are exiled to the deadly Surface.

### ✍️ Author

This project was created by **Matysekxx** as a passion project to explore the capabilities of modern Java and Spring Boot in building complex, real-time game backends.

---

## 🚀 Quick Start Guide

Follow these steps to host your own instance of the Prague Metro and invite your friends.

### 🛠️ 1. Server Setup (The Host)

1.  **Prerequisites:** Ensure you have **Java 21** (JDK) installed.
2.  **Launch the Engine:** Run the server using the JAR file in your terminal:
    ```powershell
    java -jar aftermath-server.jar
    ```
3.  **Get Your IP:** Once started, the server console will display your **Local IP Address** (e.g., `192.168.100.244`). 
    > 💡 **Note:** All players must be on the same Wi-Fi or LAN network as the host.

---

### 🎮 2. Joining the Game (The Players)

1.  **Get the Client:** Download the latest native Windows client.
<div align="center">

## 👉 [DOWNLOAD AFTERMATH CLIENT (C++)](https://github.com/Matysekxx/aftermath_client) 👈
</div>

2.  **Connect:** Open your terminal and run the client with the host's WebSocket URL:
    ```powershell
    aftermath_client.exe ws://<HOST_IP>:8080/game
    ```
    *(Replace `<HOST_IP>` with the address provided by the server host, e.g., `192.168.100.244`)*

---

## 🎮 Game Mechanics

The server simulates a complex RPG system where every choice matters.

### Player Classes
The engine handles different stats and perks for each class:

| Class | Role | HP / Rad Limit | Inv. Slots / Weight | Specialization |
| :--- | :--- | :--- | :--- | :--- |
| 💂 **Soldier** | Combat | 120 / 20 | 20 slots / 50kg | Balanced combat stats and high durability. |
| 🎒 **Scavenger** | Looting | 80 / 15 | 30 slots / 60kg | **Maximum storage** for resource gathering. |
| 🔭 **Stalker** | Exploration | 100 / 50 | 25 slots / 55kg | **Extreme radiation resistance** for scouting. |
| 🛡️ **Tank** | Defense | 150 / 10 | 15 slots / 80kg | Maximum health and **highest carry weight**. |

### ⚙️ Configuration

The server is highly configurable via `application.properties`.

| Property | Default   | Description |
| :--- |:----------| :--- |
| `server.port` | `8080`    | The port the server listens on. |
| `game.tick-rate` | `250`     | The game loop update rate in milliseconds (lower = faster game). |
| `game.global-debt` | `1000000` | The starting global debt for the server community. |

---

## 🔧 Troubleshooting

*   **Connection Refused:** Ensure `server.address` is set to `0.0.0.0` in properties and your Windows Firewall allows port `8080`.
*   **Address already in use:** Another program is using port 8080. Change `server.port` in `application.properties` or kill the conflicting process.
*   **Lag / High Latency:** If playing over the internet, ensure your upload speed is sufficient. The server sends frequent map updates via WebSocket.

---

## ⬇️ Installation & Development

This repository contains the server-side core of The Aftermath. To set up the development environment or build the JAR:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Matysekxx/aftermath-server.git
    cd aftermath-server
    ```
2.  **Build the project (using Gradle):**
    ```cmd
    gradlew clean build
    ```
    The resulting JAR will be located in `build/libs/aftermath-server-0.0.1-SNAPSHOT.jar`.

---

## 💭 Invite users to give feedback and contribute

Your feedback and contributions are highly valued! Whether you've found a bug, have a feature idea, or just want to discuss the project, please don't hesitate to reach out.

*   **Report Bugs:** If you encounter any issues, please open an issue on GitHub: Report Bug
*   **Request Features:** Have an idea for a new feature or improvement? Let us know: Request Feature
*   **Contribute Code:** Feel free to fork the repository and submit pull requests. All contributions are welcome!

---

<div align="center">

Created by **Matysekxx**
<br/>
2025 - 2026

</div>
