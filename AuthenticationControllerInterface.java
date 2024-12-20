package com.ocs.authservice.adapter.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface AuthenticationControllerInterface {
	
	public ResponseEntity<Map<String,String>> getPublicKey();
	
//	public GenericResponse<com.ocs.authservice.adapter.model.UserAuthDetail> userLogin(@RequestBody @Valid LoginDTO loginDTO, 
//			@RequestHeader(name = "unit", required = true) String unit,
//			@RequestHeader(name = "channel", required = true) String channel,
//			@RequestHeader(name = "Accept-Language", required = true) String lang,
//			@RequestHeader(name = "serviceId", required = true) String serviceId);
	
	public String getPassword(String password);
}
