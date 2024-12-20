package com.ocs.authservice.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.repository.JpaFPBlockedAndroidRepository;

@Service
public class FPBlockedAndroidRepositoryImpl implements FPBlockedAndroidRepository{

	@Autowired
	private JpaFPBlockedAndroidRepository jpaFpBlockedRepo;
	
	
	@Override
	public boolean existsByModelAndStatus(String model, String status) {
		boolean exists = jpaFpBlockedRepo.existsByModelAndStatus(model, status);
		return exists;
	}

}
