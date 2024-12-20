package com.ocs.authservice.adapter.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ocs.common.entity.RRmessage;
import com.ocs.common.mapper.ConfigMapper;
import com.ocs.common.repository.JPARRmessageRepository;

@Repository
public class RRRepositoryImpl implements RRmessageRepository {

	private final JPARRmessageRepository jparRmessageRepository;
	
    public RRRepositoryImpl(JPARRmessageRepository jparRmessageRepository, ConfigMapper configMapper) {
        this.jparRmessageRepository = jparRmessageRepository;
    }

	@Override
	public List<RRmessage> findByChannelIdAndCategoryCodeAndRequestDateBetweenOrderByDateCreatedDesc(String channelId,
			String categoryCode, String startDate, String endDate) {
		// TODO Auto-generated method stub
		return jparRmessageRepository.findByChannelIdAndCategoryCodeAndRequestDateBetweenOrderByDateCreatedDesc(channelId, categoryCode, startDate, endDate)
				;
	}

	@Override
	public Integer findCountByChannelIdAndCategoryCodeAndResponseCodeAndRequestDateBetweenOrderByDateCreatedDesc(
			String channelId, String categoryCode, String startDate, String endDate, String responseCode) {
		// TODO Auto-generated method stub
		return jparRmessageRepository.findCountByChannelIdAndCategoryCodeAndResponseCodeAndRequestDateBetweenOrderByDateCreatedDesc(channelId, categoryCode, startDate, endDate, responseCode);
	}

	@Override
	public List<RRmessage> findByChannelIdAndRequestDateBetweenOrderByDateCreatedDesc(String channelId,
			String startDate, String endDate) {
		// TODO Auto-generated method stub
		return jparRmessageRepository.findByChannelIdAndRequestDateBetweenOrderByDateCreatedDesc(channelId, startDate, endDate)
				;
	}

	@Override
	public String getAuditRequired(String categoryCode) {
		// TODO Auto-generated method stub
		return jparRmessageRepository.getAuditRequired(categoryCode);
	}

 
}