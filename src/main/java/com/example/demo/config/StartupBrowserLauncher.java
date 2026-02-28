package com.example.demo.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@Component
public class StartupBrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment env;

    public StartupBrowserLauncher(Environment env) {
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean open = Boolean.parseBoolean(env.getProperty("app.open-browser","false"));
        if (!open) return;

        // Prefer the actual runtime port if available via WebServerApplicationContext
        int port = 8081; // fallback default
        if (event.getApplicationContext() instanceof WebServerApplicationContext) {
            try {
                port = ((WebServerApplicationContext) event.getApplicationContext()).getWebServer().getPort();
            } catch (Exception ignored) {}
        } else {
            String localPort = env.getProperty("local.server.port");
            if (localPort != null) {
                try { port = Integer.parseInt(localPort); } catch (Exception ignored) {}
            } else {
                try { port = Integer.parseInt(env.getProperty("server.port","8081")); } catch (Exception ignored) {}
            }
        }

        String url = "http://localhost:" + port + "/";

        // Open default browser if supported
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(URI.create(url));
                } catch (IOException e) {
                    System.err.println("Failed to open browser: " + e.getMessage());
                }
            }
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            try {
                if (os.contains("win")) {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "", url});
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec(new String[]{"open", url});
                } else if (os.contains("nix") || os.contains("nux")) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", url});
                }
            } catch (IOException e) {
                System.err.println("Failed to open browser via runtime exec: " + e.getMessage());
            }
        }
    }
}
