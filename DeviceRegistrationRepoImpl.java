package com.ocs.authservice.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.repository.JPADeviceRegisterRepository;
import com.ocs.common.entity.DeviceRegistration;

@Service
public class DeviceRegistrationRepoImpl implements DeviceRegistrationRepository{

	@Autowired
	private JPADeviceRegisterRepository jpaDeviceRegisterRepo;
	
	
	@Override
	public String getRegisteredDeviceCountByDeviceId(String userNo, String deviceId, String oldDeviceId) {
		return jpaDeviceRegisterRepo.getRegisteredDeviceCountByDeviceId(userNo, deviceId,oldDeviceId);
	}
	
	
	@Override
	public DeviceRegistration findByUserNoAndDeviceId(String userNo, String deviceId) {
		return jpaDeviceRegisterRepo.findByUserNoAndDeviceId( userNo,  deviceId);
	}
	
	
}
