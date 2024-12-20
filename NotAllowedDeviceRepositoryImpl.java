package com.ocs.authservice.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.entity.NotallowedDevices;
import com.ocs.authservice.core.repository.JPANotAllowedDeviceRepository;

@Service
public class NotAllowedDeviceRepositoryImpl implements NotAllowedDeviceRepository {

	@Autowired
	private JPANotAllowedDeviceRepository jpaDeviceRepo;
	
	@Override
	public NotallowedDevices findByDeviceId(String deviceId) {
		return jpaDeviceRepo.findByDeviceId(deviceId);
	}

}
