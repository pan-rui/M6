
package com.yanguan.device.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceMapper {
    Map<String, Object> queryDeviceCity(@Param("mcc") String mcc, @Param("mnc") String mnc,@Param("lac") String lac,@Param("cell")String cell);
    Map<String, Object> queryDeviceProvince(@Param("mcc") String mcc, @Param("mnc") String mnc,@Param("lac") String lac);
    List<Map<String, Object>> queryAllCity();
    List<Map<String,Object>> queryAllProvince();
}