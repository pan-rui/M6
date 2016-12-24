package com.yanguan.device.cmd;

import io.netty.channel.Channel;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 13:08)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public interface IProcess{

    void process(Channel channel, Map<String, Object> data);
}
