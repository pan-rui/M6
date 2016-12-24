package com.yanguan.device.util;

import com.yanguan.device.model.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-28 16:50)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Component
public class MyKeyGenerator implements KeyGenerator {
    @Autowired
    public RedisCacheManager cacheManager;
    @Autowired
    public JedisPool jedisPool;
    @Override
    public Object generate(Object o, Method method, Object... objects) {
        if(objects[0] instanceof Map && method.getName().endsWith("InTab")){
            Map<String, Object> dataMap = (Map<String, Object>) objects[0];
//            Collection values=dataMap.values();
//            for (Object obj: dataMap.values())
                return objects[1] + "~" + dataMap.values().toArray()[dataMap.size()-1];
        }
        return null;
    }

    @PostConstruct
    public void setProperties() {
        Constant.cacheManager=cacheManager;
        Constant.jedisPool=jedisPool;
    }
}
