package io.home4Me.Security.commons.identity;

import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Embeddable
public class CreationDate {
	
	@Generated(GenerationTime.INSERT)
	private LocalDateTime creationDate;
}
