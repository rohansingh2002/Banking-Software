package com.ocs.authservice.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocs.authservice.util.RSAUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/password")
public class PasswordController  {

	
	@Autowired
	HttpServletRequest request;

	public PasswordController() {
	}

		@GetMapping("/")
		public String getPassword(@RequestBody String password){
	
			try {
				return RSAUtil.encrypt(password);
			} catch (Exception e) {
				System.out.println(e);
				// TODO Auto-generated catch block
				return "";	
			}
		}
}