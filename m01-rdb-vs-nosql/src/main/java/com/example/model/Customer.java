package com.example.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Customer {
    private Long id;
    private String name;
    private String email;
    private Instant createdAt;

    public Customer() {}

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
