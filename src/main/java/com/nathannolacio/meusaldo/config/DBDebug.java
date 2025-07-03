package com.nathannolacio.meusaldo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DBDebug {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @PostConstruct
    public void logDb() {
        System.out.println("🔍 Conectado ao banco:");
        System.out.println("➡️ URL: " + dbUrl);
        System.out.println("➡️ User: " + dbUser);
    }

}
