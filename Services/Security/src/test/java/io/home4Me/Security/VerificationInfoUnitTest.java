package io.home4Me.Security;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VerificationInfoUnitTest {
	
	private static final String REASON_1 = "anyReason1";
	private static final String REASON_2 = "anyReason2";
	private static final String REASON_3 = "anyReason3";
	
	@Test
	public void shouldBeValidationsFailed() {
		
		VerificationInfo v1 = new VerificationInfo(true).onFailSetReason(REASON_1);
		VerificationInfo v2 = new VerificationInfo(false).onFailSetReason(REASON_2);
		VerificationInfo v3 = new VerificationInfo(true).onFailSetReason(REASON_3);
		
		assertThat(v1.getReason()).isEqualTo(StringUtils.EMPTY);
		assertThat(v3.getReason()).isEqualTo(StringUtils.EMPTY);
		
		Optional<VerificationInfo> failed = VerificationInfo.findFailures(List.of(v1,v2,v3));
		
		assertThat(failed).isPresent();
		VerificationInfo verificationInfoThatFailed = failed.get();
		
		assertThat(verificationInfoThatFailed.isVerified()).isFalse();
		assertThat(verificationInfoThatFailed.getReason()).isEqualTo(REASON_2);
	}

}
