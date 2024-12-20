package com.ocs.authservice.adapter.repository;

import com.ocs.authservice.core.entity.FingerPrintDetails;

public interface FingerPrintDetailRepository {

	FingerPrintDetails findByOldDeviceIdAndImeiAndUserName(String deviceId, String imei, String userName);
}
