package com.ocs.authservice.adapter.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.adapter.model.UserContext;
import com.ocs.authservice.adapter.service.CommonService;
import com.ocs.common.dto.GenericResponse;
import com.ocs.authservice.core.usecase.CommonServiceUseCase;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService{
	
	@Autowired
	private CommonServiceUseCase commonServiceUseCase;

	@Override
	public GenericResponse<Map<String, Object>> getUserInfo(UserContext userContext, String unit, String channel) {
		return commonServiceUseCase.userInfo(userContext, unit, channel);
	}

}
