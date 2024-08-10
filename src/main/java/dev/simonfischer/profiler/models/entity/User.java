package dev.simonfischer.profiler.models.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserAttributes attributes;
}
