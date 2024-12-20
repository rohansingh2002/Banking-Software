package com.ocs.authservice.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.entity.FingerPrintDetails;
import com.ocs.authservice.core.repository.JpaFingerPrintDetailRepository;

@Service
public class FingerPrintDetailRepositoryImpl implements FingerPrintDetailRepository {

	@Autowired
	private JpaFingerPrintDetailRepository fingerPrintDetRepo;


	@Override
	public FingerPrintDetails findByOldDeviceIdAndImeiAndUserName(String deviceId, String imei, String userName) {
		 return	fingerPrintDetRepo.findByOldDeviceIdAndImei(deviceId,imei);
	}

}
