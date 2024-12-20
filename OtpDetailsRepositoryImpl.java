package com.ocs.authservice.adapter.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.repository.JPAOtpDetailsRepository;
import com.ocs.common.entity.MobOtpEntity;

@Service
public class OtpDetailsRepositoryImpl implements OtpDetailsRepository {

	@Autowired
	private JPAOtpDetailsRepository jpaMobileOtpRepo;

	@Override
	public MobOtpEntity findByOtpRefAndUserName(String otpRef, String userName) {
		return jpaMobileOtpRepo.findByOtpRefAndUserName(otpRef, userName);
	}

	@Override
	public Optional<MobOtpEntity> findByOtpRef(String otqRef) {
		return jpaMobileOtpRepo.findByOtpRef(otqRef);
	}

	@Override
	public void updateStatus(String otpRef, Date now, String status) {
		jpaMobileOtpRepo.updateStatus(otpRef, now, status);
	}

	@Override
	public void updateFailedAttempt(String otpRef, Date now, String status, int failedAttempt) {
		jpaMobileOtpRepo.updateFailedAttempt(otpRef, now, status, failedAttempt);
	}

	@Override
	public void resetFailedOTPCountAttempt(String userName, String status, Date now) {
		jpaMobileOtpRepo.resetFailedOTPCountAttempt(userName, status, now);
	}

	@Override
	public void updateMismatchAttempt(String otpRef, Date now, int mismatchAttempt) {
		jpaMobileOtpRepo.updateMismatchAttempt(otpRef, now, mismatchAttempt);
	}

	@Override
	public int getTodayExpriredCount(String userName, String otpType, String status, Date startDateToday, Date today) {
		return jpaMobileOtpRepo.countByUserNameAndOtpTypeAndStatusAndCreatedTimeBetween(userName,otpType,status,startDateToday,today);
	}

}
