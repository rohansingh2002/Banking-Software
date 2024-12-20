package com.ocs.authservice.adapter.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.ocs.common.entity.RRmessage;

public interface RRmessageRepository {
	
	List<RRmessage> findByChannelIdAndCategoryCodeAndRequestDateBetweenOrderByDateCreatedDesc(
			String channelId,  String categoryCode,
			String startDate, String endDate);
	
	Integer findCountByChannelIdAndCategoryCodeAndResponseCodeAndRequestDateBetweenOrderByDateCreatedDesc(
			 String channelId,  String categoryCode,
			 String startDate,  String endDate,  String responseCode);

	List<RRmessage> findByChannelIdAndRequestDateBetweenOrderByDateCreatedDesc(@Param("channelId") String channelId,
			 String startDate,  String endDate);

	String getAuditRequired( String categoryCode);
	

}
