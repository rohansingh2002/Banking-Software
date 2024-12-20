package com.ocs.authservice.adapter.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocs.authservice.adapter.model.UserContext;
import com.ocs.authservice.adapter.service.CommonService;
import com.ocs.authservice.constant.ApiResponseConstant;
import com.ocs.authservice.constant.AppConstant;
import com.ocs.common.constants.AppConstants;
import com.ocs.common.dto.GenericResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/commonservice"+AppConstants.VERSION)
@Slf4j
public class CommonController {

	@Autowired
	private CommonService commonService;

	@PostMapping("/user-info")
	@ApiResponse(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = ApiResponseConstant.USER_INFO_REQ)), responseCode = "200")
	public GenericResponse<Map<String, Object>> getUserInfo(@RequestBody @Valid UserContext userContext, 
			@RequestHeader(AppConstant.HEADER_UNIT) String unit,
			@RequestHeader(AppConstant.HEADER_CHANNEL) String channel) {
		log.debug("Inside user-info");
		return commonService.getUserInfo(userContext, unit, channel);
	}

}
