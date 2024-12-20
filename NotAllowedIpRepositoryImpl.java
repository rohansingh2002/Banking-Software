package com.ocs.authservice.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.core.entity.NotallowedIP;
import com.ocs.authservice.core.repository.JPANotAllowedIpRepository;

@Service
public class NotAllowedIpRepositoryImpl implements NotAllowedIpRepository {

	@Autowired
	private JPANotAllowedIpRepository jpaIpRepo;
	
	@Override
	public NotallowedIP findByIp(String ip) {
		return jpaIpRepo.findByIp(ip);
	}

}
