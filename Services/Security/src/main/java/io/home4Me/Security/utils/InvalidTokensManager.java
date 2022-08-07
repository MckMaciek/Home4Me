package io.home4Me.Security.utils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Service
public class InvalidTokensManager {
		
	private Map<String, LocalDateTime> tokensMap = new ConcurrentHashMap<>();

	public void discardAccessToken(String refreshToken, String accessToken) {
		//tokensMap.put(refreshToken, accessToken);
	}
	
}
