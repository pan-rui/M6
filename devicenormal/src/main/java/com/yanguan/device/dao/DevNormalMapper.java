package com.yanguan.device.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-19 16:38)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Repository
public interface DevNormalMapper {
//    @CacheEvict(value = "m6", keyGenerator = "myKeyGenerator")
    int cardActive(@Param("iccid") String iccid);

    int insertOrUpdateMileage(@Param("mileage") double mileage, @Param("devId") double devId);
}
