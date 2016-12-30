package com.yanguan.device.mq;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-12-21 12:16)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class AppPush  {
    private static final Logger logger = Logger.getLogger(AppPush.class);
    private JmsTemplate jmsQueueTemplate;
    public void setJmsQueueTemplate(JmsTemplate jmsQueueTemplate) {
        this.jmsQueueTemplate = jmsQueueTemplate;
    }

    public void sendMessage(int devId, int msgType, long rmdTime,Object data) {
        jmsQueueTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage=session.createMapMessage();
                mapMessage.setInt("DeviceID",devId);
                mapMessage.setInt("msgType",msgType);
                mapMessage.setLong("rmdTime",rmdTime);
                mapMessage.setObject("data", data);
                return mapMessage;
            }
        });
//        logger.info("push MQ is success on the DeviceID:"+devId);     //Test....
    }

}
