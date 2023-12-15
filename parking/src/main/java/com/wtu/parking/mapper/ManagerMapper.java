package com.wtu.parking.mapper;

import com.wtu.parking.entity.Manager;
import org.springframework.stereotype.Repository;

public interface ManagerMapper {
    Manager findByMId(Integer mId);
    Manager findByIdentityId(String identityId);

    int insert(Manager manager);

    Integer getLastId();
}
