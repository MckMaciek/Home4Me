package io.home4Me.Security.commons.identity;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Embeddable
public class Username {

	@NotBlank
	@Size(min=5, max=16)
	private String username;
}
