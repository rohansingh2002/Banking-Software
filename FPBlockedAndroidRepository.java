package com.ocs.authservice.adapter.repository;

public interface FPBlockedAndroidRepository {

	boolean existsByModelAndStatus(String model,String status);
}
