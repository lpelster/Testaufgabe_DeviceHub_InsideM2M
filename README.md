DeviceHub Anwendung
Autor: Louis Pelster
(Entwicklung mithilfe von Ki)

Start: 

1. ./mvnw quarkus:dev Backend (devicehub) starten
2. Anschließend die Main-Methode der Simulation (devicesimulation) ausführen.
3. Weboberfläche aufrufen: http://localhost:8080/view/devices

Testen: Durch Beenden und erneutes Starten der Main (Simulation der Devices) 
können WebSocket-Verbindungen getrennt und wiederhergestellt werden. 
So lassen sich der Online-/Offline-Status und die Übertragung von Messdaten testen.