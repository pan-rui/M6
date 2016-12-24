
package com.yanguan.device.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author [*田园间*]   liaoxuqian@hotmail.com
 * @datetime 2015-8-21  10:51:17
 * @since version 1.0
 */
@Repository
public interface DeviceMapper {
    @Cacheable(value = "schedu", keyGenerator = "myKeyGenerator")
    List<Map<String, Object>> queryByProsInTab(@Param("params") Map<String, Object> params, @Param("tableName") String tableName);

    List<Map<String, Object>> queryAllInTab(@Param("tableName") String tableName);

    @CacheEvict(value = "schedu", keyGenerator = "myKeyGenerator")
    int updateByProsInTab(@Param("params") Map<String, Object> params,@Param("tableName") String tableName);

    @CacheEvict(value = "schedu", keyGenerator = "myKeyGenerator")
    int deleteByProsInTab(@Param("params") Map<String, Object> params,@Param("tableName") String tableName);

    void insertByProsInTab(@Param("params") Map<String, Object> params);

}