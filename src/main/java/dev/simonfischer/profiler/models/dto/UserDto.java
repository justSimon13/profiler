package dev.simonfischer.profiler.models.dto;

import dev.simonfischer.profiler.models.enumeratio.AccountRole;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private AttributesDto attributes;
    private AccountRole accountRole;
}
