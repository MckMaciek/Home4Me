package io.home4Me.Security.commons.identity;

import javax.persistence.Embeddable;
import javax.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class EMail {
	
	@Email
	private String email;
}
