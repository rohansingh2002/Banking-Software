package com.ocs.authservice.adapter.repository;
import com.ocs.authservice.core.entity.NotallowedImei;

public interface NotAllowedImeiRepository {

	NotallowedImei findByImei(String imei);

}
