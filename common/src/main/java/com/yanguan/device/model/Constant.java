package com.yanguan.device.model;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisHash;
import redis.clients.jedis.JedisPool;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-17 16:33)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class Constant {
    //常用对象
    public static RedisCacheManager cacheManager;
    public static JedisPool jedisPool;

    //常量字符串
    public static final String SPLIT_CHAR = ",";

    //数据库表名
    public static final String DEVICE_TABLE_REF = "YG_DEVICE_REF";
    public static final String DEVICE_TABLE_INFO = "YG_DEVICE_INFO";

    //缓存Hash域
    public static final String Device_Is_OnLine="Is_OnLine";
    public static final String HeartBeat="HeartBeat";
    public static final String Device_Cmd_Cache = "Device_Cmd_Cache";

    //app推送消息类型
    public static final int Push_OnLine=10;     //在线
    public static final int Push_OffineLine=11;     //离线
    public static final int Push_Status=12;     //设备状态
    public static final int Push_Rmd_Roll=1;     //滚动报警
    public static final int Push_Rmd_Shake=2;     //滚动报警
    public static final int Push_Rmd_Outage=3;     //断电报警
    public static final int Push_Rmd_Rail=5;     //断电报警
    public static final int Push_Cmd_Success=0;     //命令执行成功
    public static final int Push_Cmd_Fail=1;     //命令执行失败

}
