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
        // Всегда используем порт 8081 (игнорируем -Dserver.port от IntelliJ)
        int desiredPort = 8081;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try { desiredPort = Integer.parseInt(envPort); } catch (NumberFormatException ignored) {}
        }

        // Сбрасываем server.port который мог передать IntelliJ (например 8080)
        System.clearProperty("server.port");

        // Принудительно освобождаем желаемый порт (убиваем процесс на нём)
        freePortByKilling(desiredPort);

        int portToUse = choosePort(desiredPort, 8081, 8099);
        System.setProperty("server.port", String.valueOf(portToUse));
        System.out.println(">>> Starting Hospital app on port: " + portToUse);

        SpringApplication app = new SpringApplication(Demo2Application.class);
        Map<String, Object> props = new HashMap<>();
        props.put("server.port", String.valueOf(portToUse));
        app.setDefaultProperties(props);
        app.run(args);
    }

    /**
     * Принудительно убивает процесс, занимающий указанный порт (Windows).
     */
    private static void freePortByKilling(int port) {
        if (isPortAvailable(port)) return; // порт уже свободен
        System.out.println("Port " + port + " is busy — attempting to kill occupying process...");
        try {
            // Находим PID через netstat
            Process findPid = Runtime.getRuntime().exec(
                new String[]{"cmd", "/c", "netstat -ano | findstr :" + port + " | findstr LISTENING"}
            );
            String output = new String(findPid.getInputStream().readAllBytes()).trim();
            findPid.waitFor();
            if (!output.isBlank()) {
                // Парсим последнее число — это PID
                String[] parts = output.trim().split("\\s+");
                String pid = parts[parts.length - 1];
                System.out.println("Killing PID " + pid + " on port " + port);
                Process kill = Runtime.getRuntime().exec(new String[]{"taskkill", "/F", "/PID", pid});
                kill.waitFor();
                // Даём немного времени чтобы порт освободился
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.err.println("Could not kill process on port " + port + ": " + e.getMessage());
        }
    }

    private static int choosePort(int desiredPort, int startRange, int endRange) {
        if (isValidPort(desiredPort) && isPortAvailable(desiredPort)) {
            return desiredPort;
        }
        for (int p = startRange; p <= endRange; p++) {
            if (isPortAvailable(p)) return p;
        }
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            System.err.println("FATAL: unable to acquire any port - " + e.getMessage());
            System.exit(1);
        }
        return -1;
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
