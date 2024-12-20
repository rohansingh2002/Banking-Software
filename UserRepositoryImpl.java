package com.ocs.authservice.adapter.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.ocs.authservice.adapter.mapper.UserMapper;
import com.ocs.authservice.adapter.model.UserAuthDetail;
import com.ocs.authservice.adapter.model.UserDto;
import com.ocs.authservice.core.entity.UserEntity;
import com.ocs.authservice.core.entity.UserStatus;
import com.ocs.authservice.core.repository.JPAUserRepository;

@Service
public class UserRepositoryImpl implements UserRepository {

	private final JPAUserRepository jpaRepository;
	private final UserMapper userMapper;

	@Autowired
	public UserRepositoryImpl(JPAUserRepository jpaRepository, UserMapper userMapper) {
		super();
		this.jpaRepository = jpaRepository;
		this.userMapper = userMapper;
	}


	@Override
	public void save() {
		UserEntity user = new UserEntity();
		user.setUserId("11111");
		user.setCreatedBy("SYSTEM");
//		user.setCreatedTime(new Date());
//		user.setLastModifiedTime(new Date());
//		user.setLastModifiedBy("SYSTEM");
	}

	public void save(UserEntity user) {
		user.setModifiedTime(LocalDateTime.now());
		user.setModifiedBy("SYSTEM");
		jpaRepository.save(user);
	}

	@Override
	public UserAuthDetail findFirstByUserIdAndPasswordAndUserStatus(String userId, String password,
			UserStatus userStatus) {
		UserAuthDetail userAuthDetail = new UserAuthDetail();
		Optional<UserEntity> userOptional = jpaRepository.findFirstByUserIdAndPasswordAndStatus(userId, password,
				userStatus);
		if (userOptional.isPresent()) {
			UserEntity user = userOptional.get();
			userAuthDetail.setEmailId("");
			userAuthDetail.setUserName(user.getUserId());
			userAuthDetail.setMobileNo("");
			userAuthDetail.setTType(user.getUserType().toString());
			return userAuthDetail;
		} else
			return null;
	}

	@Override
	public String getPilotUserId(String userId) {
		String pilotUserId = jpaRepository.getPilotUserId(userId);
		if (Objects.nonNull(pilotUserId) && !pilotUserId.isEmpty()) {
			return pilotUserId;
		} else {
			return null;
		}
	}

	@Override
	public UserDto findByUserId(String userId) {
		UserDto userDto = new UserDto();
		UserEntity userDet = jpaRepository.findByUserId(userId);
		if (Objects.nonNull(userDet)) {
			userDto = userMapper.entityToDto(userDet);
			return userDto;
		}
		return null;
	}

	@Override
	public void updateUserStatusAndBlockReason(String userId, String status, String blockReason) {
		jpaRepository.updateUserStatusAndBlockReason(userId, status, blockReason);
	}

}
