package com.ocs.authservice.adapter.repository;

import com.ocs.authservice.core.entity.NotallowedDevices;

public interface NotAllowedDeviceRepository {

	NotallowedDevices findByDeviceId(String deviceId);

}
