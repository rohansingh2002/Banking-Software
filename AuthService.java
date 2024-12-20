package com.ocs.authservice.adapter.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import com.ocs.authservice.adapter.model.ChangePwdRequest;

import com.ocs.authservice.adapter.model.ForgotPwdRequest;

import com.ocs.authservice.adapter.model.ChangePwdResponse;
import com.ocs.authservice.adapter.model.CustomerProfileRequest;

import com.ocs.authservice.adapter.model.ValidateOtpRequest;
import com.ocs.common.dto.GenericResponse;
import com.ocs.common.dto.LoginDTO;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

public interface AuthService {
	public Map<String, String> getPublicKey(Map<String, String> inputMap);

	public GenericResponse<Map<String, Object>> login(LoginDTO loginDTO, String unit, String channel, String lang,
			String serviceId, String screenId, String moduleId, String subModuleId, String customerId, HttpServletRequest request);

	public GenericResponse<Map<String, Object>> getKeys(Map<String, String> inputMap);

	public Mono<GenericResponse<Map<String, String>>> logout(ServerWebExchange exchange);
	
	public ResponseEntity<GenericResponse<?>> otpValidation(String unit, String channel, String lang, String serviceId,
			String screenId, String moduleId, String subModuleId, ValidateOtpRequest otpReq);

	public ResponseEntity<GenericResponse<?>> resendOtp(String unit, String channel, String lang, String serviceId,
			String screenId, String moduleId, String subModuleId);

	public ResponseEntity<GenericResponse<?>> forgotPassword(ForgotPwdRequest forgetPwdRequest,String unit,String channel, String lang, String serviceId,String screenId,String moduleId,String subModuleId);

	public ResponseEntity<GenericResponse<?>> forgotPasswordValidateOtp(String unit, String channel, String lang,
			String serviceId, String screenId, String moduleId, String subModuleId, ValidateOtpRequest otpReq);

	public GenericResponse<Map<String, Object>> changePassword(String unit, String channel, String lang, String serviceId,
			String screenId, String moduleId, String subModuleId, ChangePwdRequest changePwdRequest);

	GenericResponse<Map> getCustomerProfile(CustomerProfileRequest reqMap, String unit, String channel, String lang,
			String serviceId, String screenId, String moduleId, String subModuleId);

}