package com.yanguan.device.encoder;

import com.yanguan.device.model.ProtocolEnum;
import com.yanguan.device.util.ByteConvertUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description: ${Description}
 * @Create: 潘锐 (2016-11-27 12:17)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class M6Encoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf) throws Exception {
        String[] strs = s.split(",");
        int index=0;
        ProtocolEnum protocol=null;
        for (String str:strs) {
            if(index==0) protocol = ProtocolEnum.valueOfType(Integer.parseInt(str));
            String[] strArry=null;
            if (!str.contains(".")||str.matches("^\\d+\\.\\d+$")) {
                strArry = new String[]{str};                //float 类型
            } else strArry= str.split("\\.");
            for (String ss : strArry) {
                System.out.println("-------"+ss);
                int length=protocol.getSendDataLeng()[index];
                if(length>200){   //char类型
                    byteBuf.writeBytes(ByteConvertUtil.charArryToByteArry(ss));
                }else if(length>100){
                    byteBuf.writeBytes(ByteConvertUtil.floatToByteArray(Float.parseFloat(ss)));
                }else {
                    byteBuf.writeBytes(ByteConvertUtil.objToByteArry(ss, length));
                }
                index++;
            }
        }
    }
}
