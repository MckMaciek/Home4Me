package io.home4Me.Security.authentication.identity.user;

import org.modelmapper.ModelMapper;

import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.identity.user.dto.LoginDetailsDto;

class LoginDetailsMapper {

	public static final ModelMapper mapper = new ModelMapper();
	
	public static LoginDetails map(RegisterRequest registerRequest) {
		return mapper.map(registerRequest, LoginDetails.class);
	}
	
	public static LoginDetails map(LoginRequest loginRequest) {
		return mapper.map(loginRequest, LoginDetails.class);
	}
	
	public static LoginDetailsDto map(LoginDetails loginDetails) {
		return mapper.map(loginDetails, LoginDetailsDto.class);
	}
}
