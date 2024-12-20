package com.ocs.authservice.adapter.repository;

import com.ocs.authservice.adapter.model.UserAuthDetail;
import com.ocs.authservice.adapter.model.UserDto;
import com.ocs.authservice.core.entity.UserEntity;
import com.ocs.authservice.core.entity.UserStatus;

public interface UserRepository {
	void save();

	void save(UserEntity user);

	UserAuthDetail findFirstByUserIdAndPasswordAndUserStatus(String userId, String password, UserStatus userStatus);

	String getPilotUserId(String userId);
	
	 UserDto findByUserId(String userId);

	void updateUserStatusAndBlockReason(String userId, String blockedUser, String invOtp);
	
}