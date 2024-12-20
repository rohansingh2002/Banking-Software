package com.ocs.authservice.adapter.service;

import java.util.Map;

import com.ocs.authservice.adapter.model.UserContext;
import com.ocs.common.dto.GenericResponse;

public interface CommonService {

	public GenericResponse<Map<String, Object>> getUserInfo(UserContext userContext, String unit, String channel);
}
