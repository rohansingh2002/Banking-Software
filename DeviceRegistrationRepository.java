package com.ocs.authservice.adapter.repository;

import com.ocs.common.entity.DeviceRegistration;

public interface DeviceRegistrationRepository {
	
	String getRegisteredDeviceCountByDeviceId(String userNo, String deviceId, String oldDeviceId);

	DeviceRegistration findByUserNoAndDeviceId(String userNo, String deviceId);
	
	
}
