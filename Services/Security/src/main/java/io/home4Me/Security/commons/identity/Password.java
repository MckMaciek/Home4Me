package io.home4Me.Security.commons.identity;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Password {
	private String password;
}
