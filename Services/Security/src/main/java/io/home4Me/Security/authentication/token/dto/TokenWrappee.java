package io.home4Me.Security.authentication.token.dto;

import java.util.List;

import org.springframework.data.util.Pair;

import io.home4Me.Security.VerificationInfo;
import io.micrometer.core.instrument.util.StringUtils;

public class TokenWrappee {

	private Pair<String, String> accessWithRefresh;
	
	public TokenWrappee(String refreshToken, String accessToken) {
		accessWithRefresh = Pair.of(refreshToken, accessToken);
	}
	
	public String getRefreshToken() {
		return accessWithRefresh.getFirst();
	}
	
	public String getAccessToken() {
		return accessWithRefresh.getSecond();	
	}
	
	public List<VerificationInfo> getValidationInfo() {
		return List.of(
				new VerificationInfo(StringUtils.isNotBlank(getAccessToken())).onFailSetReason("Access token is null or empty"), 
				new VerificationInfo(StringUtils.isNotBlank(getRefreshToken())).onFailSetReason("Refresh token is null or empty")
		);
	}
}
