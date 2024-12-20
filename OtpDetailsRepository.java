package com.ocs.authservice.adapter.repository;

import java.util.Date;
import java.util.Optional;

import com.ocs.common.entity.MobOtpEntity;

public interface OtpDetailsRepository {

	MobOtpEntity findByOtpRefAndUserName(String otpRef, String userName);

	Optional<MobOtpEntity> findByOtpRef(String otqRef);

	void updateStatus(String otpRef, Date now, String status);

	void updateFailedAttempt(String otpRef, Date now, String status, int failedAttempt);

	void resetFailedOTPCountAttempt(String userName, String status, Date now);

	void updateMismatchAttempt(String otpRef, Date now, int mismatchAttempt);
	
	int getTodayExpriredCount(String userName, String otpType, String status, Date startDateToday, Date today);
}
