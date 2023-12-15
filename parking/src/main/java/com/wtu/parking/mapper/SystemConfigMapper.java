package com.wtu.parking.mapper;

public interface SystemConfigMapper {
    Integer getPricePerH();
    Integer getMaxPricePerD();

    Integer getMonthlyPrice();

    int getTotalPosition();
    int setTotalPosition(int value);

    int getUsedCount();

    int setUsedCount(int value);



}
