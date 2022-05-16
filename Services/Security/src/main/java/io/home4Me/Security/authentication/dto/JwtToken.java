package io.home4Me.Security.authentication.dto;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtToken {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime expiryDate;

    private LocalDateTime activationDate;

    private String token;
}
