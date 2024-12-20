package com.ocs.authservice.adapter.repository;

import com.ocs.authservice.core.entity.NotallowedIP;

public interface NotAllowedIpRepository {

	NotallowedIP findByIp(String ip);

}
