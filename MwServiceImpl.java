package com.ocs.authservice.adapter.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dohabank.xmlns.namespace.dbframework.headerschema.Header;
import com.dohabank.xmlns.namespace.dbframework.mobileapps.ChangePasswordRequest;
import com.dohabank.xmlns.namespace.dbframework.mobileapps.ChangePasswordResponse;
import com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.AuthenticateCustomerRequest;
import com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.AuthenticateCustomerResponse;
import com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.GetCustomerProfileDCRequest;
import com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.GetCustomerProfileDCResponse;
import com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.WSSecurity;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ATMCardDetails;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ATMHeader;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ATMRequest;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ForgotPasswordRequest;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ForgotPasswordResponse;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.UserDetails;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ValidateFPDTrxnPinRequest;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ValidateFPDTrxnPinResponse;
import com.dohabank.xmlns.namespace.dbframework.selfregistration.ValidatePin;
import com.dohabank.xmlns.namespace.dbframework.sessionvalidations.SessionValidations;
import com.example.xmlns._1293275650883.MobileBanking;
import com.example.xmlns._1426143020548.MobileApps;
import com.example.xmlns._1426143020548.MobileApps_Service;
import com.ocs.authservice.adapter.feign.CommonServiceFeign;
import com.ocs.authservice.adapter.model.AuthenticationResponse;
import com.ocs.authservice.adapter.model.BlockedUserReponse;
import com.ocs.authservice.adapter.model.ChangePwdRequest;
import com.ocs.authservice.adapter.model.ChangePwdResponse;
import com.ocs.authservice.adapter.model.CustomerProfileDto;
import com.ocs.authservice.adapter.model.CustomerProfileRequest;
import com.ocs.authservice.adapter.model.ForgotPwdRequest;
import com.ocs.authservice.adapter.model.ForgotPwdResponse;
import com.ocs.authservice.adapter.model.UnitsRes;
import com.ocs.authservice.adapter.model.UserDto;
import com.ocs.authservice.adapter.model.ValidateFPOtpResponse;
import com.ocs.authservice.adapter.model.ValidateOtpRequest;
import com.ocs.authservice.adapter.repository.ConfigRepository;
import com.ocs.authservice.adapter.repository.UserRepository;
import com.ocs.authservice.adapter.service.MWService;
import com.ocs.authservice.constant.AppConstant;
import com.ocs.authservice.util.AuthUtil;
import com.ocs.common.adapter.repository.ConfigurationRepository;
import com.ocs.common.constants.AppConstants;
import com.ocs.common.dto.AppExceptionHandlerUtilDto;
import com.ocs.common.dto.ConfigDto;
import com.ocs.common.dto.GenericResponse;
import com.ocs.common.dto.LoginDTO;
import com.ocs.common.dto.ResultSet;
import com.ocs.common.dto.ResultUtilVO;
import com.ocs.common.repository.JPARRmessageRepository;
import com.ocs.common.repository.URLProviderRepo;
import com.ocs.common.service.AsyncLogServcie;
import com.ocs.common.service.HeadersUtil;
import com.ocs.common.service.IServiceExecutor;
import com.ocs.common.service.RRmessageAuditingService;
import com.ocs.common.service.impl.MqJmsMessage;
import com.ocs.common.service.impl.ServiceExecutorImpl;
import com.ocs.common.util.CommonUtil;
import com.ocs.common.util.DateUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.ws.BindingProvider;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MwServiceImpl implements MWService {

	@Autowired
	private CommonServiceFeign commonServiceFeign;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	private RRmessageAuditingService rrMessageAuditingService;

	@Autowired
	private JPARRmessageRepository rrMessageRepository;

	@Autowired
	MqJmsMessage mqJmsMessage;

	@Autowired
	private HttpServletRequest httpRequest;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AsyncLogServcie asyncService;

	ResultUtilVO resultUtilVo = new ResultUtilVO();

	@Autowired
	private HeadersUtil headersUtil;

	@Autowired
	private com.ocs.common.service.WsSecurityUtil wsSecurityUtil;

	@Autowired
	private ConfigurationRepository configurationRepo;

	@Autowired
	URLProviderRepo repository;

	@Override
	public GenericResponse<BlockedUserReponse> blockedUser(String guId, String unit, String channel, String userId,
			String lang) {
		AppExceptionHandlerUtilDto appExeDto = null;
		ResponseEntity<BlockedUserReponse> response = null;
		GenericResponse<BlockedUserReponse> responseData = new GenericResponse<>();
		var entity = new HttpEntity<Map<String, String>>(null, null);
		var header = new HashMap<String, String>();
		var headers = new HttpHeaders();
		try {
			resultUtilVo = new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC);
			appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, AppConstant.SERVICEID_BLCKUSER);
			appExeDto.setStartTime(DateUtil.currentDate());
			Map<String, String> req = new HashMap<>();
			req.put(AppConstant.HEADER_GUID, guId);
			req.put(AppConstant.HEADER_UNIT, unit);
			req.put(AppConstant.HEADER_CHANNEL, channel);
			req.put(AppConstant.HEADER_ACCEPT_LANGUAGE, lang);
			req.put(AppConstant.USER_ID, userId);
			entity = new HttpEntity<>(req, AuthUtil.generHeaders(unit, channel));
			headers = AuthUtil.generHeaders(unit, channel);

			BlockedUserReponse blockedUserRes = new BlockedUserReponse(new ResultUtilVO(AppConstants.RESULT_CODE,
					AppConstants.RESULT_DESC, AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
			response = ResponseEntity.ok(blockedUserRes);
			appExeDto.setEndTime(DateUtil.currentDate());

			Optional.ofNullable(response).map(ResponseEntity::getBody)
					.filter(body -> AppConstant.RESULT_CODE.equals(body.getResult().getCode())).ifPresent(body -> {
						userRepository.updateUserStatusAndBlockReason(userId, AppConstant.BLOCKED_USER,
								AppConstant.INV_OTP);
						responseData.setData(body);
					});

			header.put(AppConstant.HEADER_UNIT, unit);
			header.put(AppConstant.HEADER_CHANNEL, channel);
			header.put(AppConstant.HEADER_GUID, guId);
			header.put(AppConstant.SERVICEID, AppConstant.SERVICEID_BLCKUSER);
			header.put(AppConstant.HEADER_ACCEPT_LANGUAGE, lang);
			header.put(AppConstant.BROWSER, httpRequest.getHeader(AppConstant.BROWSER));
			header.put(AppConstant.USER_NO, httpRequest.getHeader(AppConstant.USER_NO));
			header.put(AppConstant.IP_ADDRESS, httpRequest.getHeader(AppConstant.IP_ADDRESS));
			header.put(AppConstant.CUSTOMER_ID, httpRequest.getHeader(AppConstant.CUSTOMER_ID));
			header.put(AppConstant.URL, httpRequest.getRequestURL().toString());
		} catch (Exception e) {
			appExeDto.setEndTime(DateUtil.getCurrentDate());
			resultUtilVo = new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC);
			log.error("Exception while user block MW service API : " , e);
			httpRequest.setAttribute(AppConstant.EXCEPTION_MSG, e.getMessage());
		} finally {
			asyncService.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response, resultUtilVo);
		}
		responseData.setStatus(resultUtilVo);
		return responseData;
	}

	@Override
	public GenericResponse<AuthenticationResponse> mwLogin(LoginDTO loginDto, String unit, String channel, String lang,
			String serviceID, String customerId, AppExceptionHandlerUtilDto appExeDto) {
		GenericResponse<AuthenticationResponse> res = new GenericResponse<>();
		String mwURL = null;
		AuthenticationResponse response = null;
		Map<String, String> header = new HashMap<>();
		// var headers = new HttpHeaders();
		AuthenticateCustomerRequest authenticateCustomerRequest = new AuthenticateCustomerRequest();
		try {
			/*
			 * Map<String, String> req = new HashMap<>(); req.put(AppConstant.USER_NAME,
			 * loginDto.getUserInfo().getUserName()); req.put(AppConstant.PASSWORD,
			 * loginDto.getUserInfo().getPassword()); headers = AuthUtil.generHeaders(unit,
			 * channel); appExeDto.setStartTime(DateUtil.currentDate());
			 */
			log.info("inside login service mwLogin()  :{}",  serviceID);
			appExeDto.setCustomerId(customerId);

			appExeDto.setMicroSerId(AppConstant.MICROSERVICE_ID);
			appExeDto.setUserName(loginDto.getUserInfo().getUserName());
			appExeDto.setCustomerNo(loginDto.getUserInfo().getCustomerNo());

			if (loginDto.getUserInfo().getLoginType().equals(AppConstant.LOGIN_TYPE_PASS)) {
				authenticateCustomerRequest.setClientSessionKey(UUID.randomUUID().toString());
				authenticateCustomerRequest.setPassword(loginDto.getUserInfo().getPassword());
				authenticateCustomerRequest.setCustomerNumber(loginDto.getUserInfo().getCustomerNo());

				// Map<String, Object> resMap = CommonUtil.generHeaders(repository, appExeDto);
				Map<String, Object> resMap = CommonUtil.generHeaders(repository, appExeDto);
				mwURL = String.valueOf(resMap.get("mwURL"));

				authenticateCustomerRequest.setDeviceFlag("");
				authenticateCustomerRequest.setDeviceId(loginDto.getDeviceInfo().getDeviceId());
				authenticateCustomerRequest.setDeviceName(loginDto.getDeviceInfo().getDeviceName());
				authenticateCustomerRequest.setOSVersion(loginDto.getDeviceInfo().getOsVersion());
				authenticateCustomerRequest.setOSType(loginDto.getDeviceInfo().getOsType());
				authenticateCustomerRequest.setSessionIP(loginDto.getDeviceInfo().getIpAddress());
				authenticateCustomerRequest.setHeader((Header) resMap.get("headerValue"));
				authenticateCustomerRequest.setWSSecurity((WSSecurity) resMap.get("vs"));
				
				log.info("inside login service mwLogin()  :{}",  serviceID);
				response = setMWUserAuthResponse(authenticateCustomerRequest, appExeDto, loginDto, mwURL);
				
				log.info("inside loginservice mwLogin reponse :{}",  response);
			} else {
				res.setStatus(new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, "Bio Metric not yet implemented!"));
				res.setData(null);
				return res;
			}

			appExeDto.setEndTime(DateUtil.currentDate());
			final AuthenticationResponse finalResponse = response;
			Optional.ofNullable(response).filter(mwres -> AppConstants.MW_RESULT.equals(mwres.getResult().getCode()))
					.ifPresentOrElse(mwres -> {
						ConfigDto configOtpLength = configurationRepo
								.findByUnit_IdAndChannel_ChannelAndKeyAndStatus(unit, channel, AppConstant.OTP_LENGTH,
										AppConstants.STATUS)
								.orElseThrow(() -> new IllegalArgumentException("Config not found!"));
						finalResponse.setOtpLength(configOtpLength.getValue());
						res.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, mwres.getResultSet().getMessage()));
						res.setData(finalResponse);
					}, () -> {
						// MW response is not success
						res.setStatus(new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, AppConstants.RESULT_ERROR_DESC,
								finalResponse.getResultSet().getStatus(), finalResponse.getResultSet().getMessage()));
						res.setData(null);
					});

			appExeDto.setMobileNumber("NA");
			appExeDto.setEmailId("NA");
			appExeDto.setClientInfo("NA");

			header.put(AppConstants.UNIT, appExeDto.getUnit());
			header.put(AppConstants.CHANNEL, appExeDto.getChannel());
			header.put(AppConstants.SERVICEID, appExeDto.getServiceId());
			header.put(AppConstants.GUID, AuthUtil.generateUniqueID(appExeDto.getChannel()));
			header.put(AppConstants.ACCEPT_LANGUAGE, appExeDto.getLang());
			header.put(AppConstants.SCREENID, httpRequest.getHeader(AppConstant.HEADER_SCREEN_ID));
			header.put(AppConstants.MODULE_ID, httpRequest.getHeader(AppConstants.MODULE_ID));
			header.put(AppConstants.SUB_MODULE_ID, httpRequest.getHeader(AppConstants.SUB_MODULE_ID));
			header.put(AppConstants.CUSTOMER_ID, httpRequest.getHeader(AppConstants.CUSTOMER_ID));
			header.put(AppConstants.REQUEST_METHOD, httpRequest.getMethod());
			header.put(AppConstants.URL, httpRequest.getRequestURI());
			header.put(AppConstants.USERNO, "NA");
		} catch (Exception e) {
			e.printStackTrace();
			appExeDto.setEndTime(DateUtil.currentDate());
			log.error("Exception in mw login service :{}", e);
			res.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.GEN_ERROR_DESC));
		} finally {
			// asyncService.logAsync(appExeDto, rrMessageRepository,
			// authenticateCustomerRequest, response, header, headers,
			// res.getStatus(), header.toString());
			
			asyncService.logAsync(appExeDto, rrMessageRepository, authenticateCustomerRequest, response, res.getStatus());
		}
		return res;
	}

	private AuthenticationResponse setMWUserAuthResponse(AuthenticateCustomerRequest passRequest,
			AppExceptionHandlerUtilDto appExeDto, LoginDTO loginDto, String mwURL) {

		AuthenticationResponse response = new AuthenticationResponse();
		try {

			String message = "";
			String errorFlag = "";
			String processId = "";
			String branchCode = "";
			String customerNumber = "";
			String customerInfoFlag = "";
			String sessionKeyTibco = "";
			String sessionKey = "";
			String clientSessionKey = "";
			String accessDate = "";
			String days = "";
			String qidExp = "";
			String firstLogin = "";
			String phase = "";
			String deviceCount = "";
			ResultSet result = new ResultSet();

			AuthenticateCustomerResponse passResponse = null;
			com.example.xmlns._1293275650883.Service sevService = new com.example.xmlns._1293275650883.Service();
			MobileBanking mb = sevService.getMobileBankingEndpoint1();
			// Client client = ClientProxy.getClient(mb);

			// Configure the client with SSL/TLS settings

			 disableHostnameVerification(mb);

			BindingProvider bindingProvider = (BindingProvider) mb;
			bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, mwURL);
			// "https://10.8.30.210:55912/ProcessDefinitions/Testing/TestServer/MobileBanking/Service.serviceagent/MobileBankingEndpoint1");
//			passResponse = mb.authenticateCustomerOp(passRequest);
			IServiceExecutor<AuthenticateCustomerRequest,  HttpServletRequest, AuthenticateCustomerResponse> mwExecutor = new ServiceExecutorImpl<>();
			passResponse = mwExecutor.execute(passRequest, httpRequest, mb::authenticateCustomerOp, appExeDto);

			message = passResponse.getResultSet().getMessage();
			errorFlag = passResponse.getResultSet().getErrorFlag();
			processId = passResponse.getResultSet().getProcessID();
			branchCode = passResponse.getBranchCode();
			customerNumber = passResponse.getCustomerNumber();
			customerInfoFlag = passResponse.getCustinfoFlag();
			sessionKey = passResponse.getSessionKey();
			clientSessionKey = passResponse.getClientSessionKey();
			accessDate = passResponse.getAccessDate();
			phase = passResponse.getPhase();
			days = passResponse.getDays();
			qidExp = passResponse.getQIDExpDate();
			firstLogin = passResponse.getFirstLogin();
			deviceCount = passResponse.getDeviceCount();

			result.setMessage(passResponse.getResultSet().getMessage());
			result.setStatus(passResponse.getResultSet().getStatus());
			result.setErrorFlag(passResponse.getResultSet().getErrorFlag());
			result.setProcessID(passResponse.getResultSet().getProcessID());

			response.setResultSet(result);
			response.setCustomerNumber(customerNumber);
			response.setBranchCode(branchCode);
			response.setAccessDate(accessDate);
			response.setClientSessionKey(clientSessionKey);
			response.setSessionKey(sessionKey);
			response.setCustinfoFlag(customerInfoFlag);
			response.setPhase(phase);
			response.setDays(days);
			response.setQidExpDate(qidExp);
			response.setDeviceCount(deviceCount);
			response.setFirstLogin(firstLogin);
			UserDto userDet = userRepository.findByUserId(loginDto.getUserInfo().getUserName());

			response.setMobileNo("+97412345678");
			response.setEmail("test@gmail.com");
			if (Objects.nonNull(userDet)) {
				response.setUserNo(userDet.getUserNo());
			} else {
				response.setUserNo("09");
			}
			List<UnitsRes> unitResLst = new ArrayList<>();
			List<String> mbUnits = new ArrayList<>();
			UnitsRes unitRes = new UnitsRes();
			List<String> preferredUnits = new ArrayList<>();
			unitRes.setCustomerSegmentation("test");
			unitRes.setIsRegisteredUnit(true);
			unitRes.setLatestbaseNo("675554");
			unitRes.setNationalId("3435435");
			unitRes.setNationalIdExpiry("43545");
			unitRes.setParentCountryCode("QA");
			unitRes.setResidentCountryCode("QA");
			unitRes.setSalaryReceiving("Y");
			unitRes.setUnitId("PRD");
			unitResLst.add(unitRes);
			response.setUnits(unitResLst);
			for (UnitsRes unit : response.getUnits()) {
				if (AppConstants.MB_CHANNEL_ID.equals(appExeDto.getChannel())) {
					if (mbUnits.contains(unit.getUnitId())) {
						preferredUnits.add(unit.getUnitId());
					}
				} else {
					preferredUnits.add(unit.getUnitId());
				}
			}
			response.setPreferredUnits(preferredUnits);
			response.setResult(new ResultUtilVO(result.getStatus(), result.getMessage()));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
		return response;
	}

	private static void disableHostnameVerification(Object serviceInterfaceClient) {
		
		  Client client = ClientProxy.getClient(serviceInterfaceClient); HTTPConduit
		  conduit = (HTTPConduit) client.getConduit();
		  
		  TLSClientParameters tlsParams = new TLSClientParameters();
		  tlsParams.setDisableCNCheck(true); // Disable Common Name (CN) check
		  
		  tlsParams.setHostnameVerifier(new HostnameVerifier() {
		  
		  @Override public boolean verify(String hostname, SSLSession session) { return
		  true; // Always return true to disable hostname verification 
		  } });
		  
		  conduit.setTlsClientParameters(tlsParams);
	
	}

	public GenericResponse<ForgotPwdResponse> forgotPasswordMW(ForgotPwdRequest forgotPwdRequest, String unit,
			String channel, String lang, String serviceId, String moduleId, String subModuleId, String screenId) {
		GenericResponse<ForgotPwdResponse> responseData = new GenericResponse<ForgotPwdResponse>();
		GenericResponse<ForgotPwdResponse> response = new GenericResponse<ForgotPwdResponse>();
		Map<String, String> header = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto();
		HttpEntity<Map<String, String>> entity = null;
		try {
			Map<String, String> req = new HashMap<>();
			req.put(AppConstant.USER_NAME, forgotPwdRequest.getUserInfo().getUserName());
			req.put(AppConstant.CUSTOMER_NO, forgotPwdRequest.getUserInfo().getCustomerNumber());
			appExeDto.setStartTime(DateUtil.getCurrentDate());
			appExeDto.setChannel(channel);
			appExeDto.setUnit(unit);
			appExeDto.setServiceId(serviceId);
			entity = new HttpEntity<>(req, AuthUtil.generHeaders(unit, channel));
			headers = AuthUtil.generHeaders(unit, channel);
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
			Header headerValue = headersUtil.createHeader(unit, forgotPwdRequest.getUserInfo().getUserName(), channel);
			forgotPasswordRequest.setHeader(headerValue);

			ATMRequest atmRequest = new ATMRequest();
			atmRequest.setHostName("33");
			atmRequest.setIPaddress("123123");
			atmRequest.setRemoteAddr("10.4.30.42");
			atmRequest.setSessionUserID(forgotPwdRequest.getUserInfo().getUserName());
			atmRequest.setURL("?");

			ATMHeader atmHeader = new ATMHeader();
			atmHeader.setChannel(headerValue.getChannelID());
			atmHeader.setCountryCode("QAR");
			atmHeader.setFlag("");
			atmHeader.setFunctionType("FPD");
			atmHeader.setRefNum("0");

			UserDetails userDetails = new UserDetails();
			userDetails.setBranchCode("");
			userDetails.setCustomerNumber(forgotPwdRequest.getUserInfo().getCustomerNumber());
			userDetails.setCustomerType("3");
			userDetails.setUserID(forgotPwdRequest.getUserInfo().getUserName());

			ATMCardDetails atmCardDetails = new ATMCardDetails();
			atmCardDetails.setCardMonth("-");
			atmCardDetails.setCardNumber("639950XXXXXX0156");
			atmCardDetails.setCardPin("-");
			atmCardDetails.setCardYear("-");
			atmCardDetails.setQID("");

			forgotPasswordRequest.setATMRequest(atmRequest);
			forgotPasswordRequest.setATMHeader(atmHeader);
			forgotPasswordRequest.setUserDetails(userDetails);
			forgotPasswordRequest.setATMCardDetails(atmCardDetails);

			response = setMWForgetPasswordResponse(forgotPasswordRequest, forgotPwdRequest, appExeDto);

			Optional<GenericResponse<ForgotPwdResponse>> optionalResponse = Optional.ofNullable(response);

			optionalResponse.map(GenericResponse::getStatus) // Get status if response is present
					.ifPresentOrElse(status -> {
						if (AppConstants.RESULT_CODE.equals(status.getCode())) {
							responseData
									.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
							optionalResponse.map(GenericResponse::getData).ifPresent(responseData::setData);
						} else {
							responseData.setStatus(
									new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, status.getDescription()));
							optionalResponse.map(GenericResponse::getData).ifPresent(responseData::setData);
						}
					}, () -> {
						responseData.setStatus(
								new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.RESULT_ERROR_DESC));

					});
			appExeDto.setEndTime(DateUtil.getCurrentDate());
			header.put(AppConstants.UNIT, unit);
			header.put(AppConstants.CHANNEL, channel);
			header.put(AppConstants.SERVICEID, serviceId);
			header.put(AppConstants.GUID, AuthUtil.generateUniqueID(channel));
		} catch (Exception e) {
			log.error("Exception in ForgetPassword: ", e);
			responseData.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));
		} finally {
			  asyncService.logAsync(appExeDto, rrMessageRepository, new HashMap<>(),  response, responseData.getStatus());
			  
			 
		}

		return responseData;
	}

	private GenericResponse<ForgotPwdResponse> setMWForgetPasswordResponse(ForgotPasswordRequest request,
			ForgotPwdRequest forgotPwdRequest, AppExceptionHandlerUtilDto appExeDto) {
		GenericResponse<ForgotPwdResponse> response = new GenericResponse<ForgotPwdResponse>();
		ForgotPwdResponse forgotPwdResponse = new ForgotPwdResponse();
		MobileApps_Service service = new MobileApps_Service();
		MobileApps mb = service.getMobileAppsEndpoint1();
		BindingProvider bindingProvider = (BindingProvider) mb;
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				"https://10.8.30.210:55913/Service/MobileApps.serviceagent/MobileAppsEndpoint1");
		try {
			//ForgotPasswordResponse mwResponse = mb.forgetPasswordOp(request);
			IServiceExecutor<ForgotPasswordRequest,  HttpServletRequest, ForgotPasswordResponse> mwExecutor = new ServiceExecutorImpl<>();
			ForgotPasswordResponse mwResponse = mwExecutor.execute(request, httpRequest, mb::forgetPasswordOp, appExeDto);

			if (Optional.ofNullable(mwResponse).map(ForgotPasswordResponse::getResultSet).isPresent()) {
				forgotPwdResponse.setMessage(mwResponse.getResultSet().getMessage());
				forgotPwdResponse.setStatus(mwResponse.getResultSet().getStatus());
				forgotPwdResponse.setOtpRefNo(mwResponse.getReferenceMSG());
				forgotPwdResponse.setUserName(forgotPwdRequest.getUserInfo().getUserName());
				forgotPwdResponse.setCustomerNumber(forgotPwdRequest.getUserInfo().getCustomerNumber());
				if ("0".equals(mwResponse.getResultSet().getStatus())) {
					response.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
				} else {
					response.setStatus(
							new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, AppConstants.RESULT_ERROR_DESC));
				}
			}
			response.setData(forgotPwdResponse);
		} catch (Exception e) {
			log.error("Exception in Middleware Service of ForgetPassword: {}", e);
			response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));

		}
		return response;
	}

	public GenericResponse<ValidateFPOtpResponse> forgotPasswordOtpValidMW(ValidateOtpRequest validateOTPRequest,
			String unit, String channel, String lang, String serviceId, String moduleId, String subModuleId,
			String screenId) {
		var responseData = new GenericResponse<ValidateFPOtpResponse>();
		GenericResponse<ValidateFPOtpResponse> response = new GenericResponse<ValidateFPOtpResponse>();
		Map<String, String> header = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		AppExceptionHandlerUtilDto appExeDto = new AppExceptionHandlerUtilDto();
		HttpEntity<Map<String, String>> entity = null;
		try {
			Map<String, String> req = new HashMap<>();
			req.put(AppConstant.USER_NAME, httpRequest.getHeader(AppConstants.USERNAME));
			req.put(AppConstant.CUSTOMER_NO, httpRequest.getHeader(AppConstants.CUSTOMER_NO));
			entity = new HttpEntity<>(req, AuthUtil.generHeaders(unit, channel));
			headers = AuthUtil.generHeaders(unit, channel);
			appExeDto.setStartTime(DateUtil.getCurrentDate());
			appExeDto.setChannel(channel);
			appExeDto.setUnit(unit);
			appExeDto.setServiceId(serviceId);
			ValidateFPDTrxnPinRequest validateFPDTrxnPinRequest = new ValidateFPDTrxnPinRequest();
			Header headerValue = headersUtil.createHeader(unit, httpRequest.getHeader(AppConstants.USERNAME), channel);
			validateFPDTrxnPinRequest.setHeader(headerValue);

			ATMRequest atmRequest = new ATMRequest();
			atmRequest.setHostName("33");
			atmRequest.setIPaddress("123123");
			atmRequest.setRemoteAddr("10.4.30.42");
			atmRequest.setSessionUserID(httpRequest.getHeader(AppConstants.USERNAME));
			atmRequest.setURL("Checkavail");

			ATMHeader atmHeader = new ATMHeader();
			atmHeader.setChannel(headerValue.getChannelID());
			atmHeader.setCountryCode("QAR");
			atmHeader.setFlag("");
			atmHeader.setFunctionType("FPD");
			atmHeader.setRefNum(httpRequest.getHeader(AppConstants.OTP_REF_NO));

			UserDetails userDetails = new UserDetails();
			userDetails.setBranchCode("");
			userDetails.setCustomerNumber(httpRequest.getHeader(AppConstants.CUSTOMER_NO));
			userDetails.setCustomerType("3");
			userDetails.setUserID(httpRequest.getHeader(AppConstants.USERNAME));

			ValidatePin validatePin = new ValidatePin();
			validatePin.setPin(validateOTPRequest.getOtpDet().getOtpValue());
			validatePin.setRefNum(httpRequest.getHeader(AppConstant.OTP_REF_NO));

			validateFPDTrxnPinRequest.setATMRequest(atmRequest);
			validateFPDTrxnPinRequest.setATMHeader(atmHeader);
			validateFPDTrxnPinRequest.setUserDetails(userDetails);
			validateFPDTrxnPinRequest.setValidatePin(validatePin);

			response = setMWForgetPasswordOtpValidResponse(validateFPDTrxnPinRequest, appExeDto);

			Optional<GenericResponse<ValidateFPOtpResponse>> optionalResponse = Optional.ofNullable(response);

			optionalResponse.map(GenericResponse::getStatus).ifPresentOrElse(status -> {
				if (AppConstants.RESULT_CODE.equals(status.getCode())) {
					responseData.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
					optionalResponse.map(GenericResponse::getData).ifPresent(responseData::setData);
				} else {
					responseData.setStatus(new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, status.getDescription()));
					optionalResponse.map(GenericResponse::getData).ifPresent(responseData::setData);
				}
			}, () -> {
				responseData.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, AppConstants.RESULT_ERROR_DESC));
			});
			appExeDto.setEndTime(DateUtil.getCurrentDate());
			header.put(AppConstants.UNIT, unit);
			header.put(AppConstants.CHANNEL, channel);
			header.put(AppConstants.SERVICEID, serviceId);
			header.put(AppConstants.GUID, AuthUtil.generateUniqueID(channel));
		} catch (Exception e) {
			log.error("Exception in ValidateForgotPassword OTP: ", e);
			responseData.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));
		} finally {
			asyncService.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response, responseData.getStatus());
		}
		return responseData;
	}

	private GenericResponse<ValidateFPOtpResponse> setMWForgetPasswordOtpValidResponse(
			ValidateFPDTrxnPinRequest request, AppExceptionHandlerUtilDto appExeDto) {
		GenericResponse<ValidateFPOtpResponse> response = new GenericResponse<ValidateFPOtpResponse>();
		ValidateFPOtpResponse validateFPOtpResponse = new ValidateFPOtpResponse();
		MobileApps_Service service = new MobileApps_Service();
		MobileApps mb = service.getMobileAppsEndpoint1();
		BindingProvider bindingProvider = (BindingProvider) mb;
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				"https://10.8.30.210:55913/Service/MobileApps.serviceagent/MobileAppsEndpoint1");
		try {
//			ValidateFPDTrxnPinResponse mwResponse = mb.validateFPTrxnPinOp(request);
			IServiceExecutor<ValidateFPDTrxnPinRequest,  HttpServletRequest, ValidateFPDTrxnPinResponse> mwExecutor = new ServiceExecutorImpl<>();
			ValidateFPDTrxnPinResponse mwResponse = mwExecutor.execute(request, httpRequest, mb::validateFPTrxnPinOp, appExeDto);


			if (Optional.ofNullable(mwResponse).map(ValidateFPDTrxnPinResponse::getResultSet).isPresent()) {
				validateFPOtpResponse.setMessage(mwResponse.getResultSet().getMessage());
				validateFPOtpResponse.setStatus(mwResponse.getResultSet().getStatus());
				if ("0".equals(mwResponse.getResultSet().getStatus())) {
					response.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
				} else {
					response.setStatus(
							new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, AppConstants.RESULT_ERROR_DESC));
				}
			}
			response.setData(validateFPOtpResponse);
		} catch (Exception e) {
			log.error("Exception in Middleware Service of ForgetPasswordValidateOtp: {}", e);
			response.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));
		}
		return response;
	}

	@Override
	public GenericResponse<ChangePwdResponse> changePwdMW(String unit, String channel, String lang, String serviceId,
			String moduleId, String subModuleId, String screenId, AppExceptionHandlerUtilDto appExeDto,
			ChangePwdRequest changePwdRequest) {
		var responseData = new GenericResponse<ChangePwdResponse>();
		var resultUtilVO = new ResultUtilVO();
		Map<String, String> header = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		ChangePwdResponse middlewareResponse = new ChangePwdResponse();
		HttpEntity<Map<String, String>> entity = null;
		ChangePwdResponse response = new ChangePwdResponse();
		String mwURL = null;

		try {
			appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
			appExeDto.setMicroSerId(AppConstant.CHANGE_PWD_MICRO_SERVICEID);
			appExeDto.setUserName(httpRequest.getHeader(AppConstants.USERNAME));
			appExeDto.setSessionKey(httpRequest.getHeader(AppConstants.SESSION_KEY));
			appExeDto.setClientSessionKey(httpRequest.getHeader(AppConstants.CLIENT_SESSION_KEY));
			appExeDto.setCustomerNo(httpRequest.getHeader(AppConstants.CUSTOMER_NO));
	
			var configMap = new HashMap<String, String>();
			Map<String, String> req = new HashMap<>();
			req.put(AppConstant.USER_NAME, httpRequest.getHeader(AppConstants.USERNAME));
			req.put(AppConstant.CUSTOMER_NO, httpRequest.getHeader(AppConstants.CUSTOMER_NO));
			entity = new HttpEntity<>(req, AuthUtil.generHeaders(unit, channel));
      		headers = AuthUtil.generHeaders(unit, channel);
			Map<String, Object> resMap = CommonUtil.generHeaders(repository, appExeDto);
			mwURL = String.valueOf(resMap.get(AppConstant.MW_URL));

			ChangePasswordRequest changePwdReq = new ChangePasswordRequest();
//	        Header headerValue = headersUtil.createHeader(unit, request.getHeader(AppConstants.USERNAME), channel);
//	        changePwdReq.setHeader(headerValue);
//	        var sessionValidations = new SessionValidations();
//	        sessionValidations.setClientSessionKey(request.getHeader(AppConstants.CLIENT_SESSION_KEY));
//	        sessionValidations.setSessionKey(request.getHeader(AppConstants.SESSION_KEY));
//	        sessionValidations.setCustNumber(request.getHeader(AppConstants.CUSTOMER_NO));
//	        sessionValidations.setOTPFLAG("N");       
//	        sessionValidations.setFUNCTIONTYPE("");
//	        sessionValidations.setACTIVETIME("6");
//	        sessionValidations.setACTIVEFLAG("Y");
			changePwdReq.setHeader((Header) resMap.get("headerValue"));
			Map<String, String> sessionVld = (Map)resMap.get("sessionVld");
			SessionValidations sessionValidations = new SessionValidations();
			sessionValidations.setClientSessionKey(sessionVld.get("ClientSessionKey"));
			sessionValidations.setSessionKey(sessionVld.get("SessionKey"));
			sessionValidations.setCustNumber(sessionVld.get("CustNumber"));
			sessionValidations.setOTPFLAG(sessionVld.get("OTPFLAG"));
			sessionValidations.setFUNCTIONTYPE(sessionVld.get("FUNCTIONTYPE"));
			sessionValidations.setACTIVETIME(sessionVld.get("ACTIVETIME"));
			sessionValidations.setACTIVEFLAG(sessionVld.get("ACTIVEFLAG"));
			changePwdReq.setSessionValidations(sessionValidations);
			changePwdReq.setNewPassword(changePwdRequest.getUserInfo().getNewPassword());
			changePwdReq.setOldPassword(changePwdRequest.getUserInfo().getOldPassword());
			changePwdReq.setCustomerNumber(httpRequest.getHeader(AppConstants.CUSTOMER_NO));
			changePwdReq.setFuncType("");
			appExeDto.setStartTime(DateUtil.currentDate());
			middlewareResponse = setMWChangePwdResponse(changePwdReq, mwURL, appExeDto);

			Optional.ofNullable(middlewareResponse).ifPresentOrElse(mwres -> {
				if (AppConstants.RESULT_CODE.equals(mwres.getResult().getCode())) {
					resultUtilVO.setCode(AppConstants.RESULT_CODE);
					responseData.setData(mwres); // Set data for success
				} else {
					resultUtilVO.setCode(AppConstants.GEN_ERROR_CODE);
					responseData.setData(mwres); // Set data for error case too
				}
				responseData.setStatus(resultUtilVO);
			}, () -> {
				resultUtilVO.setCode(AppConstants.GEN_ERROR_CODE);
				responseData.setData(null); // Set null if there's no middleware response
				responseData.setStatus(resultUtilVO);
			});
			header.put(AppConstants.UNIT, unit);
			header.put(AppConstants.CHANNEL, channel);
			header.put(AppConstants.SERVICEID, serviceId);
			header.put(AppConstants.GUID, AuthUtil.generateUniqueID(channel));

		} catch (Exception e) {
			log.error("Exception in changePwdMW service: {}", e);
			ChangePwdResponse responseRes = new ChangePwdResponse(); // To avoid NullPointerException
			responseRes.setResult(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));
			responseData.setData(responseRes);
			responseData.setStatus(resultUtilVO);
		} finally {
			asyncService.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response, responseData.getStatus());
		}
		return responseData;
	}

	private ChangePwdResponse setMWChangePwdResponse(ChangePasswordRequest request, String mwURL, AppExceptionHandlerUtilDto appExeDto) {
		var response = new ChangePwdResponse();
		try {
			var MobileAppservice = new MobileApps_Service();
			var mobileApps = MobileAppservice.getMobileAppsEndpoint1();
			disableHostnameVerification(mobileApps);
			var bindingProvider = (BindingProvider) mobileApps;
			// bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
			bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,mwURL);
			// "https://10.8.30.210:55913/Service/MobileApps.serviceagent/MobileAppsEndpoint1");
//			var mwResponse = mobileApps.changePassword(request);
			IServiceExecutor<ChangePasswordRequest,  HttpServletRequest, ChangePasswordResponse> mwExecutor = new ServiceExecutorImpl<>();
			var mwResponse = mwExecutor.execute(request, httpRequest, mobileApps::changePassword, appExeDto);

			if (Optional.ofNullable(mwResponse).map(ChangePasswordResponse::getResultSet).isPresent()) {
				var resultSet = mwResponse.getResultSet();
				response.setMessage(resultSet.getMessage());
				response.setStatus(resultSet.getStatus());
				response.setErrorFlag(resultSet.getErrorFlag());

				if (AppConstants.MW_RESULT.equals(resultSet.getStatus())) {
					response.setResult(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
				} else {
					response.setResult(
							new ResultUtilVO(AppConstants.RESULT_ERROR_CODE, AppConstants.RESULT_ERROR_DESC));
				}
			}
		} catch (Exception e) {
			response.setResult(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));
		}

		return response;
	}

		@Override
		public GenericResponse<CustomerProfileDto> getCustomerProfile(CustomerProfileRequest reqMap, String unit,
				String channel, String lang, String serviceId, String moduleId, String subModuleId, String screenId) {
			var responseData = new GenericResponse<CustomerProfileDto>();
			Map<String, String> header = new HashMap<>();
			HttpHeaders headers = new HttpHeaders();
			var appExeDto = new AppExceptionHandlerUtilDto(unit, channel, lang, serviceId);
			HttpEntity<Map<String, String>> entity = null;
			ChangePwdResponse response = new ChangePwdResponse();

			try {
				Map<String, String> req = new HashMap<>();
				req.put(AppConstant.USER_NAME, httpRequest.getHeader(AppConstants.USERNAME));
				req.put(AppConstant.CUSTOMER_NO, httpRequest.getHeader(AppConstants.CUSTOMER_NO));
				entity = new HttpEntity<>(req, AuthUtil.generHeaders(unit, channel));
				headers = AuthUtil.generHeaders(unit, channel);

				header.put(AppConstants.CHANNEL, channel);
				header.put(AppConstants.UNIT, unit);
				header.put(AppConstants.SERVICEID, serviceId);
				header.put(AppConstants.LANG, lang);
				header.put(AppConstants.BROWSER, httpRequest.getHeader(AppConstants.BROWSER));
				header.put(AppConstants.USERNO, httpRequest.getHeader(AppConstants.USERNO));
				header.put(AppConstants.IP_ADDRESS, httpRequest.getHeader(AppConstants.IP_ADDRESS));
				header.put(AppConstants.CUSTOMER_ID, httpRequest.getHeader(AppConstants.CUSTOMER_ID));
				header.put(AppConstants.URL, httpRequest.getRequestURL().toString());
				header.put(AppConstants.REQUEST_METHOD, httpRequest.getMethod());
				header.put(AppConstants.SESSION_ID, httpRequest.getRequestedSessionId());
				header.put(AppConstants.GUID, AuthUtil.generateUniqueID(channel));

				appExeDto.setStartTime(DateUtil.currentDate());

				/*
				GetCustomerProfileDCRequest soapRequest = new GetCustomerProfileDCRequest();
				Header soapHeader = headersUtil.createHeader(unit, request.getHeader(AppConstant.USER_NAME), channel);
				soapRequest.setHeader(soapHeader);
				WSSecurity ws = wsSecurityUtil.createWsSecurity(unit, channel, "GetCustomerProfileDCOp",
						AppConstants.INP_SERVICE_MOB);
				soapRequest.setWSSecurity(ws);

				var sessionValidations = new com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.SessionValidations();
				sessionValidations.setClientSessionKey(request.getHeader(AppConstants.CLIENT_SESSION_KEY));
				sessionValidations.setSessionKey(request.getHeader(AppConstants.SESSION_KEY));
				sessionValidations.setCustNumber(request.getHeader(AppConstants.CUSTOMER_NO));
				sessionValidations.setOTPFLAG("N");
				sessionValidations.setFUNCTIONTYPE("");
				sessionValidations.setACTIVETIME("6");
				sessionValidations.setACTIVEFLAG("Y");
				soapRequest.setSessionValidations(sessionValidations);
				*/

				appExeDto.setMicroSerId(AppConstant.MICROSERVICE_ID);
				appExeDto.setUserName(httpRequest.getHeader(AppConstants.USERNAME));
				appExeDto.setCustomerNo(httpRequest.getHeader(AppConstants.CUSTOMER_NO));
				
				Map<String, Object> resMap =  CommonUtil.generHeaders(repository, appExeDto);
				String mwURL = String.valueOf(resMap.get("mwURL"));
				Map<String, String> sessionVld = (Map)resMap.get("sessionVld");
				var sessionValidations = new com.dohabank.xmlns.namespace.dbframework.mobilebankingschema.SessionValidations();
				sessionValidations.setClientSessionKey(sessionVld.get("ClientSessionKey"));
				sessionValidations.setSessionKey(sessionVld.get("SessionKey"));
				sessionValidations.setCustNumber(sessionVld.get("CustNumber"));
				sessionValidations.setOTPFLAG(sessionVld.get("OTPFLAG"));
				sessionValidations.setFUNCTIONTYPE(sessionVld.get("FUNCTIONTYPE"));
				sessionValidations.setACTIVETIME(sessionVld.get("ACTIVETIME"));
				sessionValidations.setACTIVEFLAG(sessionVld.get("ACTIVEFLAG"));

				GetCustomerProfileDCRequest soapRequest = new GetCustomerProfileDCRequest();
				soapRequest.setHeader((Header)resMap.get("headerValue"));
				soapRequest.setWSSecurity((WSSecurity)resMap.get("vs"));
				soapRequest.setSessionValidations(sessionValidations);

				responseData = getCustomerProfileResponse(soapRequest, resMap, appExeDto);
				responseData.getStatus().setCode(AppConstants.RESULT_CODE);
				responseData.getStatus().setDescription(AppConstants.RESULT_DESC);

				appExeDto.setEndTime(DateUtil.getCurrentDate());
			} catch (Exception e) {
				log.error("Exception occurred while simulating customer profile : {}", e);
				responseData.setStatus(new ResultUtilVO(AppConstants.GEN_ERROR_CODE, e.getMessage()));
			} finally {
				asyncService.logAsync(appExeDto, rrMessageRepository, new HashMap<>(), response, responseData.getStatus());
			}

			return responseData;
		}

		
		private GenericResponse<CustomerProfileDto> getCustomerProfileResponse(GetCustomerProfileDCRequest soapRequest, Map<String, Object> resMap,
																			   AppExceptionHandlerUtilDto appExeDto){
			var customerProfileResponse = new GenericResponse<CustomerProfileDto>();
			var customerProfileDto = new CustomerProfileDto();

			com.example.xmlns._1293275650883.Service service = new com.example.xmlns._1293275650883.Service();
			MobileBanking mb = service.getMobileBankingEndpoint1();
			BindingProvider bindingProvider = (BindingProvider) mb;
			bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, String.valueOf(resMap.get("mwURL")));
//					"https://10.8.30.210:55912/ProcessDefinitions/Testing/TestServer/MobileBanking/Service.serviceagent/MobileBankingEndpoint1");
			disableHostnameVerification(mb);
//			GetCustomerProfileDCResponse mwResponse = mb.getCustomerProfileDCOp(soapRequest);
			IServiceExecutor<GetCustomerProfileDCRequest,  HttpServletRequest, GetCustomerProfileDCResponse> mwExecutor = new ServiceExecutorImpl<>();
			GetCustomerProfileDCResponse mwResponse = mwExecutor.execute(soapRequest, httpRequest, mb::getCustomerProfileDCOp, appExeDto);


			if (Optional.ofNullable(mwResponse)
					.map(response -> AppConstants.MW_RESULT.equals(response.getResultSet().getStatus()))
					.orElse(false)) {
				customerProfileDto.setRegistrationDate(mwResponse.getRegistrationDate());
				customerProfileDto.setActiveFlag(mwResponse.getActiveFlag());
				customerProfileDto.setAuthorityAmount(mwResponse.getAuthorityAmount());
				customerProfileDto.setBranchCode(mwResponse.getBranchCode());
				customerProfileDto.setDcCustomerTypeID(mwResponse.getDCCustomerTypeID());
				customerProfileDto.setLoginAttempts(mwResponse.getLoginAttempts());
				customerProfileDto.setUserName(mwResponse.getUserName());
				customerProfileDto.setRemarks(mwResponse.getRemarks());
				customerProfileDto.setCustomerSegment(mwResponse.getCustomerSegment());
				customerProfileDto.setRiyadaFlag(mwResponse.getRiyadaFlag());
				customerProfileDto.setQatariFlag(mwResponse.getQatariFlag());

				var customerDto = customerProfileDto.new Customer();
				var addressDto = customerProfileDto.new Address();
				Optional.ofNullable(mwResponse.getCustomer()).ifPresent(customer -> {
					customerDto.setCustomerNumber(customer.getCustomerNumber());
					customerDto.setFullName(customer.getFullName());
					customerDto.setShortName(customer.getShortName());
					customerDto.setBirthDate(customer.getBirthDate());
					customerDto.setDateOpen(customer.getDateOpen());
					customerDto.setEmailAddress(customer.getEmailAddress());

					Optional.ofNullable(customer.getAddress()).ifPresent(address -> {
						addressDto.setAddress1(address.getAddress1());
						addressDto.setAddress2(address.getAddress2());
						addressDto.setFullAddress(address.getFullAddress());
						addressDto.setPoBox(address.getPOBox());
						addressDto.setTelephoneNumber(address.getTelephoneNumber());
						addressDto.setMobileNo(address.getMobileNo());
						addressDto.setHomeTel(address.getHomeTel());
						addressDto.setWorkTel(address.getWorkTel());
						addressDto.setFaxNo(address.getFaxNo());
						addressDto.setNationalID(address.getNationalID());
						customerDto.setAddress(addressDto);
					});
					customerProfileDto.setCustomer(customerDto);
				});
				customerProfileResponse.setData(customerProfileDto);
			}
			resultUtilVo.setMwCode(mwResponse.getResultSet().getStatus());
			resultUtilVo.setMwdesc(mwResponse.getResultSet().getMessage());
			customerProfileResponse.setStatus(resultUtilVo);
			return customerProfileResponse;
		}

}
