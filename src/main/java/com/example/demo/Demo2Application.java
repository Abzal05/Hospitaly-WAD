package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {
        // Определяем желаемый порт (сначала system property, затем env PORT, затем значение из application.properties по умолчанию)
        String sysPort = System.getProperty("server.port");
        String envPort = System.getenv("PORT");
        int desiredPort = -1;

        if (sysPort != null && !sysPort.isBlank()) {
            try { desiredPort = Integer.parseInt(sysPort); } catch (NumberFormatException ignored) {}
            System.out.println("Using server.port from system property: " + sysPort);
        } else if (envPort != null && !envPort.isBlank()) {
            try { desiredPort = Integer.parseInt(envPort); } catch (NumberFormatException ignored) {}
            System.out.println("Using server.port from environment PORT: " + envPort);
        } else {
            // По умолчанию используем 8081 (в application.properties задан ${PORT:8081})
            desiredPort = 8081;
            System.out.println("No server.port provided — default desired port: " + desiredPort);
        }

        int portToUse = choosePort(desiredPort, 8081, 8099);

        // Устанавливаем port как system property, чтобы Spring Boot использовал его
        System.setProperty("server.port", String.valueOf(portToUse));
        System.out.println("Starting application on port: " + portToUse);

        SpringApplication app = new SpringApplication(Demo2Application.class);
        // Передаём явно свойство на случай, если кто-то читает defaultProperties
        Map<String, Object> props = new HashMap<>();
        props.put("server.port", String.valueOf(portToUse));
        app.setDefaultProperties(props);

        app.run(args);
    }

    private static int choosePort(int desiredPort, int startRange, int endRange) {
        // Если желаемый порт валиден и свободен — используем его
        if (isValidPort(desiredPort) && isPortAvailable(desiredPort)) {
            return desiredPort;
        }

        // Попробуем диапазон от startRange до endRange
        for (int p = startRange; p <= endRange; p++) {
            if (isPortAvailable(p)) return p;
        }

        // В крайнем случае попросим ОС выделить свободный порт (0)
        try (ServerSocket socket = new ServerSocket(0)) {
            int autoPort = socket.getLocalPort();
            System.out.println("No ports free in configured range — using ephemeral port: " + autoPort);
            return autoPort;
        } catch (IOException e) {
            System.err.println("FATAL: unable to acquire any port - " + e.getMessage());
            System.exit(1);
        }
        return -1; // unreachable
    }

    private static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
