package com.wtu.parking.service;

import com.wtu.parking.entity.Manager;

public interface IManagerService {
    Manager findByMId(Integer mId);

    Manager findByIdentityId(String identityId);

    int insert(Manager manager);

    int insertMonthlyUser(int uId,int effective);
}
