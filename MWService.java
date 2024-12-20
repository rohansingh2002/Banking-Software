package com.ocs.authservice.adapter.service;

import com.ocs.authservice.adapter.model.AuthenticationResponse;
import com.ocs.authservice.adapter.model.BlockedUserReponse;

import com.ocs.authservice.adapter.model.ForgotPwdRequest;
import com.ocs.authservice.adapter.model.ForgotPwdResponse;
import com.ocs.authservice.adapter.model.ValidateFPOtpResponse;
import com.ocs.authservice.adapter.model.ValidateOtpRequest;

import com.ocs.authservice.adapter.model.ChangePwdRequest;
import com.ocs.authservice.adapter.model.ChangePwdResponse;
import com.ocs.authservice.adapter.model.CustomerProfileDto;
import com.ocs.authservice.adapter.model.CustomerProfileRequest;
import com.ocs.common.dto.AppExceptionHandlerUtilDto;
import com.ocs.common.dto.GenericResponse;
import com.ocs.common.dto.LoginDTO;

public interface MWService {

	public GenericResponse<BlockedUserReponse> blockedUser(String guId, String unit, String channel, String userId,
			String lang);

	public GenericResponse<AuthenticationResponse> mwLogin(LoginDTO dto,String unit, String channel, String lang, 
			String serviceId, String customerId, AppExceptionHandlerUtilDto appExeDto);
	

	public GenericResponse<ForgotPwdResponse> forgotPasswordMW(ForgotPwdRequest forgotPwdRequest, String unit, String channel, String lang, String serviceId, String moduleId,
			String subModuleId, String screenId);
	
	public GenericResponse<ValidateFPOtpResponse> forgotPasswordOtpValidMW(ValidateOtpRequest otpReq, String unit, String channel, String lang, String serviceId, String moduleId,
			String subModuleId, String screenId);

	public GenericResponse<ChangePwdResponse> changePwdMW(String unit, String channel, String lang, String serviceId, String moduleId,
			String subModuleId, String screenId, AppExceptionHandlerUtilDto appExeDto, ChangePwdRequest changePwdRequest);

	GenericResponse<CustomerProfileDto> getCustomerProfile(CustomerProfileRequest reqMap, String unit, String channel, String lang,
			String serviceId, String moduleId, String subModuleId, String screenId);
}

