package com.ocs.authservice.adapter.service.impl;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import com.ocs.authservice.adapter.model.ChangePwdRequest;

import com.ocs.authservice.adapter.model.ForgotPwdRequest;

import com.ocs.authservice.adapter.model.ChangePwdResponse;
import com.ocs.authservice.adapter.model.CustomerProfileRequest;

import com.ocs.authservice.adapter.model.ValidateOtpRequest;
import com.ocs.authservice.adapter.service.AuthService;
import com.ocs.authservice.core.usecase.AuthUseCase;
import com.ocs.common.dto.GenericResponse;
import com.ocs.common.dto.LoginDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthUseCase authUseCase;
    
    public AuthServiceImpl(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }
    
	@Override
	public Map<String, String> getPublicKey(Map<String, String> inputMap) {
		Map<String,String> responseMap = authUseCase.getPublicKey(inputMap);
		return responseMap;
	}

	@Override
	public GenericResponse<Map<String, Object>> login(@Valid LoginDTO loginDto, String unit,
			String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId,
			String customerId,HttpServletRequest request) {
		return authUseCase.login(loginDto,unit,channel,lang,serviceId,screenId,moduleId,subModuleId,customerId,request);
	}
	@Override
	public GenericResponse<Map<String, Object>> getKeys(Map<String, String> inputMap) {	
		return authUseCase.getKeys(inputMap);
	}

	@Override
	public Mono<GenericResponse<Map<String, String>>> logout(ServerWebExchange exchange) {
		return authUseCase.logout(exchange);
	}

	@Override
	public ResponseEntity<GenericResponse<?>> otpValidation(String unit, String channel, String lang, String serviceId,
			String screenId,String moduleId, String subModuleId, ValidateOtpRequest otpReq) {
		return authUseCase.otpValidation(unit, channel, lang, serviceId, screenId,moduleId,subModuleId,otpReq);
	}

	@Override
	public ResponseEntity<GenericResponse<?>> resendOtp(String unit, String channel, String lang, String serviceId,
			String screenId,String moduleId, String subModuleId) {
		return authUseCase.resendOtp(unit, channel, lang, serviceId, screenId,moduleId,subModuleId);
	}

	@Override

	public ResponseEntity<GenericResponse<?>> forgotPassword(ForgotPwdRequest forgetPwdRequest,String unit,String channel, String lang, String serviceId,String screenId,String moduleId,String subModuleId){
		return authUseCase.forgotPassword(forgetPwdRequest,unit, channel, lang, serviceId,screenId,moduleId,subModuleId);
	}
	
	@Override
	public ResponseEntity<GenericResponse<?>> forgotPasswordValidateOtp(String unit, String channel, String lang,
			String serviceId, String screenId, String moduleId, String subModuleId, ValidateOtpRequest otpReq){
		return authUseCase.forgotPasswordValidateOtp(unit,channel, lang,
				serviceId,  screenId,  moduleId, subModuleId, otpReq);
	}

	public GenericResponse<Map<String, Object>> changePassword(String unit, String channel, String lang, String serviceId,
			String screenId, String moduleId, String subModuleId, ChangePwdRequest changePwdRequest) {
		return authUseCase.changePassword(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, changePwdRequest);
	}

	@Override
	public GenericResponse<Map> getCustomerProfile(CustomerProfileRequest reqMap, String unit, String channel, String lang, String serviceId,
			String screenId, String moduleId, String subModuleId) {
		return authUseCase.getCustomerProfile(reqMap, unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
	}

}
