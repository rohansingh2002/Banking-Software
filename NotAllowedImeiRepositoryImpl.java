package com.ocs.authservice.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.entity.NotallowedImei;
import com.ocs.authservice.core.repository.JPANotAllowedImeiRepository;

@Service
public class NotAllowedImeiRepositoryImpl implements NotAllowedImeiRepository {

	@Autowired
	private JPANotAllowedImeiRepository jpaImeiRepo;
	
	@Override
	public NotallowedImei findByImei(String imei) {
		return jpaImeiRepo.findByImei(imei) ;
	}

}
