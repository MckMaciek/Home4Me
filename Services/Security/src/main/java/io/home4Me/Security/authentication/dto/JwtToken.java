package io.home4Me.Security.authentication.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtToken {

    private Long id;

    private LocalDateTime expiryDate;

    private LocalDateTime activationDate;

    private String token;
}
