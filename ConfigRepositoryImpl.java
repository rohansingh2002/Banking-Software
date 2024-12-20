package com.ocs.authservice.adapter.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocs.authservice.constant.AppConstant;
import com.ocs.common.dto.ConfigDto;
import com.ocs.common.entity.Config;
import com.ocs.common.mapper.ConfigMapper;
import com.ocs.common.repository.JPAConfigRepository;

@Service
public class ConfigRepositoryImpl implements ConfigRepository {

	private final JPAConfigRepository jpaConfigRepository;
	
	private ConfigMapper configMapper;
	
	/* public ConfigRepositoryImpl(ConfigMapper configMapper)
	{
		this.jpaConfigRepository = null;
		this.configMapper = configMapper; 
	}
	
	public ConfigRepositoryImpl(JPAConfigRepository jpaConfigRepository)
	{
		this.jpaConfigRepository = jpaConfigRepository; 
	} */

    public ConfigRepositoryImpl(JPAConfigRepository jpaConfigRepository, ConfigMapper configMapper) {
        this.jpaConfigRepository = jpaConfigRepository;
        this.configMapper = configMapper;
    }

    @Override
	public Optional<ConfigDto> findByUnit_IdAndChannel_ChannelAndKeyAndStatus(String unit, String channel, String key,
			String status) {
		return Optional.of(configMapper.configToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKeyAndStatus(unit, channel, key, status)));
	}


	public Config getConfig(final String unit, final String channel, final String key, final String status) {
		return jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKeyAndStatus(unit, channel, key, status);
	}

	public List<Config> getConfigList(final String unit, final String channel, final String key, final String status) {
		List<Config> configList = jpaConfigRepository
				.findByUnit_IdAndChannel_ChannelAndKeyAndStatusOrderByDescription(unit, channel, key, status);
		return configList;
	}

	public Map<String, String> getConfigDetail(String unit, String channel, final Map<String, String> confMap) {
		Map<String, String> configEnableOrDis = null;
		Config config = null;
		try {
			configEnableOrDis = new HashMap<String, String>();
			if (confMap != null) {
				String key = new StringBuffer().append(confMap.get("screenId")).append("_")
						.append(confMap.get("configKey")).toString();
				config = jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKeyAndStatus(unit, channel, key,
						AppConstant.DEFAULT_STATUS);
				if (config != null) {
					configEnableOrDis.put("configValue", config.getValue() != null ? config.getValue() : "");
				} else {
				}
			}
		} catch (Exception e) {

		}
		return configEnableOrDis;
	}

	public Map<String, String> getConfigMap(String key, String unit, String channel) {
		Map<String, String> configMap = null;
		configMap = jpaConfigRepository.findByKeyStartsWithAndUnit_IdAndStatusAndChannel_Channel(key, unit,
				AppConstant.DEFAULT_STATUS, channel).stream()
				.collect(Collectors.toMap(Config::getKey, Config::getValue));
		return configMap;
	}

	public String getConfigValueByKey(String otpMaxWrongAttempts) {
		return jpaConfigRepository.findByKey(otpMaxWrongAttempts);
	}
	
	public ConfigDto findByUnit_IdAndChannel_ChannelAndKey(String unit, String channel, String otpEnabled) {
		return (configMapper.configToDto(jpaConfigRepository.findByUnit_IdAndChannel_ChannelAndKey(unit, channel, otpEnabled)));
	}
}