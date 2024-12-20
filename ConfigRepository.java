package com.ocs.authservice.adapter.repository;

import java.util.List;
import java.util.Optional;

import com.ocs.common.dto.ConfigDto;
import com.ocs.common.entity.Config;

public interface ConfigRepository {
 
	Optional<ConfigDto> findByUnit_IdAndChannel_ChannelAndKeyAndStatus(String unit, String channel, String key, String status);
	
	ConfigDto findByUnit_IdAndChannel_ChannelAndKey(String unit, String channel, String otpEnabled);


}