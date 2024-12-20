package com.ocs.authservice.adapter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;


import com.ocs.authservice.adapter.model.ForgotPwdRequest;
import com.ocs.authservice.adapter.model.ChangePwdRequest;

import com.ocs.authservice.adapter.model.CustomerProfileRequest;

import com.ocs.authservice.adapter.model.ValidateOtpRequest;
import com.ocs.authservice.adapter.service.AuthService;
import com.ocs.authservice.constant.ApiResponseConstant;
import com.ocs.authservice.constant.AppConstant;
import com.ocs.authservice.util.RSAUtil;
import com.ocs.common.constants.AppConstants;
import com.ocs.common.dto.GenericResponse;
import com.ocs.common.dto.LoginDTO;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/auth"+AppConstants.VERSION)
@Slf4j
public class AuthenticationController implements AuthenticationControllerInterface {

	private final AuthService authService;

	@Autowired
	HttpServletRequest request;

	private ServerWebExchange exchange;

	public AuthenticationController(AuthService authService) {
		this.authService = authService;
	}

	@Override
	@PostMapping(path = "/public/rp")
	public ResponseEntity<Map<String, String>> getPublicKey() {
		Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put(AppConstant.HEADER_UNIT, request.getHeader(AppConstant.HEADER_UNIT));
		inputMap.put(AppConstant.HEADER_CHANNEL, request.getHeader(AppConstant.HEADER_CHANNEL));
		return new ResponseEntity<Map<String, String>>(authService.getPublicKey(inputMap), HttpStatus.OK);

	}

//	@Override
	@PostMapping(path = "/user/login")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = ApiResponseConstant.LOGIN_REQ)),
	responseCode = "200")
	public GenericResponse<Map<String, Object>> userLogin(
			@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
			@RequestHeader(name = AppConstants.CUSTOMER_ID) String customerId,
			@RequestBody LoginDTO loginDto) {
		return authService.login(loginDto, unit, channel, lang, serviceId,screenId,moduleId,subModuleId,customerId, request);
	}
	

	@PostMapping("/public/iok")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = ApiResponseConstant.IOK_REQ)),
	responseCode = "200")
	public GenericResponse<Map<String, Object>> getKeys(@RequestBody Map<String, String> inputmap) {
		log.debug("Inside IOK " + inputmap);
		return authService.getKeys(inputmap);
	}

	@Override
	@GetMapping("/password/{password}")
	public String getPassword(@PathVariable("password") String password) {

		try {
			return RSAUtil.encrypt(password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}
	}

	@PostMapping(path = "/user/logout")
	public Mono<GenericResponse<Map<String, String>>> logout() {
		return authService.logout(exchange);
	}
	
	@PostMapping(path = AppConstant.USER_RESEND_OTP_URL)
	@ApiResponse(content = @Content(examples = @ExampleObject(value = ApiResponseConstant.USER_RESEND_OTP_RES)),responseCode = "200")
	public ResponseEntity<GenericResponse<?>>resendOtp(
			@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId){
		return authService.resendOtp(unit,channel,lang,serviceId,screenId,moduleId,subModuleId);
	}
		
	@PostMapping(path = AppConstant.USER_VALIDATE_OTP_URL)
	@ApiResponse(content = @Content(mediaType = "application/json", 
	examples = @ExampleObject(value = ApiResponseConstant.USER_VALIDATE_RES)),responseCode = "200")
	public ResponseEntity<GenericResponse<?>>otpValidation(
			@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
			@RequestBody ValidateOtpRequest otpReq){
		return authService.otpValidation(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,otpReq);		
	}
	@PostMapping(path = AppConstant.USER_CHANGE_PASSWORD)
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = ApiResponseConstant.CHANGE_PASSWORD_RESPONSE)),
	responseCode = "200")
	public GenericResponse<Map<String, Object>> changePassword(
			@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
			@RequestBody ChangePwdRequest changePwdRequest){
		return authService.changePassword(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,changePwdRequest);		
	}
	@PostMapping(path = AppConstant.FORGOT_PASSWORD_URL)
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = ApiResponseConstant.FORGOT_PASSWORD_RESPONSE)),
	responseCode = "200")
	public ResponseEntity<GenericResponse<?>>forgotPassword(
			@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestBody ForgotPwdRequest forgetPwdRequest){
		return authService.forgotPassword(forgetPwdRequest,unit, channel, lang, serviceId,screenId,moduleId,subModuleId);
	}
	@PostMapping(path = AppConstant.FORGOT_PASSWORD_VALIDATE_OTP_URL)
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = ApiResponseConstant.ValidateOtp_Forgot_Password)),
	responseCode = "200")
	public ResponseEntity<GenericResponse<?>>forgotPasswordValidateOtp(
			@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
			@RequestBody ValidateOtpRequest otpReq){
		return authService.forgotPasswordValidateOtp(unit,channel,lang,serviceId,screenId,moduleId,subModuleId,otpReq);		
	}

	@PostMapping("/customer/profile")
	@ApiResponse(content = @Content(mediaType = AppConstant.MEDIA_TYPE, examples = @ExampleObject(value = "")), responseCode = "200")
	public GenericResponse<Map> getCustomerProfile(@RequestHeader(name = AppConstants.UNIT) String unit,
			@RequestHeader(name = AppConstants.CHANNEL) String channel,
			@RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
			@RequestHeader(name = AppConstants.SERVICEID) String serviceId,
			@RequestHeader(name = AppConstants.SCREENID) String screenId,
			@RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
			@RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
			@RequestBody CustomerProfileRequest reqMap) {
		return authService.getCustomerProfile(reqMap, unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
	}
}