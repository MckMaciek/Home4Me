package io.home4Me.Security;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class VerificationInfo {
	
	private boolean isVerified = false;
	private String reason = "";
	
	public VerificationInfo(boolean isVerified) {
		this.isVerified = isVerified;
	}
	
	public boolean isVerified() {
		return isVerified;
	}
	
	public String getReason() {
		return reason;
	}
	
	public VerificationInfo onFailSetReason(String reason) {
		if(!isVerified) {
			this.reason = reason;
		}
		return this;
	}
	
	public static Optional<VerificationInfo> findAnyFailure(List<VerificationInfo> infos){
		return infos.stream()
				.filter(vInfo -> !vInfo.isVerified())
				.findAny();
	}
	
	public static boolean checkAndInCaseOfFailureRun(List<VerificationInfo> infos, Consumer<? super VerificationInfo> onFailAction) {
		Optional<VerificationInfo> validationFailed = VerificationInfo.findAnyFailure(infos);
		validationFailed.ifPresent(onFailAction);
		return validationFailed.isEmpty();
	}
	
}
